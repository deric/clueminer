package org.clueminer.clustering.api.dendrogram;

import org.clueminer.clustering.api.HierarchicalResult;

/**
 * Hierarchical clustering does not specify in which order clusters should be
 * displayed. Better ordering of dendrogram leaves might lead to better
 * understanding of data structure.
 *
 * @author Tomas Barton
 */
public interface OptimalTreeOrder {

    /**
     * Unique human readable name of the method
     *
     * @return method name
     */
    String getName();

    void optimize(HierarchicalResult clustering);

    /**
     *
     * @param clustering
     * @param reverse    whether to use reverse ordering
     */
    void optimize(HierarchicalResult clustering, boolean reverse);
}
