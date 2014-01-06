package org.clueminer.clustering.api.dendrogram;

/**
 *
 * @author Tomas Barton
 */
public interface DendroNode {

    /**
     *
     * @return true when is a leaf node
     */
    public boolean isLeaf();

    /**
     *
     * @return true when node is top of a dendrogram tree
     */
    public boolean isRoot();

    /**
     *
     * @return left node if any, otherwise null
     */
    public DendroNode getLeft();

    public void setLeft(DendroNode left);

    /**
     *
     * @return true when left node exists
     */
    public boolean hasLeft();

    /**
     *
     * @return right node if any, otherwise null
     */
    public DendroNode getRight();

    public void setRight(DendroNode right);

    /**
     *
     * @return true when right node exists
     */
    public boolean hasRight();

    /**
     * (Sub)tree level
     *
     * @return number of levels under this node
     */
    public int level();

    /**
     * If root node, parent is null
     *
     * @return parent node
     */
    public DendroNode getParent();

    public void setParent(DendroNode parent);

    /**
     *
     * @return number of children
     */
    public int childCnt();

    public double getHeight();

    public void setHeight(double height);

    /**
     * Position of last node is also width of the tree
     *
     * @return width of tree
     */
    public double getPosition();

    public void setPosition(double value);

    public void setId(int id);

    public int getId();
}
