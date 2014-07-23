package org.clueminer.dendrogram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Tomas Barton
 */
public class DendroToolbar extends JToolBar {

    private static final long serialVersionUID = 3796559248116111100L;
    private JToggleButton btnFitToSpace;
    private final DendroViewer viewer;

    public DendroToolbar(DendroViewer viewer) {
        super(SwingConstants.HORIZONTAL);
        this.viewer = viewer;
        initComponents();
    }

    private void initComponents() {

        this.setFloatable(false);
        this.setRollover(true);


        btnFitToSpace = new JToggleButton(ImageUtilities.loadImageIcon("org/clueminer/dendrogram/gui/fullscreen16.png", false));
        btnFitToSpace.setToolTipText("Fit to window");
        add(btnFitToSpace);

        btnFitToSpace.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                viewer.setFitToPanel(btnFitToSpace.isSelected());
            }
        });

        addSeparator();
    }

}
