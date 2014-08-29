package org.clueminer.clustering.gui;

import javax.swing.JPanel;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public abstract class ClusteringDialog extends JPanel {

    private static final long serialVersionUID = -7343429695502397667L;

    @Override
    public abstract String getName();

    public abstract Props getParams();

    public abstract void setParent(ClusterAnalysis clust);

    public abstract ClusteringAlgorithm getAlgorithm();
}
