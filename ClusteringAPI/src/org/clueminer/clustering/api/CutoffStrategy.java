package org.clueminer.clustering.api;

/**
 *
 * @author Tomas Barton
 */
public interface CutoffStrategy {

    /**
     * Finds optimal dendrogram cutoff according to given strategy
     *
     * @param hclust hierarchical clustering result
     * @return tree cutoff
     */
    public double findCutoff(HierarchicalResult hclust);
}
