package org.clueminer.branding;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.TransferHandler;
import org.clueminer.importer.ImportController;
import org.clueminer.project.api.ProjectControllerUI;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 * @see <a
 * href="http://java.sun.com/docs/books/tutorial/uiswing/dnd/toplevel.html">Top-Level
 * Drop Swing tutorial</a>
 *
 * @author Tomas Barton
 */
public class DragNDropFrameAdapter {

    public static void register() {
        JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
        frame.setTransferHandler(new TransferHandler() {
            private static final long serialVersionUID = -8934882956893995526L;

            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    return false;
                }
                //Due to bug 6759788 - http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6759788
                //Impossible to get data here and look if compatible format
                return true;
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }
                try {
                    List<File> data = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    File file = (File) data.get(0);
                    FileObject fileObject = (FileObject) FileUtil.toFileObject(file);
                    if (!file.exists()) {
                        return false;
                    }

                    ProjectControllerUI pc = Lookup.getDefault().lookup(ProjectControllerUI.class);
                    if (pc.isFileSupported(file)) {
                        try {
                            pc.openProject(file);
                        } catch (Exception ew) {
                            NotifyDescriptor.Message msg = new NotifyDescriptor.Message(NbBundle.getMessage(DragNDropFrameAdapter.class, "DragNDropFrameAdapter.openClueminerError"), NotifyDescriptor.WARNING_MESSAGE);
                            DialogDisplayer.getDefault().notify(msg);
                        }
                    } else {
                        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
                        if (importController.isFileSupported(file)) {
                            importController.preload(fileObject);
                        } else {
                            return false;
                        }
                    }

                    return true;
                } catch (UnsupportedFlavorException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                return false;
            }
        });
    }
}
