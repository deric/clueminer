package org.clueminer.attrstats;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;

/**
 *
 * @author Tomas Barton
 */
public class DistributionFrame extends JPanel {

    private Clustering<Cluster> clustering;

    public DistributionFrame() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }

    private void redraw() {
        //remove all components
        this.removeAll();

        if (clustering == null) {
            return;
        }

    }

    public Clustering<Cluster> getClustering() {
        return clustering;
    }

    public void setClustering(Clustering<Cluster> clust) {
        this.clustering = clust;
        redraw();
    }

}
