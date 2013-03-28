package org.clueminer.clustering.gui;

/**
 *
 * @author Tomas Barton
 */
public class ClusteringRunner implements Runnable {

    private ClusteringDialog config = null;
    private ClusterAnalysis analysis;
    

    public ClusteringRunner(ClusterAnalysis clust, ClusteringDialog config) {
        this.analysis = clust;
        this.config = config;
    }

    @Override
    public void run() {
        analysis.execute(config.getParams());
    }
}
