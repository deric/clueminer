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
package org.clueminer.dendrogram.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.gui.BPanel;
import org.clueminer.utils.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Color stripe for showing assignments to clusters
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class ClusterAssignment<E extends Instance, C extends Cluster<E>> extends BPanel implements DendrogramDataListener, ClusteringListener<E, C> {

    private static final long serialVersionUID = 7662186965958650502L;
    private final DendroPane panel;
    protected int stripeWidth = 20;
    private int maxTextWidth = 20;
    protected boolean drawBorders = true;
    private boolean drawLabels = true;
    protected final Insets insets = new Insets(0, 10, 0, 10);
    private static final Logger LOG = LoggerFactory.getLogger(ClusterAssignment.class);
    protected Font font = new Font("verdana", Font.BOLD, 12);
    protected int lineHeight;
    private final int labelOffset = 5;
    protected Clustering<E, Cluster<E>> flatClust;
    protected HierarchicalResult hieraRes;

    public ClusterAssignment(DendroPane panel) {
        this.panel = panel;
        setBackground(panel.getBackground());
        //this.preserveAlpha = true;
        //this.fitToSpace = true;
        recalculate();
    }

    protected void drawData(Graphics2D g) {
        if (flatClust != null) {
            HierarchicalResult res = flatClust.getLookup().lookup(HierarchicalResult.class);
            if (res != null) {
                hieraRes = res;
            }
        }
        if (flatClust != null && hieraRes != null) {
            //param for getClusters is not used
            int[] clusters = hieraRes.getClusters(0);
            int i = 0;
            if (clusters == null || clusters.length == 0) {
                LOG.info("clusters size is 0!!!");
                return;
            }
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            lineHeight = fm.getHeight();
            int currClust = clusters[i];
            int start = 0;
            int x = insets.left;
            int mapped;
            while (i < clusters.length) {
                mapped = hieraRes.getMappedIndex(i);
                if (clusters[mapped] != currClust) {
                    drawCluster(g, x, start, i, currClust);
                    start = i; //index if new cluster start
                    currClust = clusters[mapped];
                }
                i++;
            }
            //close unfinished cluster
            drawCluster(g, x, start, i, currClust);
        }
        g.dispose(); //finished drawing
    }

    private void drawCluster(Graphics2D g, int x, int start, int end, int clusterNum) {
        int y = start * elemHeight();
        Color color;
        if (flatClust.hasAt(clusterNum)) {
            color = flatClust.get(clusterNum).getColor();
        } else {
            color = Color.GRAY;
        }

        int y2 = (end - start) * elemHeight();
        FontRenderContext frc = g.getFontRenderContext();
        if (y == 0) {
            g.setColor(color);
            g.fillRect(x + 1, y + 1, stripeWidth - 2, y2 - 2);
            g.setColor(Color.black);
            if (this.drawBorders) {
                g.drawRect(x, y, stripeWidth - 1, y2 - 1);
            }
        } else {
            g.setColor(color);
            g.fillRect(x + 1, y - 1, stripeWidth - 2, y2);
            g.setColor(Color.black);
            if (this.drawBorders) {
                g.drawRect(x, y - 1, stripeWidth - 1, y2);
            }

        }
        if (y2 > 0) {
            //clusterNum starts from 0, we make it more human readable
            drawLabel(g, y + y2 / 2.0, clusterNum + 1, frc);
        }
    }

    private void drawLabel(Graphics2D g, double y, int clusterNum, FontRenderContext frc) {
        String label;
        int labelWidth;
        double xLabel, yLabel;

        if (drawLabels) {
            label = String.valueOf(clusterNum);
            labelWidth = (int) (g.getFont().getStringBounds(label, frc).getWidth()) + 10;
            checkMax(labelWidth);
            yLabel = y + lineHeight / 2.0;
            xLabel = stripeWidth + labelOffset + insets.left;
            //logger.log(Level.INFO, "label {0} at {1}, {2}", new Object[]{label, xLabel, yLabel});
            g.drawString(label, (float) xLabel, (float) yLabel);
        }
    }

    protected int elemHeight() {
        return panel.getElementSize().height;
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dendroData) {
        hieraRes = dendroData.getRowsResult();
        //flatClust = hieraRes.getClustering();
        resetCache();
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        //we don't care
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        resetCache();
    }

    @Override
    public void clusteringChanged(Clustering clust) {
        flatClust = clust;
        resetCache();
    }

    @Override
    public void clusteringStarted(Dataset<E> dataset, Props params) {
        //nothing to do
    }

    @Override
    public void resultUpdate(HierarchicalResult hclust) {
        hieraRes = hclust;
        resetCache();
    }

    public boolean isDrawLabels() {
        return drawLabels;
    }

    public void setDrawLabels(boolean drawLabels) {
        this.drawLabels = drawLabels;
    }

    public boolean isDrawBorders() {
        return drawBorders;
    }

    public void setDrawBorders(boolean drawBorders) {
        this.drawBorders = drawBorders;
    }

    private void checkMax(int width) {
        if (width > maxTextWidth) {
            maxTextWidth = width;
            resetCache();
        }
    }

    @Override
    public void render(Graphics2D g) {
        if (!hasData()) {
            return;
        }

        drawData(g);
    }

    @Override
    public void sizeUpdated(Dimension size) {
        realSize = size;

        setPreferredSize(realSize);
        setMinimumSize(realSize);
    }

    @Override
    public boolean hasData() {
        if (hieraRes != null) {
            Dataset<E> dataset = hieraRes.getDataset();
            if (dataset != null) {
                if (dataset.getClasses().size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public final void recalculate() {
        if (hasData()) {
            realSize.width = insets.left + stripeWidth + 2 * labelOffset + maxTextWidth + insets.right;
            realSize.height = insets.top + panel.getElementSize().height * hieraRes.getDataset().size() + 2 + insets.bottom;
            reqSize.width = realSize.width;
            reqSize.height = realSize.height;

            setSize(realSize);
        }
    }

    @Override
    public boolean isAntiAliasing() {
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
