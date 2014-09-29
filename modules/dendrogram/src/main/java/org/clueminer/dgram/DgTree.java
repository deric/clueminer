package org.clueminer.dgram;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Ellipse2D;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.clueminer.std.StdScale;

/**
 *
 * @author Tomas Barton
 */
public abstract class DgTree extends BPanel implements DendrogramDataListener, DendrogramTree {

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
    protected Dimension size = new Dimension(0, 0);
    private static final Logger logger = Logger.getLogger(DgTree.class.getName());
    private final StdScale scale = new StdScale();
    protected final Insets insets = new Insets(0, 0, 0, 0);
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
        this.fitToSpace = true;
        this.preserveAlpha = true;
    }

    @Override
    public abstract void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset);

    @Override
    public abstract void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting);

    @Override
    public abstract void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting);

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
            realSize.width = size.width;
            realSize.height = size.height;
            if (bufferedImage != null) {
                System.out.println("bi " + bufferedImage.getWidth() + " x " + bufferedImage.getHeight());
            }
            resetCache();
        }
    }

    @Override
    public void recalculate() {
        width = insets.left + treeHeight + insets.right + panel.getSliderDiameter();
        height = insets.top + dendroData.getNumberOfRows() * elementHeight + insets.bottom;
        System.out.println("tree " + width + " x " + height);
        halfElem = elementHeight / 2;
        realSize.width = width;
        reqSize.width = width;
        realSize.height = height;
        reqSize.height = height;
        //nodes on right, 90 deg rot
        //setSizes(width, height);
        setMinimumSize(reqSize);
        setSize(reqSize);
        //setPreferredSize(realSize);
        logger.log(Level.FINER, "recalculate");
    }

    @Override
    public boolean isAntiAliasing() {
        return true;
    }

    /**
     * Set component size
     *
     * @param width
     * @param height
     */
    public void setSizes(int width, int height) {
        size.width = width;
        size.height = height;
        setPreferredSize(size);
        setSize(size);
        setMinimumSize(size);
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
            if (drawNodeCircle) {
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
        recalculate();
        fireTreeUpdated();
        resetCache();
    }

    @Override
    public DendroTreeData getTreeData() {
        return treeData;
    }

    @Override
    public
            void addTreeListener(TreeListener listener) {
        treeListeners.add(TreeListener.class, listener);
    }

    @Override
    public
            void removeTreeListener(TreeListener listener) {
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
            listeners = treeListeners.getListeners(TreeListener.class
            );
            for (TreeListener listener : listeners) {
                listener.treeUpdated(this, size.width, size.height);
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
        return size.height;
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
            listeners = treeListeners.getListeners(TreeListener.class
            );
            for (TreeListener listener : listeners) {
                listener.leafOrderUpdated(source, result);
            }
        }
    }
}
