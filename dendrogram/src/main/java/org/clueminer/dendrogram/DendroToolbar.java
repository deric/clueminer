package org.clueminer.dendrogram;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
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
    private ButtonGroup buttonGroup;
    private JToggleButton btnFitToSpace;

    public DendroToolbar() {
        super(SwingConstants.HORIZONTAL);
        initComponents();
    }

    private void initComponents() {

        this.setFloatable(false);
        this.setRollover(true);

        buttonGroup = new ButtonGroup();

        btnFitToSpace = new JToggleButton(ImageUtilities.loadImageIcon("org/clueminer/dendrogram/gui/fullscreen.png", false));
        btnFitToSpace.setToolTipText("Fit to window");
        buttonGroup.add(btnFitToSpace);


        add((Action) buttonGroup);
        addSeparator();
    }

}
