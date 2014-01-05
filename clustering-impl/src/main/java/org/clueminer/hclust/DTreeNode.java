package org.clueminer.hclust;

import org.clueminer.clustering.api.dendrogram.DendroNode;

/**
 *
 * @author Tomas Barton
 */
public class DTreeNode implements DendroNode {

    private final boolean leaf = false;
    private boolean root = false;
    protected DendroNode left;
    protected DendroNode right;
    protected DendroNode parent;
    private double height;
    private int level = -1;

    public DTreeNode() {
    }

    public DTreeNode(DendroNode parent) {
        this.parent = parent;
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
    public void setLeft(DendroNode left) {
        this.left = left;
    }

    @Override
    public void setRight(DendroNode right) {
        this.right = right;
    }

    @Override
    public int level() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public void setHeight(double height) {
        this.height = height;
    }

}
