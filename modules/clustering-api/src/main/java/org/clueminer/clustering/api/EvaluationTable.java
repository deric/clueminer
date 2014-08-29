package org.clueminer.clustering.api;

import java.util.Map;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 * Table with evaluation results, might perform caching or other optimizations
 *
 * @author Tomas Barton
 */
public interface EvaluationTable {

    void setData(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset);

    /**
     * Get score for given evaluator
     *
     * @param evaluator
     * @return
     */
    double getScore(ClusterEvaluation evaluator);

    /**
     * Names of all available evaluators
     *
     * @return
     */
    String[] getEvaluators();

    /**
     *
     * @return map of internal evaluation scores
     */
    Map<String, Double> getInternal();

    /**
     *
     * @return map of external evaluations scores
     */
    Map<String, Double> getExternal();
}
