package org.clueminer.dgram;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.hclust.DClusterLeaf;

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

    BasicStroke normalStroke;
    BasicStroke thickStroke;

    public DgRightTree(DendroPane panel) {
        super(panel);
        setBorder(border);
        this.orientation = SwingConstants.HORIZONTAL;
        insets.left = 0;

        leftOffset = panel.getSliderDiameter();
        normalStroke = new BasicStroke(1);
        thickStroke = new BasicStroke((float) 1.5);
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
            g2.setStroke(normalStroke);
            g2.drawLine(nx, ly, nx, ry);

            if (isClusterLeaf(node.getRight())) {
                drawCluster(g2, nx, ry, (DClusterLeaf) node.getRight());
            } else {
                g2.drawLine(nx, ry, rx, ry);
            }

            if (isClusterLeaf(node.getLeft())) {
                drawCluster(g2, nx, ly, (DClusterLeaf) node.getLeft());
            } else {
                g2.drawLine(nx, ly, lx, ly);
            }
        }
    }

    private void drawCluster(Graphics2D g2, int x, int y, DClusterLeaf leaf) {
        int splitStart = treeHeight - (int) scaleDistance(0.5) + leftOffset;
        g2.drawLine(x, y, splitStart, y);
        int clusterBottom = (int) (y + elementHeight * ((leaf.getInstances().size() - 1) / 2));
        int clusterUp = (int) (y - elementHeight * ((leaf.getInstances().size()) / 2));
        g2.setStroke(thickStroke);
        g2.drawLine(splitStart, clusterUp, splitStart, clusterBottom);
        g2.drawLine(splitStart, clusterUp, treeHeight + leftOffset * 3 + 1, clusterUp);
        g2.drawLine(splitStart, clusterBottom, treeHeight + leftOffset * 3 + 1, clusterBottom);
        g2.setStroke(normalStroke);

    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping mapping) {
        this.dendroData = mapping;
        HierarchicalResult clustering = mapping.getRowsResult();
        setTreeData(clustering.getTreeData());
        if (clustering.getTreeData().containsClusters()) {
            leftOffset = panel.getSliderDiameter() / 3;
        }
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

    /**
     * Convert position on component to tree height
     *
     * @param pos
     * @return
     */
    private double reverseScale(double pos) {
        return scale.scaleToRange(pos, 0, treeHeight, treeData.getRoot().getHeight(), 0);
    }

    private double reverseWidth(double pos) {
        return scale.scaleToRange(pos, 0, treeWidth, realSize.height, 0);
    }

    /**
     * Find subtree below given point
     *
     * @param p point on tree canvas
     * @return
     */
    @Override
    public DendroNode findSubTree(Point p) {
        DendroNode node;
        double x = p.x - leftOffset - insets.left;
        double y = p.y - insets.top;
        //System.out.println("[" + p.getX() + ", " + p.getY() + "] -> " + reverseScale(x));
        DendroNode root = treeData.getRoot();
        node = getHierarchicalData().findTreeBelow(root, reverseScale(x), reverseWidth(y));
        //System.out.println("root pos " + root.getPosition() + ", h = " + root.getHeight());

        return node;
    }

}
