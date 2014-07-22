package org.clueminer.dendrogram;

import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Tomas Barton
 */
public class DendroToolbar extends JToolBar {

    private static final long serialVersionUID = 3796559248116111100L;
    private JToggleButton btnFitToSpace;

    public DendroToolbar() {
        super(SwingConstants.HORIZONTAL);
        initComponents();
    }

    private void initComponents() {

        this.setFloatable(false);
        this.setRollover(true);


        btnFitToSpace = new JToggleButton(ImageUtilities.loadImageIcon("org/clueminer/dendrogram/gui/fullscreen16.png", false));
        btnFitToSpace.setToolTipText("Fit to window");
        add(btnFitToSpace);

        addSeparator();
    }

}
