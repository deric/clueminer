package org.clueminer.clustering.api.dendrogram;

/**
 *
 * @author Tomas Barton
 */
public interface TreeData {

    /**
     * Return number of terminal nodes (leaves)
     *
     * @return number of tree leaves
     */
    public int numLeaves();
    
    /**
     * 
     * @return number of levels in tree
     */
    public int treeLevels();
}
