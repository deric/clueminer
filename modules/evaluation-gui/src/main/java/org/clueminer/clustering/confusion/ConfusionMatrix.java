/*
 * Copyright (C) 2011-2017 clueminer.org
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
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class ConfusionMatrix<E extends Instance, C extends Cluster<E>> extends JPanel {

    private static final long serialVersionUID = 8898532203822388282L;
    private ConfusionTable table;
    private RowLabels rowLabels;
    private ColumnLabels columnLabels;
    private Dimension dim = new Dimension(100, 100);
    private final Dimension elemSize = new Dimension(10, 10);
    private String[] rowData;
    private String[] colData;
    private final Insets insets = new Insets(10, 0, 5, 10);
    private boolean displayClusterSizes = true;

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

        addColumnLabels(0, 1);
        addRowLabels(1, 0);
        addMatrix(1, 1);

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
            if (rowData.length > 0 && colData.length > 0) {
                //System.out.println("rows = " + rowLabels.getSize());
                //System.out.println("cols = " + columnLabels.getSize());
                int rowCnt = rowData.length;
                int colCnt = colData.length;
                if (displayClusterSizes) {
                    rowCnt += 1;
                    colCnt += 1;
                }
                elemSize.width = (dim.width - insets.left - insets.right - rowLabels.getSize().width) / colCnt;
                elemSize.height = (dim.height - insets.top - insets.bottom - columnLabels.getSize().height) / rowCnt;
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

    public void setClustering(Clustering<E, C> clust) {
        table.setClustering(clust);
        updateLabels();

    }

    public void setClusterings(Clustering<E, C> a, Clustering<E, C> b) {
        table.setClusterings(a, b);
        updateLabels();
    }

    private void updateLabels() {
        rowData = table.getRowLabels();
        colData = table.getColLabels();
        rowLabels.setLabels(rowData);
        columnLabels.setLabels(colData);

        recalculate();
    }

    public boolean isDisplayClusterSizes() {
        return displayClusterSizes;
    }

    public void setDisplayClusterSizes(boolean displayClusterSizes) {
        this.displayClusterSizes = displayClusterSizes;
        table.setDisplayClustSizes(displayClusterSizes);
    }

}
