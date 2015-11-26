package org.clueminer.hclust;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Stack;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.openide.util.Exceptions;

/**
 * Represents a tree structure
 *
 * @author Tomas Barton
 */
public class DynamicTreeData implements DendroTreeData {

    private DendroNode root;
    protected int[] mapping;
    protected DendroNode[] leaves;

    public DynamicTreeData() {

    }

    /**
     * When given expected size we might avoid reallocation of memory
     *
     * @param root
     * @param hintSize estimated number number of nodes (doesn't have to be
     * accurate)
     */
    public DynamicTreeData(DendroNode root, int hintSize) {
        this.root = root;
        ensureCapacity(hintSize);
    }

    public DynamicTreeData(DendroNode root) {
        this.root = root;
    }

    @Override
    public int numLeaves() {
        if (root != null) {
            //avoid recursive computations if possible
            if (mapping == null) {
                //some reasonable default, array will be shrinked to needed size
                //when we know how many nodes we actually have
                createMapping(5, root);
            }
            return mapping.length;
        }
        return 0;
    }

    @Override
    public int treeLevels() {
        if (root != null) {
            return root.level();
        }
        return 0;
    }

    @Override
    public int distinctHeights() {
        if (root != null) {
            return distinctHeights(1e-9);
        }
        return 0;
    }

    /**
     * Count distinct node heights within a tree with given tolerance for Double
     * value difference
     *
     * @param tolerance
     * @return
     */
    @Override
    public int distinctHeights(double tolerance) {
        if (root != null) {
            double[] heights = new double[numNodes() - numLeaves()];
            collectHeights(heights, root, 0);
            Arrays.sort(heights);
            int cnt = 0;
            //count number of distinct values
            double prev = Double.MAX_VALUE;
            for (int i = 0; i < heights.length; i++) {
                double height = heights[i];
                if (Math.abs(height - prev) > tolerance) {
                    cnt++;
                }
                prev = height;
            }
            return cnt;
        }
        return 0;
    }

    /**
     * Collect node heights into an array (without leaves)
     *
     * @param heights
     * @param node
     * @param idx
     * @return
     */
    private int collectHeights(double[] heights, DendroNode node, int idx) {
        if (node.isLeaf()) {
            //nothing to do
            return idx;
        }
        int nextId;
        heights[idx++] = node.getHeight();
        nextId = idx;
        if (node.hasLeft()) {
            nextId = collectHeights(heights, node.getLeft(), nextId);
        }
        if (node.hasRight()) {
            nextId = collectHeights(heights, node.getRight(), nextId);
        }

        return nextId;
    }

    @Override
    public int numNodes() {
        return root.childCnt() + 1;
    }

    @Override
    public DendroNode getRoot() {
        return root;
    }

    @Override
    public DendroNode setRoot(DendroNode node) {
        this.root = node;
        return this.root;
    }

    @Override
    public DendroNode first() {
        if (root == null) {
            throw new RuntimeException("root is empty");
        }
        DendroNode current = root;
        while (!current.isLeaf()) {
            current = current.getLeft();
        }
        return current;
    }

    /**
     * Given a binary tree, prints out all of its root-to-leaf paths, one per
     * line. Uses a recursive helper to do the work.
     */
    public void printPaths() {
        int[] path = new int[treeLevels() + 1];
        printPaths(root, path, 0);
    }

    /**
     * Recursive printPaths helper -- given a node, and an array containing the
     * path from the root node up to but not including this node, prints out all
     * the root-leaf paths.
     */
    private void printPaths(DendroNode node, int[] path, int pathLen) {
        if (node == null) {
            return;
        }

        // append this node to the path array
        path[pathLen] = node.getId();
        pathLen++;

        // it's a leaf, so print the path that led to here
        if (node.getLeft() == null && node.getRight() == null) {
            printArray(path, pathLen);
        } else {
            // otherwise try both subtrees
            printPaths(node.getLeft(), path, pathLen);
            printPaths(node.getRight(), path, pathLen);
        }
    }

    /**
     * Utility that prints ints from an array on one line.
     */
    private void printArray(int[] ints, int len) {
        int i;
        for (i = 0; i < len; i++) {
            System.out.print(ints[i] + " ");
        }
        System.out.println();
    }

    public void printTree(OutputStreamWriter out, DendroNode treeRoot) throws IOException {
        DendroNode node = treeRoot.getLeft();
        if (node != null) {
            node.printTree(out, false, "");
        }
        ((DTreeNode) treeRoot).printNodeValue(out);

        node = treeRoot.getRight();
        if (node != null) {
            node.printTree(out, true, "");
        }
    }

    @Override
    public void print(DendroNode treeRoot) {
        try {
            OutputStreamWriter out = new OutputStreamWriter(System.out);
            printTree(out, treeRoot);
            out.flush();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void printTreeWithHeight(OutputStreamWriter out, DendroNode treeRoot) throws IOException {
        DendroNode node = treeRoot.getLeft();
        if (node != null) {
            node.printTreeWithHeight(out, false, "");
        }
        ((DTreeNode) treeRoot).printNodeValue(out);

        node = treeRoot.getRight();
        if (node != null) {
            node.printTreeWithHeight(out, true, "");
        }
    }

    @Override
    public void printWithHeight() {
        try {
            OutputStreamWriter out = new OutputStreamWriter(System.out);
            printTreeWithHeight(out, getRoot());
            out.flush();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void print() {
        try {
            OutputStreamWriter out = new OutputStreamWriter(System.out);
            printTree(out, getRoot());
            out.flush();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void setMapping(int[] mapping) {
        this.mapping = mapping;
    }

    @Override
    public DendroNode getLeaf(int i) {
        if (leaves != null && i < leaves.length) {
            return leaves[i];
        }
        return null;
    }

    @Override
    public void setLeaves(DendroNode[] leaves) {
        this.leaves = leaves;
    }

    @Override
    public int getMappedId(int i) {
        if (mapping != null && i < mapping.length) {
            return mapping[i];
        }
        return -1;
    }

    /**
     * @TODO maybe implement node array reallocation (normally we know how many
     * nodes we have)
     * @param i
     * @param node
     */
    @Override
    public void setLeaf(int i, DendroNode node) {
        if (leaves != null) {
            leaves[i] = node;
        } else {
            throw new RuntimeException("leaves mapping was not initialized");
        }
    }

    /**
     * In-order tree walk to mark default order of instances
     *
     * @param n
     * @param node - root of the tree
     * @return
     */
    @Override
    public int[] createMapping(int n, DendroNode node) {
        Stack<DendroNode> stack = new Stack<>();
        int i = 0;
        ensureCapacity(n);
        while (!stack.isEmpty() || node != null) {
            if (node != null) {
                stack.push(node);
                node = node.getLeft();
            } else {
                node = stack.pop();
                if (node.isLeaf()) {
                    node.setPosition(i);
                    if (i >= mapping.length) {
                        int req = (int) (i * 1.618);
                        if (req <= i) {
                            req = i + 1;
                        }
                        ensureCapacity(req);
                    }
                    leaves[i] = node;
                    mapping[i] = node.getIndex();
                    i++;
                    //System.out.println((i - 1) + " -> " + mapping[(i - 1)]);
                }
                node = node.getRight();
            }
        }
        //trim arrays only to required capacity
        ensureCapacity(i);
        return mapping;
    }

    @Override
    public int[] createMapping(int n, DendroNode node, DendroNode noise) {
        return createMapping(n, node);
    }

    /**
     * Creates mapping and leaves of required capacity
     *
     * @param capacity
     */
    protected final void ensureCapacity(int capacity) {
        if (mapping == null) {
            mapping = new int[capacity];
            leaves = new DendroNode[capacity];
            return;
        }
        if (capacity == mapping.length) {
            return;
        }
        int toCopy;
        if (capacity < mapping.length) {
            toCopy = capacity;
        } else {
            toCopy = mapping.length;
        }
        //mapping
        int[] newData = new int[capacity];
        System.arraycopy(mapping, 0, newData, 0, toCopy);
        mapping = newData;
        //nodes
        DendroNode[] newNodes = new DendroNode[capacity];
        System.arraycopy(leaves, 0, newNodes, 0, toCopy);
        leaves = newNodes;
    }

    @Override
    public int[] getMapping() {
        return mapping;
    }

    /**
     * Check whether mapping was already created
     *
     * @return
     */
    @Override
    public boolean hasMapping() {
        return mapping != null;
    }

    /**
     * Recursive tree nodes positions update
     *
     * @TODO move this methods to tree itself
     * @param node
     * @return
     */
    @Override
    public double updatePositions(DendroNode node) {
        if (node.isLeaf()) {
            return node.getPosition();
        }
        //System.out.println(node.getId() + "L: " + node.getLeft() + ", R: " + node.getRight());
        double position = (updatePositions(node.getLeft()) + updatePositions(node.getRight())) / 2.0;
        node.setPosition(position);
        return position;
    }

    @Override
    public boolean containsClusters() {
        return false;
    }
}
