package org.clueminer.meta.h2;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.meta.api.MetaStorage;

/**
 *
 * @author Tomas Barton
 */
public class H2Store implements MetaStorage {

    private static H2Store instance;

    public static H2Store getInstance() {
        if (instance == null) {
            instance = new H2Store();
        }
        return instance;
    }

    private H2Store() {

    }

    @Override
    public void add(String datasetName, Clustering<? extends Cluster> clustering) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double findScore(String datasetName, Clustering<? extends Cluster> clustering, ClusterEvaluation eval) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
