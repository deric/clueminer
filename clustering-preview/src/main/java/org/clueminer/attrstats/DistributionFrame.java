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

    private Clustering<Cluster> clust;

    public DistributionFrame() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }

    private void redraw() {
        //remove all components
        this.removeAll();
    }

    public Clustering<Cluster> getClustering() {
        return clust;
    }

    public void setClustering(Clustering<Cluster> clust) {
        this.clust = clust;
        redraw();
    }

}
