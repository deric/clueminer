package org.clueminer.importer.gui;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.clueminer.importer.ImportController;
import org.clueminer.importer.ImportControllerUI;
import org.clueminer.longtask.LongTaskErrorHandler;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.project.api.MostRecentFiles;
import org.clueminer.spi.FileImporter;
import org.clueminer.spi.ImporterUI;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
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

}
