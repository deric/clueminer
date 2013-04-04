package org.clueminer.branding;

import javax.swing.JOptionPane;
import org.clueminer.project.api.ProjectController;
import org.clueminer.project.api.ProjectControllerUI;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall {

    private static final long serialVersionUID = -8455460980390347040L;

    @Override
    public void restored() {
        initClueminer();
    }

    private void initClueminer() {
        final ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                pc.startup();
                DragNDropFrameAdapter.register();
            }
        });

        //Memory Starvation Manager
        if (System.getProperty("org.clueminer.MemoryStarvationManager.enabled", "true").equals("true")) {
            MemoryStarvationManager memoryStarvationManager = new MemoryStarvationManager();
            memoryStarvationManager.startup();
        }
    }

    @Override
    public boolean closing() {
        if (Lookup.getDefault().lookup(ProjectController.class).getCurrentProject() == null) {
            //Close directly if no project open
            return true;
        }

        int option = JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(), NbBundle.getMessage(Installer.class, "CloseConfirmation.message"), NbBundle.getMessage(Installer.class, "CloseConfirmation.message"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            Lookup.getDefault().lookup(ProjectControllerUI.class).saveProject();
        } else if (option == JOptionPane.CANCEL_OPTION) {
            return false;//Exit canceled
        }
        Lookup.getDefault().lookup(ProjectController.class).closeCurrentProject();
        return true;
    }
}
