package org.clueminer.explorer;

import java.awt.event.ActionEvent;
import org.clueminer.clustering.api.ClusterEvaluation;

/**
 *
 * @author Tomas Barton
 */
public interface ToolbarListener {

    void evolutionAlgorithmChanged(ActionEvent evt);

    void startEvolution(ActionEvent evt, String evolution);

    void evaluatorChanged(ClusterEvaluation eval);
}
