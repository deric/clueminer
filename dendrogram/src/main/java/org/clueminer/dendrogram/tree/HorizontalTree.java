package org.clueminer.dendrogram.tree;

import java.awt.Graphics;
import java.awt.Graphics2D;
import org.clueminer.cluster.HierachicalClusteringResult;
import org.clueminer.dendrogram.DendroPane;
import org.clueminer.dendrogram.DendrogramData;
import org.clueminer.dendrogram.events.DendrogramDataEvent;

/**
 *
 * @author Tomas Barton
 */
public class HorizontalTree extends AbstractTree {

    private static final long serialVersionUID = 1120679319594279173L;

    public HorizontalTree(DendroPane panel) {
        super(panel);
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        updateTreeSize();
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        //do nothing, we don't care about height change
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramData dataset) {
        this.dataset = dataset;
        HierachicalClusteringResult clustering = (HierachicalClusteringResult) dataset.getColsResult();
        setTreeData(clustering.getTreeData());
        repaint();
    }

    @Override
    protected double getScale() {
        if (!this.useAbsoluteHeight) {
            return (getSize().height - yOffset) / maxHeight;
            // pHeights[node] = Math.max(pHeights[child_1], pHeights[child_2]) + Math.max(Math.min((int) Math.round(height[node] * scale), max_pixels), min_pixels);
        } else {
            //  System.out.println(orientation + ": height " + getSize().height + " off " + yOffset + " max h= " + maxHeight);
            return (getSize().height - yOffset) / maxHeight;//this.maxHeight;
        }
    }

    @Override
    protected void drawArms(Graphics2D g2, int child_1_x1, int child_1_x2, int child_1_y, int child_2_x1, int child_2_x2, int child_2_y) {
        if (actualArms) {
            int maxx1 = Math.max(child_1_x2, child_2_x2);
            g2.drawLine(child_1_x1, child_1_y, maxx1, child_1_y);
            g2.drawLine(child_2_x1, child_2_y, maxx1, child_2_y);
            g2.drawLine(child_1_x1, child_1_y, child_2_x1, child_2_y);
        } else {
            //    System.out.println("xOff= "+xOffset+ " yOff= "+yOffset);
            g2.drawLine(child_1_x1, child_1_y, child_1_x2, child_1_y);
            g2.drawLine(child_2_x1, child_2_y, child_2_x2, child_2_y);
            g2.drawLine(child_1_x1, child_1_y, child_2_x1, child_2_y);

        }
    }

    @Override
    protected void updateTreeSize() {
        /*
         * if (flatTree || this.stepSize == elementSize.width) { return; }
         */
        this.stepSize = elementSize.width;
        //move the tree branch to middle of cell
        setOffsetX(-elementSize.width / 2);
        int width = stepSize * treeData.treeLevels();
        int height = getMaxDistance();//for scale
        //System.out.println("row tree width= "+width+", height= "+height);
        setSizes(width, height);
    }

    @Override
    protected void drawEdge(Graphics g, int[] xs, int[] ys, int x1, int x2, int y1, int y2, int k, int k1) {
        ys[0] = (y2 - y1) / 2 + y1 + yOffset;
        ys[1] = (int) (this.positions[k] * this.stepSize) + this.stepSize / 2;
        ys[2] = (int) (this.positions[k1] * this.stepSize) + this.stepSize / 2;
        xs[0] = x1;
        xs[1] = -1 * this.pHeights[treeData.getOrder(treeData.treeLevels() - 1)];
        xs[2] = -1 * this.pHeights[treeData.getOrder(treeData.treeLevels() - 1)];
    }

    @Override
    protected boolean nodeFound(int x, int y, int child_1_x1, int child_1_y, int child_2_y) {
        if (child_1_y < x && child_2_y > x && y > child_1_x1) {
            return true;
        }
        return false;
    }

    @Override
    protected void setOrientation(Graphics2D g2) {
        g2.rotate(-Math.PI / 2.0);
        sign = -1;
    }
}
