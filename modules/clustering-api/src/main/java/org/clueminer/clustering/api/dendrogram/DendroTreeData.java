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
     * Number of distinct node heights within a tree
     *
     * @param tolerance threshold in height difference for considering height as
     * the same
     * @return
     */
    int distinctHeights(double tolerance);

    /**
     * Distinct levels with default tolerance
     *
     * @return number of distinct tree levels within a tree (without leaves)
     */
    int distinctHeights();

    /**
     *
     * @return tree node
     */
    DendroNode getRoot();

    /**
     * Set node as tree root
     *
     * @param root
     * @return itself
     */
    DendroNode setRoot(DendroNode root);

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
     * Create mapping of leaves and map one leaf as noise
     *
     * @param n
     * @param node - root
     * @param noise leaf with noise
     * @return
     */
    int[] createMapping(int n, DendroNode node, DendroNode noise);

    /**
     * Print tree to stdout
     */
    void print();

    /**
     * Print tree to stdout, nodes are printed in levels according to their
     * heights. Heights should be integers otherwise levels are not printed
     * correctly.
     */
    void printWithHeight();

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
     * Check whether mapping was set
     *
     * @return true when mapping to actual tree order exists
     */
    boolean hasMapping();

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

    /**
     * Whether the tree leaves contain clusters.
     *
     * @return
     */
    public boolean containsClusters();

}
