package org.clueminer.dgram;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import org.clueminer.cluster.HierachicalClusteringResult;
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
public class DgTree extends JPanel implements DendrogramDataListener, DendrogramTree {

    private DendroTreeData treeData;
    private DendrogramMapping dendroData;
    private DendroPane panel;
    private int width;
    private int treeHeight = 200;
    private int treeWidth = 200;
    private int height;
    private int elementHeight = 10;
    private int elementWidth = 10;
    private Color treeColor = Color.blue;
    private static final long serialVersionUID = -6201677645559622330L;
    protected EventListenerList treeListeners = new EventListenerList();
    private Dimension size = new Dimension(0, 0);
    private static final Logger logger = Logger.getLogger(DgTree.class.getName());
    private StdScale scale = new StdScale();

    public DgTree(DendroPane panel) {
        this.panel = panel;
        //setBackground(Color.RED);
        width = treeHeight;

        setSize(new Dimension(200, 200));
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset) {
        this.dendroData = dataset;
        HierachicalClusteringResult clustering = (HierachicalClusteringResult) dataset.getRowsResult();
        treeData = clustering.getTreeData();
        updateSize();
        logger.log(Level.INFO, "rendering tree" + treeData);

        DendroNode root = treeData.getRoot();
        System.out.println("tree has " + root.childCnt() + " nodes");
        System.out.println("root level is: " + root.level() + " height: " + root.getHeight());

        repaint();
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        elementWidth = width;
        updateSize();
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        if (!hasData()) {
            return;
        }
        elementHeight = height;
        updateSize();
    }

    @Override
    public void updateSize() {
        height = dendroData.getNumberOfRows() * elementHeight;
        //nodes on right, 90 deg rot
        treeWidth = height;
        size.width = width;
        size.height = height;
        setPreferredSize(size);
        setSize(size);
        setMinimumSize(size);
    }

    @Override
    public int getMinDistance() {
        return -1;
    }

    @Override
    public int getMaxDistance() {
        return -1;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("DgTree paintComponent");
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        System.out.println("DgTree paint");

        if (!hasData()) {
            //no data
            g.dispose();
            return;
        }

        g2.setColor(treeColor);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);

        DendroNode root = treeData.getRoot();
        System.out.println("tree has " + root.childCnt() + " nodes");
        System.out.println("root level is: " + root.level() + " height: " + root.getHeight());

        int leavesCnt = 0;
        int y = elementHeight / 2;

        //DendroNode current = treeData.first();
        drawNode(g2, root);
    }

    private void drawNode(Graphics2D g2, DendroNode node) {
        int nx;
        //  int ny = (int) node.getPosition() * elementHeight;
        if (node.isLeaf()) {
            //leaves on right side
            nx = width;
            return;
        } else {
            nx = (int) scaleHeight(node.getHeight());
            drawNode(g2, node.getLeft());
            drawNode(g2, node.getRight());
        }

        int lx = (int) scaleHeight(node.getLeft().getHeight());
        int ly = (int) node.getLeft().getPosition() * elementHeight;

        int rx = (int) scaleHeight(node.getRight().getHeight());
        int ry = (int) node.getRight().getPosition() * elementHeight;
        //we're drawing a U shape
        //straight line
        g2.drawLine(nx, ly, nx, ry);

        //left node
        g2.drawLine(nx, ly, lx, ly);

        //right node
        g2.drawLine(nx, ry, rx, ry);
    }

    private double scaleHeight(double height) {
        return scale.scaleToRange(height, 0, treeData.getRoot().getHeight(), 0, treeHeight);
    }

    @Override
    public void setTreeData(DendroTreeData treeData) {
        this.treeData = treeData;
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
}
