package org.clueminer.dgram;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.std.StdScale;

/**
 *
 * @author Tomas Barton
 */
public abstract class DgTree extends JPanel implements DendrogramDataListener, DendrogramTree {

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
    private BufferedImage buffImg;
    private Graphics2D buffGr;
    private final Insets insets = new Insets(0, 0, 0, 0);
    /**
     * mark nodes in dendrogram with a circle
     */
    protected boolean drawNodeCircle = true;
    private int diameter = 4;

    public DgTree(DendroPane panel) {
        this.panel = panel;
        setBackground(panel.getBackground());
        Dimension elem = panel.getElementSize();
        elementWidth = elem.width;
        elementHeight = elem.height;
    }

    @Override
    public abstract void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset);

    @Override
    public abstract void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting);

    @Override
    public abstract void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting);

    /**
     * For horizontal tree (left-to-right or right-to-left)
     */
    @Override
    public void updateSize() {
        width = treeHeight;
        height = dendroData.getNumberOfRows() * elementHeight;
        halfElem = elementHeight / 2;
        //nodes on right, 90 deg rot
        setSizes(width, height);
        invalidateCache();
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

    /**
     * We try to avoid complete repainting as much as possible
     */
    public void invalidateCache() {
        //invalidate cache
        buffImg = null;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (buffImg == null) {
            drawTree();
        }

        Graphics2D g2 = (Graphics2D) g;
        if (buffGr != null) {
            g2.drawImage(buffImg,
                         insets.left, insets.top,
                         size.width, size.height,
                         null);
        }
        g2.dispose();
    }

    public void drawTree() {
        if (!hasData() || size.width == 0 || size.height == 0) {
            //no data
            return;
        }
        buffImg = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        buffGr = buffImg.createGraphics();

        buffGr.setColor(treeColor);

        buffGr.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
        buffGr.setRenderingHint(RenderingHints.KEY_RENDERING,
                                RenderingHints.VALUE_RENDER_QUALITY);

        DendroNode root = treeData.getRoot();
        //System.out.println("tree has " + root.childCnt() + " nodes");
        //System.out.println("root level is: " + root.level() + " height: " + root.getHeight());

        //DendroNode current = treeData.first();
        drawSubTree(buffGr, root);
        buffGr.dispose();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
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
        updateSize();
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
     * @return
     */
    @Override
    public int getTreeHeight() {
        return size.width;
    }
}
