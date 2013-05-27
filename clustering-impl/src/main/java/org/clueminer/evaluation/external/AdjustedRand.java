package org.clueminer.evaluation.external;

import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;

/**
 *
 * @author Tomas Barton
 */
public class AdjustedRand extends ExternalEvaluator {

    private static final long serialVersionUID = -7408696944704938976L;
    private static String name = "Adjusted Rand";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<Cluster> clusters, Dataset<? extends Instance> dataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double score(Clustering<Cluster> clusters, Dataset<? extends Instance> dataset, Matrix proximity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean compareScore(double score1, double score2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double score(Clustering<Cluster> c1, Clustering<Cluster> c2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
