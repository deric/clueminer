package org.clueminer.clustering.gui;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.AlgorithmParameters;

/**
 *
 * @author Tomas Barton
 */
public class ClusteringRunner implements Runnable {

    private ClusteringDialog config = null;
    private ClusterAnalysis analysis;
    private static String rawData = "-- no transformation --";

    public ClusteringRunner(ClusterAnalysis clust, ClusteringDialog config) {
        this.analysis = clust;
        this.config = config;
    }

    @Override
    public void run() {
        AlgorithmParameters params = config.getParams();


        String datasetTransform = params.getString("dataset");
        System.out.println("using: " + datasetTransform);


        if (!analysis.hasDataset()) {
            throw new RuntimeException("missing dataset!");
        }

        Dataset<? extends Instance> data = analysis.getDataset();
        if (!datasetTransform.equals(rawData)) {
            Dataset<? extends Instance> transform;
            //check if there's preloaded dataset available
            transform = data.getChild(datasetTransform);
            if (transform == null) {
                //run analysis and wait 
                throw new RuntimeException("dataset is not available yet");
            }
            analysis.setDataset(transform);
        }

        analysis.execute(params);
    }
}
