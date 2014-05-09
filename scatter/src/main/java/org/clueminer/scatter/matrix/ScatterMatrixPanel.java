package org.clueminer.scatter.matrix;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JPanel;

/**
 *
 * @author Tomas Barton
 */
public class ScatterMatrixPanel extends JPanel {

    private static final long serialVersionUID = 4957672836007726620L;

    public ScatterMatrixPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setSize(new Dimension(800, 600));
    }

}
