package org.clueminer.dgram;

import java.awt.Graphics2D;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;

/**
 *
 * @author Tomas Barton
 */
public class DgLeftTree extends DgTree {

    private static final long serialVersionUID = 6752253584322193098L;

    public DgLeftTree(DendroPane panel) {
        super(panel);
    }

    @Override
    protected void drawSubTree(Graphics2D g2, DendroNode node) {
        int nx = (int) scaleDistance(node.getHeight());
        int ny = (int) (node.getPosition() * elementHeight + halfElem);

        drawNode(g2, node, nx, ny);

        if (!node.isLeaf()) {
            int lx = (int) scaleDistance(node.getLeft().getHeight());
            int ly = (int) (node.getLeft().getPosition() * elementHeight + halfElem);

            int rx = (int) scaleDistance(node.getRight().getHeight());
            int ry = (int) (node.getRight().getPosition() * elementHeight + halfElem);
            //we're drawing a U shape
            //straight line
            g2.drawLine(nx, ly, nx, ry);

            //left node
            g2.drawLine(nx, ly, lx, ly);

            //right node
            g2.drawLine(nx, ry, rx, ry);
        }
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset) {
        this.dendroData = dataset;
        HierarchicalResult clustering = dataset.getRowsResult();
        setTreeData(clustering.getTreeData());
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        //nothing to do
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        if (!hasData()) {
            return;
        }
        elementHeight = height;
        updateSize();
    }

}
