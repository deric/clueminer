package org.clueminer.clustering.confusion;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JPanel;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;

/**
 *
 * @author Tomas Barton
 */
public class ConfusionMatrix extends JPanel {

    private ConfusionTable table;
    private RowLabels rowLabels;
    private ColumnLabels columnLabels;
    private Dimension dim = new Dimension();
    private Dimension elemSize = new Dimension();
    private Clustering<Cluster> a;
    private Clustering<Cluster> b;

    public ConfusionMatrix() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                dim = getSize();
                sizeUpdated();
                System.out.println("matix size " + dim.width + ", " + dim.height);
                revalidate();
                validate();
                repaint();
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });

        addRowLabels(0, 0);
        addMatrix(1, 0);
        addColumnLabels(1, 1);
    }

    public void sizeUpdated() {
        int cnt;
        if (a != null && b != null) {
            cnt = Math.min(a.size(), b.size());
            if (cnt > 0) {
                elemSize.width = dim.width / cnt;
                elemSize.height = dim.height / cnt;
                rowLabels.updateSize(elemSize);
                columnLabels.updateSize(elemSize);
                table.updateSize(dim);
            }
        }
    }

    private void addMatrix(int column, int row) {
        //we call constructor just one
        if (table == null) {
            table = new ConfusionTable();
        }
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new java.awt.Insets(0, 0, 0, 0);
        c.gridx = column;
        c.gridy = row;
        add(table, c);
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
        c.weightx = 0.2;
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
        c.weightx = 1.0;
        //component in last row should be streatched to fill space at the bottom
        c.weighty = 0.0;
        // c.gridwidth = GridBagConstraints.REMAINDER;
        //  c.gridheight = GridBagConstraints.REMAINDER;
        c.insets = new java.awt.Insets(5, 0, 0, 0);
        c.gridx = column;
        c.gridy = row;
        add(columnLabels, c);
    }

    public void setClusterings(Clustering<Cluster> a, Clustering<Cluster> b) {
        this.a = a;
        this.b = b;
        table.setClusterings(a, b);
        rowLabels.setClusterings(a, b);
        columnLabels.setClusterings(a, b);
    }

}
