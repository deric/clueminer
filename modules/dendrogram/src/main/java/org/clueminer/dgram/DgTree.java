package org.clueminer.dgram;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.SwingConstants;
import javax.swing.event.EventListenerList;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.gui.BPanel;
import org.clueminer.hclust.DLeaf;
import org.clueminer.std.StdScale;

/**
 * Visualize dendrogram tree
 *
 * @author Tomas Barton
 */
public abstract class DgTree extends BPanel implements DendrogramDataListener, DendrogramTree, MouseMotionListener {

    protected DendroTreeData treeData;
    protected DendrogramMapping dendroData;
    protected DendroPane panel;
    protected int width;
    protected int treeHeight = 200;
    protected int treeWidth = 200;
    protected int height;
    protected int elementHeight;
    protected int halfElem;
    protected int elementWidth;
    private Color treeColor = Color.blue;
    private static final long serialVersionUID = -6201677645559622330L;
    protected EventListenerList treeListeners = new EventListenerList();
    protected final StdScale scale = new StdScale();
    protected final Insets insets = new Insets(0, 0, 0, 0);
    //HORIZONTAL represent rows clustering, while VERTICAL columns clustering
    protected int orientation;
    /**
     * mark nodes in dendrogram with a circle
     */
    protected boolean drawNodeCircle = true;
    private int diameter = 4;

    public DgTree(DendroPane panel) {
        this.panel = panel;
        Dimension elem = panel.getElementSize();
        elementWidth = elem.width;
        elementHeight = elem.height;
        this.fitToSpace = false;
        this.preserveAlpha = true;
        initComponents();
    }

    private void initComponents() {
        addMouseMotionListener(this);
    }

    @Override
    public abstract void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset);

    @Override
    public abstract void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting);

    @Override
    public abstract void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting);

    /**
     * Data containing hierarchical clustering result
     *
     * @return rows or columns clustering
     */
    public HierarchicalResult getHierarchicalData() {
        if (orientation == SwingConstants.HORIZONTAL) {
            return dendroData.getRowsResult();
        }
        return dendroData.getColsResult();
    }

    @Override
    public void render(Graphics2D g) {
        g.setComposite(AlphaComposite.Src);
        g.setColor(treeColor);
        DendroNode root = treeData.getRoot();
        drawSubTree(g, root);
    }

    @Override
    public void sizeUpdated(Dimension size) {
        if (hasData()) {
            if (size.width > 0 && size.height > 0) {
                reqSize = size;
                resetCache(); //calls recalculate
            }
        }
    }

    /**
     * Only for rows (horizontal) trees
     */
    @Override
    public void recalculate() {
        if (hasData()) {
            width = insets.left + treeHeight + insets.right + panel.getSliderDiameter();
            height = insets.top + dendroData.getNumberOfRows() * elementHeight + insets.bottom;
            halfElem = elementHeight / 2;
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

    @Override
    public boolean isAntiAliasing() {
        return true;
    }

    protected abstract void drawSubTree(Graphics2D g2, DendroNode node);

    /**
     * Draw circle marking node in the dendrogram
     *
     * @param g2
     * @param node
     * @param nx
     * @param ny
     */
    protected void drawNode(Graphics2D g2, DendroNode node, int nx, int ny) {
        Ellipse2D.Double circle;
        if (node.isLeaf()) {
            if (drawNodeCircle && !isClusterLeaf(node)) {
                circle = new Ellipse2D.Double(nx - diameter / 2.0, ny - diameter / 2.0, diameter, diameter);
                g2.fill(circle);
            }
            //nothing else to do
            return;
        } else {
            drawSubTree(g2, node.getLeft());
            drawSubTree(g2, node.getRight());
        }
        if (drawNodeCircle) {
            circle = new Ellipse2D.Double(nx - diameter / 2.0, ny - diameter / 2.0, diameter, diameter);
            g2.fill(circle);
        }
    }

    protected boolean isClusterLeaf(DendroNode node) {
        if (node != null && node.isLeaf()) {
            DLeaf leaf = (DLeaf) node;
            if (leaf.containsCluster()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Scales tree distance to pixels (for horizontal e.g. left-to-right tree)
     *
     * @param height
     * @return
     */
    protected double scaleDistance(double height) {
        return scale.scaleToRange(height, 0, treeData.getRoot().getHeight(), 0, treeHeight);
    }

    @Override
    public void setTreeData(DendroTreeData treeData) {
        this.treeData = treeData;
        resetCache();
        fireTreeUpdated();

    }

    @Override
    public DendroTreeData getTreeData() {
        return treeData;
    }

    @Override
    public void addTreeListener(TreeListener listener) {
        treeListeners.add(TreeListener.class, listener);
    }

    @Override
    public void removeTreeListener(TreeListener listener) {
        treeListeners.remove(TreeListener.class, listener);
    }

    @Override
    public boolean hasData() {
        return treeData != null;
    }

    @Override
    public void fireTreeUpdated() {
        TreeListener[] listeners;

        if (treeListeners != null) {
            listeners = treeListeners.getListeners(TreeListener.class);
            for (TreeListener listener : listeners) {
                listener.treeUpdated(this, realSize.width, realSize.height);
            }
        }
    }

    @Override
    public double getMinTreeHeight() {
        return 0.0;
    }

    @Override
    public double getMidTreeHeight() {
        return getMaxTreeHeight() / 2.0;
    }

    @Override
    public double getMaxTreeHeight() {
        if (hasData()) {
            return treeData.getRoot().getHeight();
        }
        return 0.0;
    }

    public Color getTreeColor() {
        return treeColor;
    }

    public void setTreeColor(Color treeColor) {
        this.treeColor = treeColor;
    }

    /**
     * Biggest distance between branches
     *
     * @return
     */
    @Override
    public int getTreeWidth() {
        return realSize.height;
    }

    /**
     * Distance from leaves to root
     *
     * @return actual height of tree without offsets
     */
    @Override
    public int getTreeHeight() {
        return treeHeight;
    }

    @Override
    public void fireLeafOrderUpdated(Object source, HierarchicalResult result) {
        TreeListener[] listeners;

        if (treeListeners != null) {
            listeners = treeListeners.getListeners(TreeListener.class);
            for (TreeListener listener : listeners) {
                listener.leafOrderUpdated(source, result);
            }
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        DendroNode node = findSubTree(e.getPoint());
        if (bufferedImage == null) {
            return;
        }
        if (node != null) {
            //Graphics2D g2 = bufferedImage.createGraphics();
            BufferedImage image = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) image.getGraphics();
            g2.setColor(Color.RED);
            drawSubTree(g2, node);
            bufferedImage.createGraphics().drawImage(image, null, 0, 0);
            repaint();
        } else {
            resetCache();
        }

    }

    /**
     * Find subtree below given point
     *
     * @param p
     * @return
     */
    public abstract DendroNode findSubTree(Point p);

}
