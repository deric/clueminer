package org.clueminer.dgram;

import java.awt.Graphics2D;
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

    public DgBottomTree(DendroPane panel) {
        super(panel);
    }

    @Override
    public void updateSize() {
        width = dendroData.getNumberOfColumns() * elementWidth;
        height = treeHeight;
        halfElem = elementWidth / 2;
        //nodes on right, 90 deg rot
        setSizes(width, height);
        invalidateCache();
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
            treeData = clustering.getTreeData();
            updateSize();
        }
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        elementWidth = width;
        updateSize();
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        //nothing to do
    }

}
