/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.importer.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.importer.FileImporterFactory;
import org.clueminer.importer.ImportController;
import org.clueminer.importer.ImportTask;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.Database;
import org.clueminer.processor.spi.Processor;
import org.clueminer.project.api.MostRecentFiles;
import org.clueminer.project.api.Workspace;
import org.clueminer.spi.DatabaseImporter;
import org.clueminer.spi.FileImporter;
import org.clueminer.spi.Importer;
import org.clueminer.spi.ImporterUI;
import org.clueminer.spi.ImporterWizardUI;
import org.clueminer.types.FileType;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ImportController.class)
public class ImportControllerImpl implements ImportController {

    private final List<FileImporter> fileImporters;
    private final ImporterUI[] uis;
    private MimeHelper helper;
    private static final Logger LOGGER = Logger.getLogger(ImportControllerImpl.class.getName());
    private final HashMap<String, Container> containers;

    public ImportControllerImpl() {
        this.containers = new HashMap<>();
        fileImporters = FileImporterFactory.getInstance().getAll();
        helper = new MimeHelper();
        //Get UIS
        uis = Lookup.getDefault().lookupAll(ImporterUI.class).toArray(new ImporterUI[0]);
        LOGGER.info("creating new ImportController");
    }

    @Override
    public ImportTask preload(FileObject fileObject) {
        try {
            fileObject = getArchivedFile(fileObject);   //Unzip and return content file
            final FileImporter importer = getFileImporter(fileObject);
            if (importer == null) {
                NotifyDescriptor.Message msg = new NotifyDescriptor.Message(NbBundle.getMessage(getClass(), "ImportController.error_no_matching_file_importer"), NotifyDescriptor.WARNING_MESSAGE);
                DialogDisplayer.getDefault().notify(msg);
                return null;
            } else {
                LOGGER.info("detected importer " + importer.getName());
            }

            //MRU
            MostRecentFiles mostRecentFiles = Lookup.getDefault().lookup(MostRecentFiles.class);
            mostRecentFiles.addFile(fileObject.getPath());

            return new ImportTaskImpl(importer, fileObject, this);
        } catch (MissingResourceException ex) {
            Logger.getLogger("").log(Level.WARNING, "", ex);
        }
        return null;
    }

    @Override
    public ImportTask preload(InputStream stream, String importerName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ImportTask preload(Reader reader, String importerName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Container importFile(File file) throws FileNotFoundException {
        FileObject fileObject = FileUtil.toFileObject(file);
        if (fileObject != null) {
            fileObject = getArchivedFile(fileObject);   //Unzip and return content file
            LOGGER.log(Level.INFO, "searching importer for {0}, ext: {1}", new Object[]{fileObject.getName(), fileObject.getExt()});
            FileImporter importer = getMatchingImporter(fileObject);
            if (importer == null) {
                LOGGER.info("no importer found by extension");
                //try to find importer by MIME type
                importer = getMatchingImporter(helper.detectMIME(fileObject));
            } else {
                LOGGER.log(Level.INFO, "using importer {0}", importer.getName());
            }
            return importFile(fileObject, fileObject.getInputStream(), importer, false);
        }
        return null;
    }

    @Override
    public Container importFile(FileObject fileObject, FileImporter importer) throws FileNotFoundException {
        if (fileObject != null) {
            fileObject = getArchivedFile(fileObject);   //Unzip and return content file
            if (fileObject != null) {
                Container c = importFile(fileObject, fileObject.getInputStream(), importer, false);
                if (fileObject.getPath().startsWith(System.getProperty("java.io.tmpdir"))) {
                    try {
                        fileObject.delete();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                return c;
            }
        }
        return null;
    }

    /**
     * Tries importing data while collecting problems occurred during parsing input.
     *
     * @param file     text file / archive / binary file etc.
     * @param reader
     * @param importer
     * @param reload
     * @return
     */
    @Override
    public Container importFile(FileObject file, Reader reader, FileImporter importer, boolean reload) {
        //Create Container
        Container container;
        String path = file.getPath();
        LOGGER.log(Level.INFO, "reload {0}, file: {1}", new Object[]{reload, path});
        // unique container for path
        if (containers.containsKey(path)) {
            container = containers.get(path);
        } else {
            LOGGER.log(Level.INFO, "did not find container for {0}, cached containers = {1}", new Object[]{path, containers.size()});
            container = new DraftContainer();
            containers.put(path, container);
            container.setFile(file);
        }

        //container = Lookup.getDefault().lookup(Container.class);
        LOGGER.log(Level.INFO, "importer contr num attr: {0}", container.getAttributeCount());
        LOGGER.log(Level.INFO, "importer contr num inst: {0}", container.getInstanceCount());
        //Report
        // Report report = container.getReport();
        //container.setReport(report);

        try {
            //actual data import - loads data into container
            if (importer.execute(container, reader)) {

                return container;
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

    @Override
    public Container importFile(FileObject file, InputStream stream, FileImporter importer, boolean reload) {
        try {
            Reader reader = ImportUtils.getTextReader(stream);
            return importFile(file, reader, importer, reload);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Finds appropriate file importer.
     *
     * @param file
     * @return
     */
    @Override
    public FileImporter getFileImporter(FileObject fileObject) {
        //try to get importer by an extension
        FileImporter fi = getMatchingImporter(fileObject);
        if (fi == null) {
            //try to find importer by MIME type
            fi = getMatchingImporter(helper.detectMIME(fileObject));
        }
        if (fileObject != null && fi != null) {
            if (fileObject.getPath().startsWith(System.getProperty("java.io.tmpdir"))) {
                try {
                    fileObject.delete();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return fi;
        }
        return null;
    }

    @Override
    public FileImporter getFileImporter(String importerName) {
        FileImporter importer = getMatchingImporter(importerName);
        if (importer != null) {
            return importer;
        }
        return null;
    }

    @Override
    public void process(Container container) {
        Processor processor = Lookup.getDefault().lookup(Processor.class);
        if (processor == null) {
            throw new RuntimeException("Impossible to find Default Processor");
        }
        process(container, processor, null);
    }

    @Override
    public void process(Container container, Processor processor, Workspace workspace) {
        container.closeLoader();
        processor.setContainer(container);
        processor.setWorkspace(workspace);
        processor.process();
    }

    @Override
    public FileType[] getFileTypes() {
        ArrayList<FileType> list = new ArrayList<>();
        for (FileImporter im : fileImporters) {
            list.addAll(Arrays.asList(im.getFileTypes()));
        }
        return list.toArray(new FileType[0]);
    }

    @Override
    public boolean isFileSupported(File file) {
        FileObject fileObject = FileUtil.toFileObject(file);
        for (FileImporter im : fileImporters) {
            if (im.isMatchingImporter(fileObject)) {
                return true;
            }
        }
        return fileObject.getExt().equalsIgnoreCase("zip")
                || fileObject.getExt().equalsIgnoreCase("gz")
                || fileObject.getExt().equalsIgnoreCase("bz2");
    }

    public boolean isAccepting(FileObject file) {
        return isAccepting(FileUtil.toFile(file));
    }

    @Override
    public boolean isAccepting(File file) {
        Collection mimeTypes = helper.detectMIME(file);
        for (FileImporter im : fileImporters) {
            if (im.isAccepting(mimeTypes)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ImporterUI getUI(Importer importer) {
        for (ImporterUI ui : uis) {
            if (ui.isUIForImporter(importer)) {
                return ui;
            }
        }
        return null;
    }

    @Override
    public ImporterWizardUI getWizardUI(Importer importer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Container importDatabase(Database database, DatabaseImporter importer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private FileObject getArchivedFile(FileObject fileObject) {
        if (fileObject == null) {
            return null;
        }
        // ZIP and JAR archives
        if (FileUtil.isArchiveFile(fileObject)) {
            fileObject = FileUtil.getArchiveRoot(fileObject).getChildren()[0];
        } else { // GZ or BZIP2 archives
            boolean isGz = fileObject.getExt().equalsIgnoreCase("gz");
            boolean isBzip = fileObject.getExt().equalsIgnoreCase("bz2");
            if (isGz || isBzip) {
                try {
                    String[] splittedFileName = fileObject.getName().split("\\.");
                    if (splittedFileName.length < 2) {
                        return fileObject;
                    }

                    String fileExt1 = splittedFileName[splittedFileName.length - 1];
                    String fileExt2 = splittedFileName[splittedFileName.length - 2];

                    File tempFile;
                    if (fileExt1.equalsIgnoreCase("tar")) {
                        String fname = fileObject.getName().replaceAll("\\.tar$", "");
                        fname = fname.replace(fileExt2, "");
                        tempFile = File.createTempFile(fname, "." + fileExt2);
                        // Untar & unzip
                        if (isGz) {
                            tempFile = ImportUtils.getGzFile(fileObject, tempFile, true);
                        } else {
                            tempFile = ImportUtils.getBzipFile(fileObject, tempFile, true);
                        }
                    } else {
                        String fname = fileObject.getName();
                        fname = fname.replace(fileExt1, "");
                        tempFile = File.createTempFile(fname, "." + fileExt1);
                        // Unzip
                        if (isGz) {
                            tempFile = ImportUtils.getGzFile(fileObject, tempFile, false);
                        } else {
                            tempFile = ImportUtils.getBzipFile(fileObject, tempFile, false);
                        }
                    }
                    tempFile.deleteOnExit();
                    tempFile = FileUtil.normalizeFile(tempFile);
                    fileObject = FileUtil.toFileObject(tempFile);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return fileObject;
    }

    private FileImporter getMatchingImporter(FileObject fileObject) {
        if (fileObject == null) {
            return null;
        }
        for (FileImporter im : fileImporters) {
            if (im.isMatchingImporter(fileObject)) {
                return im;
            }
        }
        return null;
    }

    protected FileImporter getMatchingImporter(Collection mimeTypes) {
        if (mimeTypes == null || mimeTypes.isEmpty()) {
            return null;
        }
        for (FileImporter im : fileImporters) {
            if (im.isAccepting(mimeTypes)) {
                return im;
            }
        }
        return null;
    }

    private FileImporter getMatchingImporter(String extension) {
        for (FileImporter im : fileImporters) {
            for (FileType ft : im.getFileTypes()) {
                for (String ext : ft.getExtensions()) {
                    if (ext.equalsIgnoreCase(extension)) {
                        return im;
                    }
                }
            }
        }
        return null;
    }

}
