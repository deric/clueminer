package org.clueminer.dgram;

import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;

/**
 * Dendrogram tree with root on left side and leaves on right
 *
 * @author Tomas Barton
 */
public class DgRightTree extends DgTree {

    private static final Border border = BorderFactory.createEmptyBorder(0, 10, 0, 0);
    private static final long serialVersionUID = -6425876686675787316L;
    //for cutoffslider
    private int leftOffset;

    public DgRightTree(DendroPane panel) {
        super(panel);
        setBorder(border);
        insets.left = 0;
        leftOffset = panel.getSliderDiameter();
    }

    @Override
    protected void drawSubTree(Graphics2D g2, DendroNode node) {
        int nx = treeHeight - (int) scaleDistance(node.getHeight()) + leftOffset;
        int ny = (int) (node.getPosition() * elementHeight + halfElem);

        drawNode(g2, node, nx, ny);

        if (!node.isLeaf()) {
            int lx = treeHeight - (int) scaleDistance(node.getLeft().getHeight()) + leftOffset;
            int ly = (int) (node.getLeft().getPosition() * elementHeight + halfElem);

            int rx = treeHeight - (int) scaleDistance(node.getRight().getHeight()) + leftOffset;
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
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping mapping) {
        this.dendroData = mapping;
        HierarchicalResult clustering = mapping.getRowsResult();
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
        if (elementHeight != height) {
            elementHeight = height;
            recalculate();
        }
    }

}
