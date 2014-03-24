package org.clueminer.importer.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.clueminer.importer.ImportController;
import org.clueminer.importer.ImportControllerUI;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.Report;
import org.clueminer.longtask.LongTaskErrorHandler;
import org.clueminer.longtask.LongTaskExecutor;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.processor.spi.Processor;
import org.clueminer.processor.spi.ProcessorUI;
import org.clueminer.project.api.MostRecentFiles;
import org.clueminer.project.api.ProjectController;
import org.clueminer.project.api.ProjectControllerUI;
import org.clueminer.project.api.Workspace;
import org.clueminer.spi.FileImporter;
import org.clueminer.spi.ImporterUI;
import org.netbeans.validation.api.ui.swing.ValidationPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Barton
 */
public class ImportControllerUIImpl implements ImportControllerUI {

    private final ImportController controller;
    private final LongTaskErrorHandler errorHandler;
    private final LongTaskExecutor executor;

    public ImportControllerUIImpl() {
        controller = Lookup.getDefault().lookup(ImportController.class);
        errorHandler = new LongTaskErrorHandler() {
            @Override
            public void fatalError(Throwable t) {
                if (t instanceof OutOfMemoryError) {
                    return;
                }

                Exceptions.printStackTrace(t);
            }
        };
        executor = new LongTaskExecutor(true, "Importer", 10);
    }

    @Override
    public void importFile(FileObject fileObject) {
        try {
            final FileImporter importer = controller.getFileImporter(FileUtil.toFile(fileObject));
            if (importer == null) {
                NotifyDescriptor.Message msg = new NotifyDescriptor.Message(NbBundle.getMessage(getClass(), "ImportControllerUI.error_no_matching_file_importer"), NotifyDescriptor.WARNING_MESSAGE);
                DialogDisplayer.getDefault().notify(msg);
                return;
            }

            //MRU
            MostRecentFiles mostRecentFiles = Lookup.getDefault().lookup(MostRecentFiles.class);
            mostRecentFiles.addFile(fileObject.getPath());

            ImporterUI ui = controller.getUI(importer);
            if (ui != null) {
                String title = NbBundle.getMessage(ImportControllerUIImpl.class, "ImportControllerUI.file.ui.dialog.title", ui.getDisplayName());
                JPanel panel = ui.getPanel();
                ui.setup(importer);

                final DialogDescriptor dd = new DialogDescriptor(panel, title);
                if (panel instanceof ValidationPanel) {
                    ValidationPanel vp = (ValidationPanel) panel;
                    vp.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            dd.setValid(!((ValidationPanel) e.getSource()).isFatalProblem());
                        }
                    });
                }

                Object result = DialogDisplayer.getDefault().notify(dd);
                if (!result.equals(NotifyDescriptor.OK_OPTION)) {
                    ui.unsetup(false);
                    return;
                }
                ui.unsetup(true);
            }

            LongTask task = null;
            if (importer instanceof LongTask) {
                task = (LongTask) importer;
            }

            //Execute task
            fileObject = getArchivedFile(fileObject);
            final String containerSource = fileObject.getNameExt();
            final InputStream stream = fileObject.getInputStream();
            String taskName = NbBundle.getMessage(ImportControllerUIImpl.class, "DesktopImportControllerUI.taskName", containerSource);
            executor.execute(task, new Runnable() {
                @Override
                public void run() {
                    try {
                        Container container = controller.importFile(stream, importer);
                        if (container != null) {
                            container.setSource(containerSource);
                            finishImport(container);
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }, taskName, errorHandler);
            if (fileObject.getPath().startsWith(System.getProperty("java.io.tmpdir"))) {
                try {
                    fileObject.delete();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger("").log(Level.WARNING, "", ex);
        }
    }

    private void finishImport(Container container) {
        if (container.verify()) {
            Report report = container.getReport();

            //Report panel
            ReportPanel reportPanel = new ReportPanel();
            reportPanel.setData(report, container);
            DialogDescriptor dd = new DialogDescriptor(reportPanel, NbBundle.getMessage(ImportControllerUIImpl.class, "ReportPanel.title"));
            if (!DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
                reportPanel.destroy();
                return;
            }
            reportPanel.destroy();
            final Processor processor = reportPanel.getProcessor();

            //Project
            Workspace workspace = null;
            ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
            ProjectControllerUI pcui = Lookup.getDefault().lookup(ProjectControllerUI.class);
            if (pc.getCurrentProject() == null) {
                pcui.newProject();
                workspace = pc.getCurrentWorkspace();
            }

            //Process
            final ProcessorUI pui = getProcessorUI(processor);
            final ValidResult validResult = new ValidResult();
            if (pui != null) {

                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            String title = NbBundle.getMessage(ImportControllerUIImpl.class, "ImportControllerUIImpl.processor.ui.dialog.title");
                            JPanel panel = pui.getPanel();
                            pui.setup(processor);
                            final DialogDescriptor dd2 = new DialogDescriptor(panel, title);
                            if (panel instanceof ValidationPanel) {
                                ValidationPanel vp = (ValidationPanel) panel;
                                vp.addChangeListener(new ChangeListener() {
                                    @Override
                                    public void stateChanged(ChangeEvent e) {
                                        dd2.setValid(!((ValidationPanel) e.getSource()).isFatalProblem());
                                    }
                                });
                                dd2.setValid(!vp.isFatalProblem());
                            }
                            Object result = DialogDisplayer.getDefault().notify(dd2);
                            if (result.equals(NotifyDescriptor.CANCEL_OPTION) || result.equals(NotifyDescriptor.CLOSED_OPTION)) {
                                validResult.setResult(false);
                            } else {
                                pui.unsetup(); //true
                                validResult.setResult(true);
                            }
                        }
                    });
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (InvocationTargetException ex) {
                    Exceptions.printStackTrace(ex);
                }

            }
            if (validResult.isResult()) {
                controller.process(container, processor, workspace);

                //StatusLine notify
                String source = container.getSource();
                if (source.isEmpty()) {
                    source = NbBundle.getMessage(ImportControllerUIImpl.class, "ImportControllerUIImpl.status.importSuccess.default");
                }
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(ImportControllerUIImpl.class, "ImportControllerUIImpl.status.importSuccess", source));
            }
        } else {
            System.err.println("Bad container");
        }
    }

    private static class ValidResult {

        private boolean result = true;

        public void setResult(boolean result) {
            this.result = result;
        }

        public boolean isResult() {
            return result;
        }
    }

    private FileObject getArchivedFile(FileObject fileObject) {
        // ZIP and JAR archives
        if (FileUtil.isArchiveFile(fileObject)) {
            try {
                fileObject = FileUtil.getArchiveRoot(fileObject).getChildren()[0];
            } catch (Exception e) {
                throw new RuntimeException("The archive can't be opened, be sure it has no password and contains a single file, without folders");
            }
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

                    File tempFile = null;
                    if (fileExt1.equalsIgnoreCase("tar")) {
                        String fname = fileObject.getName().replaceAll("\\.tar$", "");
                        fname = fname.replace(fileExt2, "");
                        tempFile = File.createTempFile(fname, "." + fileExt2);
                        // Untar & unzip
                        if (isGz) {
                            tempFile = getGzFile(fileObject, tempFile, true);
                        } else {
                            tempFile = getBzipFile(fileObject, tempFile, true);
                        }
                    } else {
                        String fname = fileObject.getName();
                        fname = fname.replace(fileExt1, "");
                        tempFile = File.createTempFile(fname, "." + fileExt1);
                        // Unzip
                        if (isGz) {
                            tempFile = getGzFile(fileObject, tempFile, false);
                        } else {
                            tempFile = getBzipFile(fileObject, tempFile, false);
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

    @Override
    public void importStream(InputStream stream, String importerName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void importFile(Reader reader, String importerName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ImportController getImportController() {
        return controller;
    }

    private ProcessorUI getProcessorUI(Processor processor) {
        for (ProcessorUI pui : Lookup.getDefault().lookupAll(ProcessorUI.class)) {
            if (pui.isUIFoProcessor(processor)) {
                return pui;
            }
        }
        return null;
    }

    /**
     * Uncompress a Bzip2 file.
     */
    private static File getBzipFile(FileObject in, File out, boolean isTar) throws IOException {

        // Stream buffer
        final int BUFF_SIZE = 8192;
        final byte[] buffer = new byte[BUFF_SIZE];

        BZip2CompressorInputStream inputStream = null;
        FileOutputStream outStream = null;

        try {
            FileInputStream is = new FileInputStream(in.getPath());
            inputStream = new BZip2CompressorInputStream(is);
            outStream = new FileOutputStream(out.getAbsolutePath());

            if (isTar) {
                // Read Tar header
                int remainingBytes = readTarHeader(inputStream);

                // Read content
                ByteBuffer bb = ByteBuffer.allocateDirect(4 * BUFF_SIZE);
                byte[] tmpCache = new byte[BUFF_SIZE];
                int nRead, nGet;
                while ((nRead = inputStream.read(tmpCache)) != -1) {
                    if (nRead == 0) {
                        continue;
                    }
                    bb.put(tmpCache);
                    bb.position(0);
                    bb.limit(nRead);
                    while (bb.hasRemaining() && remainingBytes > 0) {
                        nGet = Math.min(bb.remaining(), BUFF_SIZE);
                        nGet = Math.min(nGet, remainingBytes);
                        bb.get(buffer, 0, nGet);
                        outStream.write(buffer, 0, nGet);
                        remainingBytes -= nGet;
                    }
                    bb.clear();
                }
            } else {
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    outStream.write(buffer, 0, len);
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outStream != null) {
                outStream.close();
            }
        }

        return out;
    }

    /**
     * Uncompress a GZIP file.
     */
    private static File getGzFile(FileObject in, File out, boolean isTar) throws IOException {

        // Stream buffer
        final int BUFF_SIZE = 8192;
        final byte[] buffer = new byte[BUFF_SIZE];

        GZIPInputStream inputStream = null;
        FileOutputStream outStream = null;

        try {
            inputStream = new GZIPInputStream(new FileInputStream(in.getPath()));
            outStream = new FileOutputStream(out);

            if (isTar) {
                // Read Tar header
                int remainingBytes = readTarHeader(inputStream);

                // Read content
                ByteBuffer bb = ByteBuffer.allocateDirect(4 * BUFF_SIZE);
                byte[] tmpCache = new byte[BUFF_SIZE];
                int nRead, nGet;
                while ((nRead = inputStream.read(tmpCache)) != -1) {
                    if (nRead == 0) {
                        continue;
                    }
                    bb.put(tmpCache);
                    bb.position(0);
                    bb.limit(nRead);
                    while (bb.hasRemaining() && remainingBytes > 0) {
                        nGet = Math.min(bb.remaining(), BUFF_SIZE);
                        nGet = Math.min(nGet, remainingBytes);
                        bb.get(buffer, 0, nGet);
                        outStream.write(buffer, 0, nGet);
                        remainingBytes -= nGet;
                    }
                    bb.clear();
                }
            } else {
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    outStream.write(buffer, 0, len);
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outStream != null) {
                outStream.close();
            }
        }

        return out;
    }

    private static int readTarHeader(InputStream inputStream) throws IOException {
        // Tar bytes
        final int FILE_SIZE_OFFSET = 124;
        final int FILE_SIZE_LENGTH = 12;
        final int HEADER_LENGTH = 512;

        ignoreBytes(inputStream, FILE_SIZE_OFFSET);
        String fileSizeLengthOctalString = readString(inputStream, FILE_SIZE_LENGTH).trim();
        final int fileSize = Integer.parseInt(fileSizeLengthOctalString, 8);

        ignoreBytes(inputStream, HEADER_LENGTH - (FILE_SIZE_OFFSET + FILE_SIZE_LENGTH));

        return fileSize;
    }

    private static void ignoreBytes(InputStream inputStream, int numberOfBytes) throws IOException {
        for (int counter = 0; counter < numberOfBytes; counter++) {
            inputStream.read();
        }
    }

    private static String readString(InputStream inputStream, int numberOfBytes) throws IOException {
        return new String(readBytes(inputStream, numberOfBytes));
    }

    private static byte[] readBytes(InputStream inputStream, int numberOfBytes) throws IOException {
        byte[] readBytes = new byte[numberOfBytes];
        inputStream.read(readBytes);

        return readBytes;
    }

}
