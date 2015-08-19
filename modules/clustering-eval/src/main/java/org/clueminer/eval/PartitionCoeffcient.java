package org.clueminer.eval;

import org.apache.commons.math3.util.FastMath;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 * Coefficient for fuzzy clusterings counts with *mu* which is degree of
 * membership to a cluster. Doesn't make sense for hard clustering
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class PartitionCoeffcient<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final String name = "PC";
    private static final long serialVersionUID = 888558324967098222L;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
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
    public boolean isBetter(double score1, double score2) {
        return (score1 > score2);
    }

    @Override
    public boolean isMaximized() {
        return true;
    }

    @Override
    public double getMin() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getMax() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
