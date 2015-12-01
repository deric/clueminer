package org.clueminer.explorer;

import java.awt.event.ActionEvent;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface ToolbarListener<E extends Instance> {

    void evolutionAlgorithmChanged(ActionEvent evt);

    void startEvolution(ActionEvent evt, final Evolution alg);

    void evaluatorChanged(ClusterEvaluation eval);

    void runClustering(ClusteringAlgorithm alg, Dataset<E> dataset, Props props);

    /**
     *
     * @return currently active algorithm
     */
    Evolution currentEvolution();

    /**
     * Remove all found clusterings from explorer
     */
    void clearAll();

    /**
     *
     * @return current dataset
     */
    Dataset<E> getDataset();

}
