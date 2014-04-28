package org.clueminer.clustering.confusion;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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

    private static final long serialVersionUID = 8898532203822388282L;
    private ConfusionTable table;
    private RowLabels rowLabels;
    private ColumnLabels columnLabels;
    private Dimension dim = new Dimension(100, 100);
    private final Dimension elemSize = new Dimension(10, 10);
    private Clustering<Cluster> rowData;
    private Clustering<Cluster> colData;
    private final Insets insets = new Insets(10, 0, 5, 10);

    public ConfusionMatrix() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                recalculate();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                dim = getSize();
                recalculate();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                dim = getSize();
                recalculate();
            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });

        addRowLabels(0, 0);
        addMatrix(0, 1);
        addColumnLabels(1, 1);

    }

    private void recalculate() {
        //System.out.println("prefered size: " + getPreferredSize());
        //System.out.println("size: " + getSize());
        //System.out.println("min size: " + getMinimumSize());
        //System.out.println("======");
        dim.width = (int) Math.ceil(getSize().width * 0.9);
        dim.height = (int) Math.ceil(getSize().height * 0.9);
        //System.out.println("matrix component " + dim.width + ", " + dim.height);

        if (sizeUpdated()) {
            revalidate();
            validate();
            repaint();
        }
    }

    public boolean sizeUpdated() {
        if (rowData != null && colData != null) {
            if (rowData.size() > 0 && colData.size() > 0) {
                //System.out.println("rows = " + rowLabels.getSize());
                //System.out.println("cols = " + columnLabels.getSize());
                elemSize.width = (dim.width - insets.left - insets.right - rowLabels.getSize().width) / colData.size();
                elemSize.height = (dim.height - insets.top - insets.bottom - columnLabels.getSize().height) / rowData.size();
                //System.out.println("cnt = " + cnt);
                //System.out.println("setting elem size: " + elemSize);
                if (elemSize.width > 0 && elemSize.height > 0) {
                    rowLabels.updateSize(elemSize);
                    columnLabels.updateSize(elemSize);
                    table.updateSize(elemSize);
                    return true;
                }
            }
        }
        return false;
    }

    private void addMatrix(int row, int column) {
        table = new ConfusionTable();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new java.awt.Insets(insets.top, 0, 0, 0);
        c.gridx = column;
        c.gridy = row;
        add(table, c);
    }

    private void addRowLabels(int row, int column) {
        rowLabels = new RowLabels();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        /**
         * at least one component must be stretching in the free space or there
         * must be some glue to fill the empty space (if no, components would be
         * centered to middle)
         */
        c.weightx = 0.3;
        c.weighty = 1.0;
        c.insets = new java.awt.Insets(insets.top, 0, 0, 0);
        c.gridx = column;
        c.gridy = row;
        add(rowLabels, c);

    }

    private void addColumnLabels(int row, int column) {
        columnLabels = new ColumnLabels();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;
        //component in last row should be streatched to fill space at the bottom
        c.weighty = 0.0;
        c.insets = new java.awt.Insets(5, 0, 0, 0);
        c.gridx = column;
        c.gridy = row;
        add(columnLabels, c);
    }

    public void setClusterings(Clustering<Cluster> a, Clustering<Cluster> b) {
        this.rowData = a;
        this.colData = b;
        table.setClusterings(a, b);
        rowLabels.setClusterings(a, b);
        columnLabels.setClusterings(a, b);

        recalculate();
    }

}
