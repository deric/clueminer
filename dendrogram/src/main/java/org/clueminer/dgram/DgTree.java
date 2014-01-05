package org.clueminer.dgram;

import java.awt.Graphics;
import java.awt.Graphics2D;
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

/**
 *
 * @author Tomas Barton
 */
public class DgTree extends JPanel implements DendrogramDataListener, DendrogramTree {

    private DendroTreeData treeData;
    private DendrogramMapping dendroData;
    private DendroPane panel;
    private int width;
    private int height;
    private static final long serialVersionUID = -6201677645559622330L;
    protected EventListenerList treeListeners = new EventListenerList();
    private static final Logger logger = Logger.getLogger(DgTree.class.getName());

    public DgTree(DendroPane panel) {
        this.panel = panel;
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset) {
        this.dendroData = dataset;
        HierachicalClusteringResult clustering = (HierachicalClusteringResult) dataset.getRowsResult();
        treeData = clustering.getTreeData();
        logger.log(Level.INFO, "rendering tree" + treeData);

        DendroNode root = treeData.getRoot();
        System.out.println("tree has " + root.childCnt() + " nodes");
        System.out.println("root level is: " + root.level() + " height: " + root.getHeight());

        repaint();
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getMinDistance() {
        return 0;
    }

    @Override
    public int getMaxDistance() {
        return 42;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        if (!hasData()) {
            //no data
            g.dispose();
            //return;
        }

        DendroNode root = treeData.getRoot();
        System.out.println("tree has " + root.childCnt() + " nodes");
        System.out.println("root level is: " + root.level() + " height: " + root.getHeight());
        DendroNode current = treeData.first();

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
        //
    }

    @Override
    public double getMinTreeHeight() {
        return 0.0;
    }

    @Override
    public double getMidTreeHeight() {
        return 21;
    }

    @Override
    public double getMaxTreeHeight() {
        return 42;
    }
}
