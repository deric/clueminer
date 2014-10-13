package org.clueminer.clustering.aggl;

import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 * SLINK clustering - also known as nearest neighbour clustering (a variant of
 * hierarchical clustering algorithm with single linkage)
 *
 * R. Sibson (1973). "SLINK: an optimally efficient algorithm for the
 * single-link cluster method". The Computer Journal (British Computer Society)
 * 16 (1): 30–34. doi:10.1093/comjnl/16.1.30.
 *
 * @author Tomas Barton
 */
public class SLINK extends AbstractClusteringAlgorithm implements AgglomerativeClustering {

    public static final String name = "SLINK";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Clustering<Cluster> cluster(Matrix matrix, Props props) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HierarchicalResult hierarchy(Dataset<? extends Instance> dataset, Props pref) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HierarchicalResult hierarchy(Matrix input, Dataset<? extends Instance> dataset, Props pref) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HierarchicalResult hierarchy(Matrix matrix, Props props) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
