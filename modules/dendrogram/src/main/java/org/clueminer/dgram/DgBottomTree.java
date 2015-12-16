package org.clueminer.dgram;

import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.SwingConstants;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;

/**
 * Root is at the top, leaves goes downwards
 *
 * @author Tomas Barton
 */
public class DgBottomTree extends DgTree {

    private static final long serialVersionUID = 4173316284862271458L;

    public DgBottomTree(DendroPane panel) {
        super(panel);
        this.orientation = SwingConstants.VERTICAL;
    }

    @Override
    public void recalculate() {
        if (hasData()) {
            width = insets.left + dendroData.getNumberOfColumns() * elementWidth + insets.right;
            height = insets.top + treeHeight + insets.bottom;
            halfElem = elementWidth / 2;
            if (width > 0 && height > 0) {
                realSize.width = width;
                //reqSize.width = width;
                realSize.height = height;
                setSize(realSize);
                setPreferredSize(realSize);
                setMinimumSize(realSize);
            }
        }
    }

    /*
     [lx, ny]    [nx, ny]      [rx, ny]
     ---------------|-----------
     |                         |
     |                         |
     |                         |
     [lx,ly]                [rx, ry]
     */
    @Override
    protected void drawSubTree(Graphics2D g2, DendroNode node) {
        int ny = treeHeight - (int) scaleDistance(node.getHeight());
        int nx = (int) (node.getPosition() * elementWidth + halfElem);

        //   System.out.println("node: [ " + nx + ", " + ny + " ]");
        drawNode(g2, node, nx, ny);

        if (!node.isLeaf()) {
            int ly = treeHeight - (int) scaleDistance(node.getLeft().getHeight());
            int lx = (int) (node.getLeft().getPosition() * elementWidth + halfElem);

            int ry = treeHeight - (int) scaleDistance(node.getRight().getHeight());
            int rx = (int) (node.getRight().getPosition() * elementWidth + halfElem);
            //we're drawing a U shape
            //straight line
            g2.drawLine(lx, ny, rx, ny);

            //left node
            g2.drawLine(lx, ny, lx, ly);

            //right node
            g2.drawLine(rx, ny, rx, ry);
        }
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset) {
        this.dendroData = dataset;
        if (dendroData.hasColumnsClustering()) {
            HierarchicalResult clustering = dataset.getColsResult();
            setTreeData(clustering.getTreeData());
            //to prevent tree flickering
            recalculate();
        }
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        if (elementWidth != width) {
            elementWidth = width;
            recalculate();
        }
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        //nothing to do
    }

    /**
     * Biggest distance between branches
     *
     * @return
     */
    @Override
    public int getTreeWidth() {
        return realSize.width;
    }

    /**
     * Distance from leaves to root
     *
     * @return
     */
    @Override
    public int getTreeHeight() {
        return treeHeight;
    }

    @Override
    public DendroNode findSubTree(Point p) {
        DendroNode node = null;

        return node;
    }

    @Override
    public boolean isHorizontal() {
        return false;
    }

    @Override
    public boolean isVertical() {
        return true;
    }

}
