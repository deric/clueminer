package org.clueminer.clustering;

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
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.clueminer.std.Scaler;

/**
 * Executor should be responsible of converting dataset into appropriate input
 * (e.g. a dense matrix) and then joining the original inputs with appropriate
 * clustering result
 *
 * @author Tomas Barton
 */
public class ClusteringExecutor {

    private AgglomerativeClustering algorithm;

    public ClusteringExecutor() {
        algorithm = new HAC();
    }

    public Clustering<Cluster> clusterRows(Dataset<? extends Instance> dataset, DistanceMeasure dm, Preferences params) {

        if (dataset == null || dataset.isEmpty()) {
            throw new NullPointerException("no data to process");
        }

        Matrix input = Scaler.standartize(dataset.arrayCopy(), params.get("std", Scaler.NONE), params.getBoolean("log-scale", false));

        params.putBoolean(AgglParams.CLUSTER_ROWS, true);
        HierarchicalResult rowsResult = algorithm.hierarchy(input, dataset, params);

        DendrogramMapping mapping = new DendrogramData(dataset, input, rowsResult);

        /**
         * @TODO generate clustering
         */
        //clustering.lookupAdd(mapping);
        return null;
    }

    public AgglomerativeClustering getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(AgglomerativeClustering algorithm) {
        this.algorithm = algorithm;
    }

}
