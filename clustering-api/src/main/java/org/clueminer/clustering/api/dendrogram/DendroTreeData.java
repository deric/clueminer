package org.clueminer.clustering.api.dendrogram;

/**
 *
 * @author Tomas Barton
 */
public interface DendroTreeData {

    /**
     * Return number of terminal nodes (leaves)
     *
     * @return number of tree leaves
     */
    int numLeaves();

    /**
     * Total number of tree nodes including leaves.
     *
     * @return
     */
    int numNodes();

    /**
     *
     * @return number of levels in tree
     */
    int treeLevels();

    /**
     *
     * @return tree node
     */
    DendroNode getRoot();

    /**
     *
     * @param root
     */
    void setRoot(DendroNode root);

    /**
     *
     * @return first leaf
     */
    DendroNode first();

    /**
     * Print tree to stdout
     */
    void print();

}
