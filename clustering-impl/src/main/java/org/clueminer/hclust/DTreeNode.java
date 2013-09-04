package org.clueminer.hclust;

import org.clueminer.clustering.api.dendrogram.DendroNode;

/**
 *
 * @author Tomas Barton
 */
public class DTreeNode implements DendroNode {

    private boolean leaf = false;
    private boolean root = false;
    private DendroNode left;
    private DendroNode right;
    private DendroNode parent;

    public DTreeNode() {
    }

    public DTreeNode(boolean root) {
        this.root = root;
    }

    @Override
    public boolean isLeaf() {
        return leaf;
    }

    @Override
    public boolean isRoot() {
        return root;
    }

    @Override
    public DendroNode getLeft() {
        return left;
    }

    @Override
    public boolean hasLeft() {
        return left != null;
    }

    @Override
    public DendroNode getRight() {
        return right;
    }

    @Override
    public boolean hasRight() {
        return right != null;
    }

    @Override
    public double height() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DendroNode getParent() {
        return parent;
    }

    @Override
    public void setParent(DendroNode parent) {
        this.parent = parent;
    }

    @Override
    public int childCnt() {
        int cnt = 0;
        if (hasLeft()) {
            cnt += 1 + getLeft().childCnt();
        }
        if (hasRight()) {
            cnt += 1 + getRight().childCnt();
        }
        return cnt;
    }
}
