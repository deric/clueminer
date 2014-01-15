package org.clueminer.clustering.gui;

import java.util.prefs.Preferences;
import javax.swing.JPanel;
import org.clueminer.clustering.api.ClusteringAlgorithm;

/**
 *
 * @author Tomas Barton
 */
public abstract class ClusteringDialog extends JPanel {

    private static final long serialVersionUID = -7343429695502397667L;

    @Override
    public abstract String getName();

    public abstract Preferences getParams();

    public abstract void setParent(ClusterAnalysis clust);

    public abstract ClusteringAlgorithm getAlgorithm();
}
