package org.clueminer.evolution.api;

import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.utils.Props;

/**
 * Single objective evolution
 *
 * @author Tomas Barton
 * @param <T>
 */
public interface EvolutionSO<T extends Individual> extends Evolution<T> {

    ClusterEvaluation getEvaluator();

    /**
     * Set objective function for evolution process
     *
     * @param evaluator
     */
    void setEvaluator(ClusterEvaluation evaluator);

    void setDefaultProps(Props prop);

    /**
     * Properties for newly created individual
     *
     * @return
     */
    Props getDefaultProps();

}
