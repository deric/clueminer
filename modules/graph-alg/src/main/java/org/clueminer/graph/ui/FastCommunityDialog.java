package org.clueminer.graph.ui;

import javax.swing.JPanel;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.gui.ClusteringDialog;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.fastcommunity.FastCommunity;
import org.clueminer.fastcommunity.orig.FastCommunityBin;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Simple UI for Fast Community configuration
 *
 * @author Hamster
 */
@ServiceProvider(service = ClusteringDialog.class)
public class FastCommunityDialog extends JPanel implements ClusteringDialog {

    private static final long serialVersionUID = 2207676005267510122L;

    @Override
    public String getName() {
        return "Fast Community dialog";
    }

    @Override
    public Props getParams() {
        Props params = new Props();
        params.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        return params;
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public boolean isUIfor(ClusteringAlgorithm algorithm, Dataset dataset) {
        return algorithm instanceof FastCommunity || algorithm instanceof FastCommunityBin;
    }

}
