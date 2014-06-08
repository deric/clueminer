package org.clueminer.dgram.vis;

import java.awt.Image;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dendrogram.gui.Heatmap;

/**
 *
 * @author Tomas Barton
 */
public class Visualization {

    public Image generate(Clustering<? extends Cluster> clustering) {
        Dataset<? extends Instance> dataset = clustering.getLookup().lookup(Dataset.class);
        //if (dataset != null) {
        Heatmap heatmap = new Heatmap();
        DendrogramMapping mapping = clustering.getLookup().lookup(DendrogramMapping.class);
        if (mapping == null) {
            mapping = createMapping(clustering);
        }
        heatmap.setData(mapping);

        //}
        /**
         * TODO implement structure generation
         */
        /*       DgViewer viewer = new DgViewer();
         viewer.setDataset(null);
         viewer.setClustering(clustering);*/
        return heatmap.generate(32, 32);
    }

    private DendrogramMapping createMapping(Clustering<? extends Cluster> clustering) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
