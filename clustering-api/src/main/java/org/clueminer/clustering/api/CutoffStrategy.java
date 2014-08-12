package org.clueminer.clustering.api;

/**
 *
 * @author Tomas Barton
 */
public interface CutoffStrategy {

    /**
     * A human readable name of cutoff strategy
     *
     * @return name of the strategy
     */
    String getName();

    /**
     * Finds optimal dendrogram cutoff according to given strategy
     *
     * @param hclust hierarchical clustering result
     * @return tree cutoff
     */
    double findCutoff(HierarchicalResult hclust);

    /**
     * Set evaluation function
     *
     * @param evaluator
     */
    void setEvaluator(ClusterEvaluator evaluator);
}
