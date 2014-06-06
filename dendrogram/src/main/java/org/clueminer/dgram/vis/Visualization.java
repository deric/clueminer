package org.clueminer.dgram.vis;

import java.awt.Image;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dgram.DgViewer;

/**
 *
 * @author Tomas Barton
 */
public class Visualization {

    public Image generate(Clustering<? extends Cluster> clustering) {
        Dataset<? extends Instance> dataset = clustering.getLookup().lookup(Dataset.class);
        /**
         * TODO implement structure generation
         */
        DgViewer viewer = new DgViewer();
        viewer.setDataset(null);
        viewer.setClustering(clustering);
        return null;
    }

}
