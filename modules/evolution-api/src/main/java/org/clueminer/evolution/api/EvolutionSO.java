package org.clueminer.evolution.api;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 * Single objective evolution
 *
 * @author Tomas Barton
 * @param <I>
 * @param <E>
 * @param <C>
 */
public interface EvolutionSO<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>> extends Evolution<I, E, C> {

    ClusterEvaluation<E, C> getEvaluator();

    /**
     * Set objective function for evolution process
     *
     * @param evaluator
     */
    void setEvaluator(ClusterEvaluation<E, C> evaluator);

    void setDefaultProps(Props prop);

    /**
     * Properties for newly created individual
     *
     * @return
     */
    Props getDefaultProps();

}
