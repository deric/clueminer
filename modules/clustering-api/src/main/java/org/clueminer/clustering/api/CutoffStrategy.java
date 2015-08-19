package org.clueminer.clustering.api;

import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public interface CutoffStrategy<E extends Instance, C extends Cluster<E>> {

    /**
     * A human readable name of cutoff strategy
     *
     * @return name of the strategy (must be unique)
     */
    String getName();

    /**
     * Finds optimal dendrogram cutoff according to given strategy
     *
     * @param hclust hierarchical clustering result
     * @param params optional parameter of the method
     * @return tree cutoff
     */
    double findCutoff(HierarchicalResult hclust, Props params);

    /**
     * Set evaluation function
     *
     * @param evaluator
     */
    void setEvaluator(InternalEvaluator<E, C> evaluator);
}
