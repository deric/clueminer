package org.clueminer.hclust;

import java.io.IOException;
import java.io.OutputStreamWriter;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.openide.util.Exceptions;

/**
 * Represents a tree structure
 *
 * @author Tomas Barton
 */
public class DynamicTreeData implements DendroTreeData {

    private DendroNode root;
    private int[] mapping;
    private DendroNode[] leaves;

    public DynamicTreeData() {

    }

    public DynamicTreeData(DendroNode root) {
        this.root = root;
    }

    @Override
    public int numLeaves() {
        if (root != null) {
            return root.childCnt();
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
    public int numNodes() {
        return root.childCnt();
    }

    @Override
    public DendroNode getRoot() {
        return root;
    }

    @Override
    public void setRoot(DendroNode root) {
        this.root = root;
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

    public void printTree(OutputStreamWriter out) throws IOException {
        DendroNode node = getRoot().getLeft();
        if (node != null) {
            node.printTree(out, false, "");
        }
        if (getRoot() != null) {
            ((DTreeNode) getRoot()).printNodeValue(out);
        }
        node = getRoot().getRight();
        if (node != null) {
            node.printTree(out, true, "");
        }
    }

    @Override
    public void print() {
        try {
            OutputStreamWriter out = new OutputStreamWriter(System.out);
            printTree(out);
            out.flush();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void printCanonical() {
        try {
            OutputStreamWriter out = new OutputStreamWriter(System.out);
            printCanonicalTree(out);
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

    @Override
    public int[] getMapping() {
        return mapping;
    }

    private void printCanonicalTree(OutputStreamWriter out) throws IOException {
        DendroNode node = getRoot().getLeft();
        if (node != null) {
            node.printCanonicalTree(out, false, "");
        }
        if (getRoot() != null) {
            ((DTreeNode) getRoot()).printCanonicalValue(out);
        }
        node = getRoot().getRight();
        if (node != null) {
            node.printCanonicalTree(out, true, "");
        }
        out.flush();
    }

}
