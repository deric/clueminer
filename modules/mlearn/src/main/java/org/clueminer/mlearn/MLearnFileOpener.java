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
package org.clueminer.mlearn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.explorer.ExplorerTopComponent;
import org.clueminer.gui.msg.NotifyUtil;
import org.clueminer.importer.ImportController;
import org.clueminer.importer.ImportTask;
import org.clueminer.io.importer.api.Container;
import org.clueminer.openfile.OpenFileImpl;
import org.clueminer.project.ProjectControllerImpl;
import org.clueminer.project.ProjectInformationImpl;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.ProjectControllerUI;
import org.clueminer.project.api.Workspace;
import org.clueminer.spi.ImportListener;
import org.clueminer.spi.Importer;
import org.clueminer.spi.ImporterUI;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 */
@org.openide.util.lookup.ServiceProvider(service = org.clueminer.openfile.OpenFileImpl.class, position = 90)
public class MLearnFileOpener implements OpenFileImpl, ImportListener {

    private ImportTask importTask;
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);
    private final ImportController controller;
    private static final Logger LOG = LoggerFactory.getLogger(MLearnFileOpener.class);

    public MLearnFileOpener() {
        controller = Lookup.getDefault().lookup(ImportController.class);
    }

    /**
     * Return true is file seems to be in format which is supported by this
     * package
     *
     * @param f
     * @return boolean
     * @throws java.io.FileNotFoundException
     */
    protected boolean isFileSupported(File f) throws FileNotFoundException, IOException {
        return controller.isFileSupported(f) || controller.isAccepting(f);
    }

    @Override
    public boolean open(FileObject fileObject) {
        //ProgressHandle ph = ProgressHandle.createHandle("Opening file " + importer.getFile().getName());
        //importer.setProgressHandle(ph);
        File f = FileUtil.toFile(fileObject);
        try {
            if (isFileSupported(f)) {
                importTask = controller.preload(fileObject);
                if (importTask != null) {
                    importTask.addListener(this);
                    RP.post(importTask);
                } else {
                    LOG.error("failed to create an import task");
                }

                //ImporterUI ui = controller.getUI(im);
                //importer = new MLearnImporter(f);
                //openDataFile(importer);
                return true;
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    protected String getExtension(String filename) {
        String extension = "";
        int i = filename.lastIndexOf('.');
        int p = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));

        if (i > p) {
            extension = filename.substring(i + 1);
        }

        return extension;
    }

    protected String getTitle(String filename) {
        String title = filename;
        int pos = filename.lastIndexOf('.');
        if (pos > -1) {
            title = filename.substring(0, pos).trim();
        }
        return title;
    }

    @Override
    public void importerChanged(Importer importer, ImporterUI importerUI) {
        //not used
        LOG.info("importer changed {}", importer.getName());
    }

    @Override
    public void dataLoaded() {
        LOG.info("data loaded");
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                Project project;
                Container container = importTask.getContainer();
                if (container != null) {
                    ProjectControllerImpl pc = Lookup.getDefault().lookup(ProjectControllerImpl.class);
                    ProjectControllerUI pcui = Lookup.getDefault().lookup(ProjectControllerUI.class);
                    Dataset<? extends Instance> dataset = container.getDataset();
                    if (dataset == null) {
                        LOG.info("loading dataset failed");
                    } else {
                        Workspace workspace = pc.getCurrentWorkspace();
                        if (workspace != null) {
                            workspace.add(dataset);  //add plate to project's lookup
                        }
                        String filename = container.getSource();
                        dataset.setName(filename);
                        if (filename == null) {
                            throw new RuntimeException("no source was given");
                        }
                        project = pcui.newProject();
                        ProjectInformationImpl pinfo = project.getLookup().lookup(ProjectInformationImpl.class);
                        pinfo.setName(dataset.getName());
                        pinfo.setFile(new File(filename));
                        project.add(dataset);
                        pc.openProject(project);

                        ExplorerTopComponent explorer = new ExplorerTopComponent();
                        explorer.setDataset(dataset);
                        //explorer.setProject(project);
                        explorer.setDisplayName(getTitle(filename));
                        explorer.open();
                        explorer.requestActive();

                        //     DataPreprocessing preprocess = new DataPreprocessing(plate, tc);
                        //     preprocess.start();
                    }
                } else {
                    NotifyUtil.error("Error", "missing loader", false);
                }
            }
        });
    }
}
