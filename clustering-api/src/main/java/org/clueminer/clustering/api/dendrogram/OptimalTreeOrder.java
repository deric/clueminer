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

    void optimize(HierarchicalResult clustering);
}
