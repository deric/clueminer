package org.clueminer.clustering.api.dendrogram;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 * @author Tomas Barton
 */
public interface DendroNode {

    /**
     *
     * @return true when is a leaf node
     */
    boolean isLeaf();

    /**
     *
     * @return true when node is top of a dendrogram tree
     */
    boolean isRoot();

    /**
     *
     * @return left node if any, otherwise null
     */
    DendroNode getLeft();

    void setLeft(DendroNode left);

    /**
     *
     * @return true when left node exists
     */
    boolean hasLeft();

    /**
     *
     * @return right node if any, otherwise null
     */
    DendroNode getRight();

    void setRight(DendroNode right);

    /**
     *
     * @return true when right node exists
     */
    boolean hasRight();

    /**
     * (Sub)tree level
     *
     * @return number of levels under this node
     */
    int level();

    /**
     * In case that tree construction is finished, we can cache tree levels
     *
     * @param level
     */
    void setLevel(int level);

    /**
     * If root node, parent is null
     *
     * @return parent node
     */
    DendroNode getParent();

    void setParent(DendroNode parent);

    /**
     *
     * @return number of children
     */
    int childCnt();

    double getHeight();

    void setHeight(double height);

    /**
     * Position of last node is also width of the tree
     *
     * @return width of tree
     */
    double getPosition();

    void setPosition(double value);

    void setId(int id);

    int getId();

    /**
     * Mapping index, used for mapping to Instances or Attributes
     *
     * @return
     */
    int getIndex();

    /**
     * printing helper
     *
     * @param out
     * @param isRight
     * @param indent
     * @throws IOException
     */
    void printTree(OutputStreamWriter out, boolean isRight, String indent) throws IOException;
}
