package org.clueminer.dendrogram.tree;

import java.awt.Graphics;
import java.awt.Graphics2D;
import org.clueminer.clustering.algorithm.HCLResult;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;

/**
 *
 * @author Tomas Barton
 */
public class VerticalTree extends AbstractTree {

    private static final long serialVersionUID = -8889564020379876891L;

    public VerticalTree(DendroPane panel) {
        super(panel);
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        //do nothing, we don't care about width change
        updateTreeSize();
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        updateTreeSize();
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset) {
        this.dataset = dataset;
        HCLResult clustering = (HCLResult) dataset.getRowsResult();
        setDTreeData(clustering.getTreeData());
        repaint();
    }

    @Override
    protected double getScale() {
        if (!this.useAbsoluteHeight) {
            return (getSize().width - xOffset) / maxHeight;
            // pHeights[node] = Math.max(pHeights[child_1], pHeights[child_2]) + Math.max(Math.min((int) Math.round(height[node] * scale), max_pixels), min_pixels);
        } else {
            return (getSize().width - xOffset) / maxHeight;//this.maxHeight;
        }

    }

    @Override
    protected void drawArms(Graphics2D g2, int child_1_x1, int child_1_x2, int child_1_y, int child_2_x1, int child_2_x2, int child_2_y) {
        if (actualArms) {
            int maxx1 = Math.min(child_1_x2, child_2_x2);
            g2.drawLine(child_1_x1, child_1_y, maxx1, child_1_y);
            g2.drawLine(child_2_x1, child_2_y, maxx1, child_2_y);
            g2.drawLine(child_1_x1, child_1_y, child_2_x1, child_2_y);

        } else {
            g2.drawLine(child_1_x1, child_1_y, child_1_x2, child_1_y);
            g2.drawLine(child_2_x1, child_2_y, child_2_x2, child_2_y);
            g2.drawLine(child_1_x1, child_1_y, child_2_x1, child_2_y);

        }
    }

    @Override
    protected void updateTreeSize() {
        /*
         * if (flatTree || this.stepSize == elementSize.height) { return; }
         */
        int length = (this.treeData == null ? 0 : treeData.treeLevels());
        this.stepSize = this.elementSize.height;
        setOffsetY(-elementSize.height / 2);
        int height = this.stepSize * length;
        System.out.println("vertical tree size width = " + getTreeHeight() + " height = " + height);
        setSizes(getTreeHeight(), height);
    }

    @Override
    protected void drawEdge(Graphics g, int[] xs, int[] ys, int x1, int x2, int y1, int y2, int k, int k1) {
        ys[0] = (y2 - y1) / 2 + y1;
        ys[1] = (int) (this.positions[k] * this.stepSize) + this.stepSize / 2;
        ys[2] = (int) (this.positions[k1] * this.stepSize) + this.stepSize / 2;
        xs[0] = x1;
        xs[1] = this.pHeights[treeData.getOrder(treeData.treeLevels())];
        xs[2] = this.pHeights[treeData.getOrder(treeData.treeLevels())];
    }

    @Override
    protected boolean nodeFound(int x, int y, int child_1_x1, int child_1_y, int child_2_y) {
        if (child_1_y < y && child_2_y > y && x > child_1_x1) {
            return true;
        }
        return false;
    }

    @Override
    protected void setOrientation(Graphics2D g2) {
        //nothing to do in here
    }
}
