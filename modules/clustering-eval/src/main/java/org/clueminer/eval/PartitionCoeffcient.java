package org.clueminer.eval;

import org.apache.commons.math3.util.FastMath;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;

/**
 * Coefficient for fuzzy clusterings counts with *mu* which is degree of
 * membership to a cluster. Doesn't make sense for hard clustering
 *
 * @author Tomas Barton
 */
public class PartitionCoeffcient extends AbstractEvaluator {

    private static final String name = "PC";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset) {
        //TODO fix this for fuzzy case
        double mu;
        double pc = 0.0;
        for (Cluster<? extends Instance> c : clusters) {
            double sum = 0.0;
            for (Instance inst : c) {
                mu = 1.0; //TODO instace membership
                sum += FastMath.pow(mu, 2);
            }
            pc = sum / (double) c.size();
        }
        return (pc / (double) clusters.size());
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset, Matrix proximity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        return (score1 > score2);
    }

    @Override
    public boolean isMaximized() {
        return true;
    }

}
