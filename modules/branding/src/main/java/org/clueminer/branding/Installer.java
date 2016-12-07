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
                pc.startup(NbBundle.getMessage(Installer.class, "CTL_AppTitle"));
                DragNDropFrameAdapter.register();
            }
        });

        //Memory Starvation Manager
        if (System.getProperty("org.clueminer.MemoryStarvationManager.enabled", "true").equals("true")) {
            MemoryStarvationManager memoryStarvationManager = new MemoryStarvationManager();
            memoryStarvationManager.startup();
        }

        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                WindowManager.getDefault().getMainWindow().setTitle(NbBundle.getMessage(Installer.class, "CTL_AppTitle"));
            }
        });
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
