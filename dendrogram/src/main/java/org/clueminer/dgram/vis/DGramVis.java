package org.clueminer.dgram.vis;

import java.awt.Image;
import java.util.prefs.Preferences;
import org.clueminer.clustering.aggl.AgglParams;
import org.clueminer.clustering.aggl.HAC;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.struct.DendrogramData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dendrogram.gui.Heatmap;
import org.clueminer.math.Matrix;
import org.clueminer.std.Scaler;

/**
 *
 * Generates thumbnail of a heat-map
 *
 * @author Tomas Barton
 */
public class DGramVis {

    public static Image generate(Clustering<? extends Cluster> clustering, int width, int height) {
        //if (dataset != null) {
        Heatmap heatmap = new Heatmap();
        DendrogramMapping mapping = clustering.getLookup().lookup(DendrogramMapping.class);
        if (mapping == null) {
            mapping = createMapping(clustering);
        } else {
            if (!mapping.hasColumnsClustering()) {
                Dataset<? extends Instance> dataset = clustering.getLookup().lookup(Dataset.class);
                Preferences params = clustering.getParams();
                AgglomerativeClustering algorithm = new HAC();

                Matrix input = Scaler.standartize(dataset.arrayCopy(), params.get("std", "none"), params.getBoolean("log-scale", false));
                params.putBoolean(AgglParams.CLUSTER_ROWS, false);
                HierarchicalResult colsResult = algorithm.hierarchy(input, dataset, params);
                mapping.setColsResult(colsResult);
            }
        }
        heatmap.setData(mapping);

        //}
        /**
         * TODO implement structure generation
         */
        /*       DgViewer viewer = new DgViewer();
         viewer.setDataset(null);
         viewer.setClustering(clustering);*/
        return heatmap.generate(width, height);
    }

    private static DendrogramMapping createMapping(Clustering<? extends Cluster> clustering) {
        Dataset<? extends Instance> dataset = clustering.getLookup().lookup(Dataset.class);
        Preferences params = clustering.getParams();
        AgglomerativeClustering algorithm = new HAC();

        Matrix input = Scaler.standartize(dataset.arrayCopy(), params.get("std", null), params.getBoolean("log-scale", false));

        params.putBoolean(AgglParams.CLUSTER_ROWS, true);
        HierarchicalResult rowsResult = algorithm.hierarchy(input, dataset, params);
        params.putBoolean(AgglParams.CLUSTER_ROWS, false);
        HierarchicalResult colsResult = algorithm.hierarchy(input, dataset, params);

        DendrogramMapping mapping = new DendrogramData(dataset, input, rowsResult, colsResult);
        clustering.lookupAdd(mapping);

        return mapping;
    }

}
