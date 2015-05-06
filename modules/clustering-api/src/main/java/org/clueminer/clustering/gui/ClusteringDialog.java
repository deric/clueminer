package org.clueminer.clustering.gui;

import javax.swing.JPanel;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.utils.Props;

/**
 * TODO: convert to an interface
 *
 * @author Tomas Barton
 */
public abstract class ClusteringDialog extends JPanel {

    private static final long serialVersionUID = -7343429695502397667L;

    @Override
    public abstract String getName();

    public abstract Props getParams();

    public abstract void setParent(ClusterAnalysis clust);

    public abstract void updateAlgorithm(ClusteringAlgorithm algorithm);

    public abstract ClusteringAlgorithm getAlgorithm();

    public abstract JPanel getPanel();

    /**
     * Return true when UI is compatible with given algorithm
     *
     * @param algorithm
     * @return
     */
    public abstract boolean isUIfor(ClusteringAlgorithm algorithm);
}
