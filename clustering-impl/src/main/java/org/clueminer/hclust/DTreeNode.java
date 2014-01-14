package org.clueminer.hclust;

import org.clueminer.clustering.api.dendrogram.DendroNode;

/**
 *
 * @author Tomas Barton
 */
public class DTreeNode implements DendroNode {

    private boolean root = false;
    protected DendroNode left;
    protected DendroNode right;
    protected DendroNode parent;
    private double height;
    private double position;
    private int level = -1;
    private int id;

    public DTreeNode() {
    }

    public DTreeNode(int id) {
        this.id = id;
    }

    public DTreeNode(DendroNode parent) {
        this.parent = parent;
    }

    public DTreeNode(boolean root) {
        this.root = root;
    }

    /**
     * Leaf doesn't have any children
     *
     * @return
     */
    @Override
    public boolean isLeaf() {
        return !hasLeft() && !hasRight();
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
        if (level == -1) {
            if (hasLeft() && hasRight()) {
                return 1 + Math.max(getLeft().level(), getRight().level());
            } else if (hasLeft() && !hasRight()) {
                return 1 + getLeft().level();
            } else if (!hasLeft() && hasRight()) {
                return 1 + getRight().level();
            } else {
                return 0;
            }
        } else {
            return level;
        }
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

    @Override
    public double getPosition() {
        return position;
    }

    @Override
    public void setPosition(double position) {
        this.position = position;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ").append("#").append(getId()).append(getPosition()).append(", ").append(level).append(" ]");
        return sb.toString();
    }

}
