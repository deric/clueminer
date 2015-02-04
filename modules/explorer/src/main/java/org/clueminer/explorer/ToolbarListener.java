package org.clueminer.explorer;

import java.awt.event.ActionEvent;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public interface ToolbarListener {

    void evolutionAlgorithmChanged(ActionEvent evt);

    void startEvolution(ActionEvent evt, String evolution);

    void evaluatorChanged(ClusterEvaluation eval);

    void runClustering(ClusteringAlgorithm alg, Props props);

    /**
     *
     * @return currently active algorithm
     */
    Evolution currentEvolution();

}
