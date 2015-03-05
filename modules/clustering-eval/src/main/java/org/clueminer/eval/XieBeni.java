package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;

/**
 *
 * @author Tomas Barton
 */
public class XieBeni extends AbstractEvaluator {

    private static final String name = "Xie Beni";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset) {
        double xb = Double.NaN;
        //TODO compute variance, add soft membership
        return xb;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset, Matrix proximity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isMaximized() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
