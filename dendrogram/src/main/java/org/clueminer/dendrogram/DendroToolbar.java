package org.clueminer.dendrogram;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

/**
 *
 * @author Tomas Barton
 */
public class DendroToolbar extends JToolBar {

    private static final long serialVersionUID = 3796559248116111100L;

    public DendroToolbar() {
        super(SwingConstants.HORIZONTAL);
        initComponents();
    }

    private void initComponents() {

        this.setFloatable(false);
        this.setRollover(true);

        add(new JButton("Fullscreen"));
        addSeparator();
    }

}
