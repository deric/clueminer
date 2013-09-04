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

    /**
     *
     * @return true when right node exists
     */
    public boolean hasRight();

    /**
     * (Sub)tree height
     *
     * @return number of levels under this node
     */
    public double height();

    /**
     * If root node, parent is null
     *
     * @return parent node
     */
    public DendroNode getParent();

    public void setParent(DendroNode parent);
}
