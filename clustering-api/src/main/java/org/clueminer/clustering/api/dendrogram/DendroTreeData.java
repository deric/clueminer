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
    public int numLeaves();

    /**
     * Total number of tree nodes including leaves.
     *
     * @return
     */
    public int numNodes();

    /**
     *
     * @return number of levels in tree
     */
    public int treeLevels();
    
    /**
     * 
     * @return tree node
     */
    public DendroNode getRoot();
}
