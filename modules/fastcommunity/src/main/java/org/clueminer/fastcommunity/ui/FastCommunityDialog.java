package org.clueminer.fastcommunity.ui;

import javax.swing.JPanel;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.gui.ClusteringDialog;
import org.clueminer.fastcommunity.FastCommunity;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
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
    public boolean isUIfor(ClusteringAlgorithm algorithm) {
        return algorithm instanceof FastCommunity;
    }

}
