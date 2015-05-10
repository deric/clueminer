package org.clueminer.clustering.gui;

import javax.swing.JPanel;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.utils.Props;

/**
 * Each algorithm can have a GUI panel which should implement this interface
 *
 * @author Tomas Barton
 */
public interface ClusteringDialog {

    /**
     * For lookup purposes, should be unique
     *
     * @return
     */
    String getName();

    Props getParams();

    void setParent(ClusterAnalysis clust);

    /**
     * Update algorithm configuration
     *
     * @param algorithm
     */
    void updateAlgorithm(ClusteringAlgorithm algorithm);

    ClusteringAlgorithm getAlgorithm();

    /**
     * GUI which will be embedded into another dialog (should not contain any
     * OK/Cancel buttons)
     *
     * @return
     */
    JPanel getPanel();

    /**
     * Return true when UI is compatible with given algorithm
     *
     * @param algorithm
     * @return
     */
    boolean isUIfor(ClusteringAlgorithm algorithm);
}
