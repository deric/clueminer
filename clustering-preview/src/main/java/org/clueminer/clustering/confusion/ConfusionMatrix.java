package org.clueminer.clustering.confusion;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;

/**
 *
 * @author Tomas Barton
 */
public class ConfusionMatrix extends JPanel {

    private ConfusionMatrix matrix;
    private RowLabels rowLabels;
    private ColumnLabels columnLabels;

    public ConfusionMatrix() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        addRowLabels(0, 0);
        addMatrix(1, 0);
        addColumnLabels(1, 1);
    }

    private void addMatrix(int column, int row) {
        //we call constructor just one
        if (matrix == null) {
            matrix = new ConfusionMatrix();
        }
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.insets = new java.awt.Insets(0, 0, 0, 0);
        c.gridx = column;
        c.gridy = row;
        add(matrix, c);
    }

    private void addRowLabels(int column, int row) {
        rowLabels = new RowLabels();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        /**
         * at least one component must be stretching in the free space or
         * there must be some glue to fill the empty space (if no,
         * components would be centered to middle)
         */
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new java.awt.Insets(0, 0, 0, 0);
        c.gridx = column;
        c.gridy = row;
        add(rowLabels, c);

    }

    private void addColumnLabels(int column, int row) {
        columnLabels = new ColumnLabels();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0.0;
        //component in last row should be streatched to fill space at the bottom
        c.weighty = 1.0;
        // c.gridwidth = GridBagConstraints.REMAINDER;
        //  c.gridheight = GridBagConstraints.REMAINDER;
        c.insets = new java.awt.Insets(5, 0, 0, 0);
        c.gridx = column;
        c.gridy = row;
        add(columnLabels, c);
    }

}
