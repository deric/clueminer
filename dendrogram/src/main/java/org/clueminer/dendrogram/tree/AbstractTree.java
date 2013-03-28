package org.clueminer.dendrogram.tree;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;
import org.clueminer.clustering.api.dendrogram.TreeCluster;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.dendrogram.DendrogramData;
import org.clueminer.dendrogram.events.DendrogramDataListener;
import org.clueminer.dendrogram.gui.DendrogramPanel;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.gui.ColorGenerator;
import org.clueminer.hclust.TreeData;
import org.clueminer.utils.Dump;

public abstract class AbstractTree extends JPanel implements DendrogramDataListener, DendrogramTree {

    private static final long serialVersionUID = 7858579750962659728L;
    protected int min_pixels = 5;
    protected int max_pixels = 200;
    protected double zero_threshold = 0.01;
    protected Color lineColor = new Color(0, 0, 128);
    protected Color belowThrColor = Color.lightGray;
    protected Color selectedLineColor = Color.RED;
    protected boolean actualArms = false;
    protected boolean useAbsoluteHeight = true;
    /**
     * could be true when parent component supports scrolling no scrolling
     * inside component
     */
    private boolean fitToArea = false;
    // initial data
    // a result data
    protected TreeData treeData;
    // helpers
    protected int stepSize = 1;
    protected int[] pHeights;
    int[] nodeRaised;
    protected float[] positions;
    protected boolean[] selected;
    protected Color[] nodesColors;
    /**
     * moves tree from the beginning of coordinate system
     */
    protected int xOffset = 0;
    protected int yOffset = 0;
    protected int[] parentNodes;
    protected boolean[] terminalNodes;
    /**
     * height of lowest level in tree
     */
    protected double minHeight;
    protected double maxHeight;
    protected boolean flatTree = false;
    protected DistanceMeasure function;
    private double similarityFactor = 1.0;
    private double nodeHeightOffset = 0.0;
    protected DendrogramData dataset;
    protected DecimalFormat decimalFormat = new DecimalFormat("#.##");
    protected DendrogramPanel panel;
    protected Dimension elementSize;
    //preferred size of tree (does not include scale)
    protected Dimension size = new Dimension(10, 10);
    protected int sign = 1;
    protected EventListenerList treeListeners = new EventListenerList();

    public AbstractTree(DendrogramPanel panel) {
        this.panel = panel;
        //don't paint background
        setOpaque(false);
        this.elementSize = panel.getElementSize();
        addMouseListener(new Listener());
    }

    public void setTreeData(TreeData treeData) {
        this.treeData = treeData;
        function = treeData.getFunction();
        // helpers
        this.flatTree = treeData.flatTreeCheck();
        this.minHeight = treeData.getMinHeight();
        this.maxHeight = treeData.getMaxHeight();

        similarityFactor = function.getSimilarityFactor();

        //this.minHeight = getMinHeight(treeData.order, treeData.height);
        //this.maxHeight = getMaxHeight(treeData.order, treeData.height);
        this.zero_threshold = minHeight;
        this.terminalNodes = new boolean[this.treeData.nodesNumber()];

        initializeParentNodeArray();
        //Dump.array(parentNodes, "parents ");
        updateHeights();
        this.positions = treeData.getPositions();
        this.selected = new boolean[treeData.getOrderLength() * 2];
        this.nodesColors = new Color[treeData.getOrderLength() * 2];
        //resetNodeColors();
        fireTreeUpdated();
        deselect(this.selected);
        updateSize();
    }

    public int getNodeSize() {
        if (this.treeData == null) {
            return 0;
        }
        return this.treeData.getOrderLength();
    }

    public void setAbsoluteHeight(boolean useAbsoluteHeight) {
        this.useAbsoluteHeight = useAbsoluteHeight;
    }

    protected TreeData getTreeData() {
        return this.treeData;
    }

    private void initializeParentNodeArray() {
        parentNodes = new int[this.treeData.nodesNumber()];

        for (int i = 0; i < this.treeData.getOrderLength(); i++) {
            if (this.treeData.getOrder(i) != -1) {
                parentNodes[this.treeData.getOrder(i)] = findParent(i);
            }
        }
    }

    private int findParent(int index) {
        int node = this.treeData.getOrder(index);
        for (int i = 0; i < this.treeData.nodesNumber(); i++) {
            if (this.treeData.getLeft(i) == node) {
                return i;
            }
        }
        for (int i = 0; i < this.treeData.nodesNumber(); i++) {
            if (this.treeData.getRight(i) == node) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Set the tree threshold
     */
    public void setZeroThreshold(float zeroThreshold) {
        this.zero_threshold = zeroThreshold;
    }

    /**
     * Returns the cluster row indices for the current distance threshold
     */
    public int[][] getClusterRowIndices() {
//        int k = this.getNumberOfTerminalNodes();
        int index;
        int[] endPoints;
        int[] rows;

        int testCnt = 0;
        int[] terminals;
        for (int i = 0; i < this.terminalNodes.length; i++) {
            if (this.terminalNodes[i] == true) {
                testCnt++;
            }
        }
        terminals = new int[testCnt];
        int terminalCnt = 0;

        for (int i = 0; i < this.terminalNodes.length; i++) {
            if (this.terminalNodes[i] == true) {
                terminals[terminalCnt] = i;
                terminalCnt++;
            }
        }

        int[][] clusters = new int[terminals.length][];

        for (int i = 0; i < clusters.length; i++) {
            index = terminals[i];
            if (index >= this.treeData.getOrderLength()) {
                endPoints = this.getSubTreeEndPointElements(index);

            } else {
                endPoints = new int[2];
                endPoints[1] = (int) this.positions[index];
                endPoints[0] = (int) this.positions[index];
            }

            rows = new int[endPoints[1] - endPoints[0] + 1];
            // rows = new int[endPoints[1]-endPoints[0]+1];

            for (int j = 0; j < rows.length; j++) {
                rows[j] = endPoints[0] + j;
            }
            clusters[i] = rows;
        }
        return clusters;
    }

    /**
     * Determines whether a data for computing a tree were supplied
     *
     * @return
     */
    public boolean hasData() {
        return (this.dataset != null);
    }

    /**
     * Sets horizontal margin (offset for sample tree)
     */
    public void setHorizontalOffset(int offset) {
        this.yOffset = offset;
    }

//    /**
//     *  finds min dist in tree, initializes zeroThreshold
//     */
//    private float findMinDistance(){
//        float min = Float.POSITIVE_INFINITY;
//        for(int i = 0; i < treeData.nodesNumber();i++){
//            min = Math.min(min, treeData.height[i]);
//        }
//        return min;
//    }
    /**
     * Sets node color.
     */
    public void setNodeColor(int node, Color color) {
        setSubTreeColor(node, color);
        repaint();
    }

    private void setSubTreeColor(int node, Color color) {
        this.nodesColors[node] = color;
        if (treeData.getLeft(node) != -1) {
            setNodeColor(treeData.getLeft(node), color);
        }
        if (treeData.getRight(node) != -1) {
            setNodeColor(treeData.getRight(node), color);
        }
    }

    /**
     * Clears all the colored tree nodes.
     */
    public void resetNodeColors() {
        for (int i = this.nodesColors.length; --i >= 0;) {
            this.nodesColors[i] = ColorGenerator.getRandomColor();
        }
        repaint();
    }

    /**
     * Returns the zero threshold attribute.
     */
    public double getZeroThreshold() {
        return zero_threshold;
    }

    /**
     * Returns the min distance on tree in pixels
     */
    @Override
    public int getMinDistance() {
        return min_pixels;
    }

    /**
     * Returns the max distance on tree in pixels
     */
    @Override
    public int getMaxDistance() {
        return max_pixels;
    }

    /**
     * Returns the minimum node distance
     */
    public double getMinNodeDistance() {
        return minHeight;
    }

    /**
     * Returns the maximum node distance
     */
    public double getMaxNodeDistance() {
        return maxHeight;
    }

    /**
     * Returns a boolean array indicating which nodes are terminal
     */
    public boolean[] getTerminalNodes() {
        return terminalNodes;
    }

    /**
     * Deselects the tree.
     */
    private void deselect(boolean[] selected) {
        for (int i = selected.length; --i >= 0;) {
            selected[i] = false;
        }
    }

    /**
     * Deselects all nodes
     */
    public void deselectAllNodes() {
        deselect(this.selected);
    }

//    /**
//     * Fills in an array by -1 values.
//     */
//    private void clear(int[] array) {
//        for (int i = array.length; --i >= 0;) {
//            array[i] = -1;
//        }
//    }
    /**
     * Calculates the current scale.
     */
    protected abstract double getScale();

    public void setOffsetX(int offset) {
        this.xOffset = offset;
    }

    public void setOffsetY(int offset) {
        this.yOffset = offset;
    }

    public void refreshPositions() {
        positions = treeData.getPositions();
    }

//    /**
//     * Returns heights shifted by min distance, corrects for distance polarity change
//     */
//    private float[] shiftHeights(float [] height, float minH){
//        for(int i = 0; i < height.length; i++)
//            height[i] = height[i] - minH;
//        return height;
//    }
    /**
     * Returns nodes heights in pixels.
     */
    private int[] getPixelHeights() {
        double scale = getScale();
        int[] heights = new int[treeData.getOrderLength() * 2];
        int node;
        int child_1, child_2;
        //  System.out.println("scale " + scale + " levels= " + height(treeData.node_order[0]));
        for (int i = treeData.getOrderLength() - 2; i >= 0; i--) {
            //  for (int i =0; i <  nodeOrder.length-1; i++) { 
            node = treeData.getOrder(i);
            child_1 = treeData.getLeft(node);
            child_2 = treeData.getRight(node);

            if (!this.useAbsoluteHeight) {
                heights[node] = (int) (Math.max(heights[child_1], heights[child_2]) + Math.max(Math.min(Math.round(treeData.getHeight(node) * scale), max_pixels), min_pixels));
            } else {
                // pHeights[node] = pHeights[parentNodes[node]] - Math.max(Math.min((int)Math.round(height[node]*scale), max_pixels), min_pixels);
                heights[node] = (int) Math.max(Math.round(treeData.getHeight(node) * scale), min_pixels);
                //  System.out.println(pHeights[node] + " p " + pHeights[parentNodes[node]] + " ch1= " + pHeights[child_1] + ", ch2=" + pHeights[child_2] + " node " + pHeights[node]);

            }

        }
        return heights;
    }

    private int height(int node) {
        int child_1 = treeData.getLeft(node);
        int child_2 = treeData.getRight(node);
        System.out.print("node " + node);
        /*
         * int child_1 = treeData.childLeft[treeData.node_order[node]]; int
         * child_2 = treeData.childRight[treeData.node_order[node]];
         *
         *
         * System.out.println("h= "+node+ " "+ treeData.node_order[node]+ "ci
         * "+child_1+ "cii "+child_2+" p "+parentNodes[node]);
         * System.out.println(" c1 "+ treeData.childLeft.length+ " c2 "+
         * treeData.childRight.length);
         */
        if (node == -1) {
            return 0;
        } else {
            return (height(treeData.getLeft(child_1)) + height(treeData.getRight(child_2)) + 1);
        }
    }

    private int getNodeRaisedHeight(int index, int max) {
        int node = treeData.getOrder(index);
        int heightChange = max - pHeights[node];
        int sum = 0;
        while (parentNodes[node] != 0) {
            int pn = parentNodes[node];
            sum += (Math.min(pHeights[pn] - pHeights[treeData.getLeft(pn)], pHeights[pn] - pHeights[treeData.getRight(pn)]));
            node = parentNodes[node];
        }
        heightChange -= sum;
        return heightChange;
    }

    /**
     * Paints the tree into specified graphics.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        if (this.treeData == null) {
            //no data
            return;
        }

        if (this.treeData.getOrderLength() == 1) {
            g2.setColor(Color.black);
            g2.drawLine(0, 0, 10, 0);
        }
        //moves the [0;0] point 
        //System.out.println(this.getClass().toString() + "translate " + xOffset + " ; " + yOffset);
        g2.translate(xOffset, yOffset);

        for (int i = 0; i < this.terminalNodes.length; i++) {
            terminalNodes[i] = false;
        }

        if (this.treeData.getOrderLength() < 2) {
            return;
        }

        setOrientation(g2);
        int max_node_height = getMaxTreeValue();
        //printArray(pHeights, "heights");
        ///  System.out.println("max height " + max_node_height + " dist to scale " + distToScale + " samples  " + experiment.getNumberOfColumns() + "orientation " + orientation);

        int node;
        int child_1, child_2;
        int child_1_x1, child_1_x2, child_1_y;
        int child_2_x1, child_2_x2, child_2_y;

        nodeRaised = new int[this.treeData.getOrderLength() - 1];
        if (actualArms) {
            for (int i = 0; i < treeData.getOrderLength() - 1; i++) {
                try {
                    nodeRaised[i] = getNodeRaisedHeight(i, max_node_height);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ///System.out.println("max height " + max_node_height);
        //  stepSize = 20;
        for (int i = 0; i < this.treeData.getOrderLength() - 1; i++) {
            node = this.treeData.getOrder(i);
            child_1 = this.treeData.getLeft(node);
            child_2 = this.treeData.getRight(node);
            //Dump.array(pHeights, "heights");
            child_1_x1 = (max_node_height - this.pHeights[node] - this.nodeRaised[i]) * sign;
            child_1_x2 = (max_node_height - this.pHeights[child_1] - this.nodeRaised[i]) * sign;
            child_1_y = (int) (this.positions[child_1] * this.stepSize) + this.stepSize;
            child_2_x1 = (max_node_height - this.pHeights[node] - this.nodeRaised[i]) * sign;
            child_2_x2 = (max_node_height - this.pHeights[child_2] - this.nodeRaised[i]) * sign;
            child_2_y = (int) (this.positions[child_2] * this.stepSize) + this.stepSize;

            /*
             * System.out.println("lx1 "+child_1_x1 +", lx2 " +child_1_x2 +",
             * ly"+child_1_y+ ", rx1 "+child_2_x1+ ", rx2 "+child_2_x2+ ", ry
             * "+child_2_y );
             */
            // System.out.println("render node " + node + " col " + g.getColor() + " - h=" + treeData.height[node]+" x "+child_1_x1 );


            // System.out.println("color "+this.nodesColors[node]);
            if (this.nodesColors[node] == null) {

                if (this.treeData.getHeight(node) >= zero_threshold) {
                    ///   System.out.println("tr height= "+this.treeData.height[node] );
                    g2.setColor(lineColor);
                    this.terminalNodes[node] = false;
                    if (this.pHeights[child_1] == 0) {
                        this.terminalNodes[child_1] = true;
                    }
                    if (this.pHeights[child_2] == 0) {
                        this.terminalNodes[child_2] = true;
                    }
                } else {
                    g2.setColor(belowThrColor);
                    this.terminalNodes[node] = false;
                    if (this.treeData.getHeight(parentNodes[node]) >= zero_threshold) {
                        drawWedge(g2, node, child_1_x1, child_2_x1, child_1_y, child_2_y);
                        this.terminalNodes[node] = true;
                        this.terminalNodes[child_1] = false;
                        this.terminalNodes[child_2] = false;
                    }
                }
            } else {
                g2.setColor(this.nodesColors[node]);
                if (this.treeData.getHeight(node) >= zero_threshold) {
                    //  g.setColor(lineColor);
                    this.terminalNodes[node] = false;
                    if (this.pHeights[child_1] == 0) {
                        this.terminalNodes[child_1] = true;
                    }
                    if (this.pHeights[child_2] == 0) {
                        this.terminalNodes[child_2] = true;
                    }
                } else {
                    //   g.setColor(belowThrColor);
                    this.terminalNodes[node] = false;

                    if (this.treeData.getHeight(parentNodes[node]) > zero_threshold) {
                        ///System.out.println("render node x1= " + child_1_x1 + xOffset + "x2=" + child_2_x1 + xOffset + " y " + child_1_y + " y2= " + child_2_y);
                        drawWedge(g2, node, child_1_x1 + xOffset, child_2_x1 + xOffset, child_1_y, child_2_y);
                        this.terminalNodes[node] = true;
                        this.terminalNodes[child_1] = false;
                        this.terminalNodes[child_2] = false;
                    }
                }
            }

            if (this.selected[node]) {
                g2.setColor(selectedLineColor);
            }
            drawArms(g2, child_1_x1, child_1_x2, child_1_y, child_2_x1, child_2_x2, child_2_y);
        }
    }

    protected abstract void setOrientation(Graphics2D g2);

    protected abstract void drawArms(Graphics2D g2, int child_1_x1, int child_1_x2, int child_1_y, int child_2_x1, int child_2_x2, int child_2_y);

    protected abstract void drawEdge(Graphics g, int[] xs, int[] ys, int x1, int x2, int y1, int y2, int k, int k1);

    protected abstract void updateTreeSize();

    public String getMinHeightDisplay() {
        return String.valueOf(decimalFormat.format(nodeHeightOffset));
    }

    public String getMidHeightDisplay() {
        return String.valueOf(decimalFormat.format((similarityFactor * (maxHeight - nodeHeightOffset) + nodeHeightOffset) / 2));
    }

    public String getMaxHeightDisplay() {
        return String.valueOf(decimalFormat.format(similarityFactor * (maxHeight - nodeHeightOffset)));
    }

    public void drawWedge(Graphics g, int node, int x1, int x2, int y1, int y2) {
        int[] xs = new int[3];
        int[] ys = new int[3];


        int k = node;
        int k1 = node;
        while (this.treeData.getLeft(k) != -1) {
            k = this.treeData.getLeft(k);
        }
        while (this.treeData.getRight(k1) != -1) {
            k1 = this.treeData.getRight(k1);
        }

        drawEdge(g, xs, ys, x1, x2, y1, y2, k, k1);

        Color color = g.getColor();
        Graphics2D g2 = (Graphics2D) g;
        Composite composite = g2.getComposite();

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g.setColor(Color.blue);
        g.fillPolygon(new Polygon(xs, ys, 3));
        g.setColor(color);
        g2.setComposite(composite);
    }

    /**
     * Updates the tree size with specified element size.
     */
    @Override
    public void updateSize() {
        if (treeData == null) {
            return;
        }
        updateTreeSize();
        updateHeights();
        repaint();
    }

    private void updateHeights() {
        this.pHeights = treeData == null ? null : getPixelHeights();
    }

    private int getMaxTreeValue() {
        if (pHeights == null) {
            return 0;
        }
        return pHeights[treeData.getOrder(treeData.getOrderLength() - 2)];
    }

    /**
     * Sets the tree sizes.
     */
    public final void setSizes(int width, int height) {
        this.size.width = width;
        this.size.height = height;
        /**
         * we want exactly this size, when windows is enlarged the heatmap
         * should be bigger
         */
        setPreferredSize(this.size);
        setMinimumSize(this.size);
        setSize(this.size);
    }

    /**
     * Returns the endpoint row indices for the subtree below a node
     */
    private int[] getSubTreeEndPointElements(int node) {
        int[] endPoints = new int[2];
        endPoints[0] = (int) positions[node];
        endPoints[1] = (int) positions[node];
        int ptr = node;

        while (this.treeData.getLeft(ptr) != -1) {
            ptr = this.treeData.getLeft(ptr);
        }

        endPoints[0] = (int) positions[ptr];

        ptr = node;
        while (this.treeData.getRight(ptr) != -1) {
            ptr = this.treeData.getRight(ptr);
        }
        endPoints[1] = (int) positions[ptr];

        return endPoints;
    }

    /**
     * Selects node by specified x and y coordinates.
     */
    private void selectNode(int x, int y) {
        if (selected == null) {
            return;
        }
        deselect(selected);
        TreeCluster cluster = new TreeCluster(findNode(x, y), Integer.MAX_VALUE, Integer.MIN_VALUE);
        selectNode(cluster, cluster.root);
        fireClusterSelected(this, cluster);
        repaint();
    }

    /**
     * Selects tree for specified root node.
     */
    private void selectNode(TreeCluster cluster, int node) {
        if (node == -1) {
            cluster.firstElem = -1;
            cluster.lastElem = -1;
            return;
        }
        this.selected[node] = true;
        if (this.treeData.getLeft(node) != -1) {
            selectNode(cluster, this.treeData.getLeft(node));
        } else {
            if (this.positions[node] < cluster.firstElem) {
                cluster.firstElem = (int) this.positions[node];
            }
            if (this.positions[node] > cluster.lastElem) {
                cluster.lastElem = (int) this.positions[node];
            }
            cluster.setFinalSize();
        }
        if (this.treeData.getRight(node) != -1) {
            selectNode(cluster, this.treeData.getRight(node));
        }
    }

    protected abstract boolean nodeFound(int x, int y, int child_1_x1, int child_1_y, int child_2_y);

    /**
     * Returns index of a node by specified x and y coordinates.
     */
    private int findNode(int x, int y) {
        //  if (this.orientation == HORIZONTAL) {
        x -= xOffset; //add origin offset
        y -= yOffset;
        //  }
        int max_node_height = getMaxTreeValue();
        int node;
        int child_1, child_2;
        int child_1_x1, child_1_y;
        int child_2_x1, child_2_y;
        for (int i = 0; i < this.treeData.getOrderLength() - 1; i++) {
            node = this.treeData.getOrder(i);
            child_1 = this.treeData.getLeft(node);
            child_2 = this.treeData.getRight(node);
            child_1_x1 = (max_node_height - this.pHeights[node] - this.nodeRaised[i]);
            child_1_y = (int) (this.positions[child_1] * this.stepSize) + this.stepSize / 2;
            //child_2_x1 = (max_node_height - this.pHeights[node] - this.nodeRaised[i]);
            child_2_y = (int) (this.positions[child_2] * this.stepSize) + this.stepSize / 2;
            if (nodeFound(x, y, child_1_x1, child_1_y, child_2_y)) {
                return node;
            }
        }
        return -1;
    }

    public boolean fireClusterSelected(AbstractTree source, TreeCluster cluster) {
        TreeListener[] listeners;

        if (treeListeners != null) {
            listeners = treeListeners.getListeners(TreeListener.class);
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].clusterSelected(source, cluster, dataset);
            }
        }
        return true;
    }
    
    /**
     * Fired when tree dimensions (or data) has changed
     */
    public void fireTreeUpdated() {
        TreeListener[] listeners;
        System.out.println("tree updated");
        
        if (treeListeners != null) {
            
            listeners = treeListeners.getListeners(TreeListener.class);
            System.out.println("listeners size "+listeners.length);
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].treeUpdated(this, size.width, size.height);
            }
        }
    }

    public void addTreeListener(TreeListener listener) {
        treeListeners.add(TreeListener.class, listener);
    }

    public void removeTreeListener(TreeListener listener) {
        treeListeners.remove(TreeListener.class, listener);
    }

    /**
     * The class to listen to mouse events.
     */
    private class Listener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                return;
            }
            selectNode(e.getX(), e.getY());
        }
    }
}
