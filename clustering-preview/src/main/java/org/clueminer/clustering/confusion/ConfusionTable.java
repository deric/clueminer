package org.clueminer.clustering.confusion;

import javax.swing.JPanel;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;

/**
 *
 * @author Tomas Barton
 */
public class ConfusionTable extends JPanel {
    private static final long serialVersionUID = -7558362062012338814L;

    public ConfusionTable() {
        initComponents();
    }

    private void initComponents() {

    }

    public void setClusterings(Clustering<Cluster> a, Clustering<Cluster> b) {
        for (Cluster x : a) {
            for (Cluster y : b) {
                System.out.println("a-" + x.getName() + "-vs" + "-b" + y.getName() + ": " + x.countMutualElements(y));
            }
        }
    }

}
