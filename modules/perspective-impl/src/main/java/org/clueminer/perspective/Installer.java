package org.clueminer.perspective;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.clueminer.perspective.api.PerspectiveController;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall {

    private static final long serialVersionUID = -7226855135848898927L;

    @Override
    public void restored() {
        //Initialize the perspective controller
        Lookup.getDefault().lookup(PerspectiveController.class);

        // Init Banner
        initBanner();
    }
    
     private void initBanner() {
        //This would be too late:
        //WindowManager.getDefault().invokeWhenUIReady(new Runnable() {});
        //Therefore use this:
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                //Get the main window of the NetBeans Platform:
                JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
                //Get our custom main toolbar:
                JComponent toolbar = new BannerComponent();

                //Set the new layout of our root pane:
                frame.getRootPane().setLayout(new BannerRootPanelLayout(toolbar));
                //Install a new toolbar component into the layered pane
                //of the main frame on layer 0:
                toolbar.putClientProperty(JLayeredPane.LAYER_PROPERTY, 0);
                frame.getRootPane().getLayeredPane().add(toolbar, 0);
            }
        });

        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

            @Override
            public void run() {
                JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();

                //Replace the content pane with our creation
                JComponent statusLinePanel = null;
                for (Component cpnt : frame.getContentPane().getComponents()) {
                    if (cpnt.getName() != null && cpnt.getName().equals("statusLine")) {
                        statusLinePanel = (JComponent) cpnt;
                    }
                }
                if (statusLinePanel != null) {
                    frame.getContentPane().remove(statusLinePanel);
                    JPanel southPanel = new JPanel(new BorderLayout());
                    southPanel.add(statusLinePanel, BorderLayout.SOUTH);
                    frame.getContentPane().add(southPanel, BorderLayout.SOUTH);
                }
            }
        });
    }
}
