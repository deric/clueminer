package org.clueminer.chameleon;

import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;


/**
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class Chameleon extends AbstractClusteringAlgorithm {
    
    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return "Chameleon";
    }

    @Override
    public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset, Props props) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
