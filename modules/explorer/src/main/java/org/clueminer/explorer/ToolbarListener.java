package org.clueminer.explorer;

import java.awt.event.ActionEvent;

/**
 *
 * @author Tomas Barton
 */
public interface ToolbarListener {

    void evolutionAlgorithmChanged(ActionEvent evt);

    void startEvolution(ActionEvent evt, String evolution);
}
