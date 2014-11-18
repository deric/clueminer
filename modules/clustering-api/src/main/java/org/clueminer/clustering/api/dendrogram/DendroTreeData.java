package org.clueminer.clustering.api.dendrogram;

/**
 * Dendrogram tree structure
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
     * Left-most leaf
     *
     * @return first leaf
     */
    DendroNode first();

    /**
     * Create mapping of leaves
     *
     * @param n
     * @param node - root
     * @return
     */
    int[] createMapping(int n, DendroNode node);

    /**
     * Print tree to stdout
     */
    void print();

    /**
     * Print tree with given root
     *
     * @param treeRoot
     */
    void print(DendroNode treeRoot);

    /**
     * Set mapping to instances indexes
     *
     * @param mapping
     */
    void setMapping(int[] mapping);

    /**
     * Return mapping of leaves to indexes in dataset
     *
     * @return
     */
    int[] getMapping();

    /**
     * Mapped index
     *
     * @param i
     * @return instance index
     */
    int getMappedId(int i);

    /**
     *
     * @param i th leaf
     * @return
     */
    DendroNode getLeaf(int i);

    /**
     * Set leaf at given position
     *
     * @param i
     * @param node
     */
    void setLeaf(int i, DendroNode node);

    /**
     * Set array of leaves nodes
     *
     * @param leaves
     */
    void setLeaves(DendroNode[] leaves);

    /**
     * Update precomputed position of the node
     *
     * @param node
     * @return
     */
    double updatePositions(DendroNode node);

}
