package org.clueminer.clustering.gui.dlg;

import javax.swing.JPanel;
import org.clueminer.chinesewhispers.ChineseWhispers;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.gui.ClusterAnalysis;
import org.clueminer.clustering.gui.ClusteringDialog;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Hamster
 */
@ServiceProvider(service = ClusteringDialog.class)
public class ChineseWhispersDialog extends JPanel implements ClusteringDialog {

	@Override
	public String getName() {
		return "Chinese Whispers dialog";
	}

	@Override
	public Props getParams() {
		Props params = new Props();
		params.putBoolean(AgglParams.CLUSTER_COLUMNS, false);
		return params;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

	@Override
	public boolean isUIfor(ClusteringAlgorithm algorithm) {
        return algorithm instanceof ChineseWhispers;
	}
	
}