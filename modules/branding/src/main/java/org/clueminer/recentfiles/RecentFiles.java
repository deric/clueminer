package org.clueminer.recentfiles;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.clueminer.importer.ImportController;
import org.clueminer.project.api.MostRecentFiles;
import org.clueminer.project.api.ProjectControllerUI;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 *
 * @author Tomas Barton
 */
public class RecentFiles extends CallableSystemAction {

    private static final String CLUEMINER_EXTENSION = "clm";

    @Override
    public void performAction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return "recentfiles";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public JMenuItem getMenuPresenter() {
        JMenu menu = new JMenu(NbBundle.getMessage(RecentFiles.class, "CTL_OpenRecentFiles"));

        MostRecentFiles mru = Lookup.getDefault().lookup(MostRecentFiles.class);
        for (String filePath : mru.getMRUFileList()) {
            final File file = new File(filePath);
            if (file.exists()) {
                final String fileName = file.getName();
                JMenuItem menuItem = new JMenuItem(new AbstractAction(fileName) {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FileObject fileObject = FileUtil.toFileObject(file);
                        if (fileObject.hasExt(CLUEMINER_EXTENSION)) {
                            ProjectControllerUI pc = Lookup.getDefault().lookup(ProjectControllerUI.class);
                            try {
                                pc.openProject(file);
                            } catch (Exception ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        } else {
                            ImportController importController = Lookup.getDefault().lookup(ImportController.class);
                            if (importController.isFileSupported(file)) {
                                importController.preload(fileObject);
                            }
                        }
                    }
                });
                menu.add(menuItem);
            }
        }
        return menu;
    }

}
