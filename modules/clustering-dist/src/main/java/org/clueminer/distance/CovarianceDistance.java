package org.clueminer.distance;

import org.clueminer.distance.api.AbstractDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Vector;
import org.openide.util.lookup.ServiceProvider;

/**
 * Doesn't seem to work with hierarchical clustering
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Distance.class)
public class CovarianceDistance extends AbstractDistance implements Distance {

    private static final String NAME = "Covariance";
    private static final long serialVersionUID = -7431617150502590047L;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y) {
        checkInput(x, y);
        double xt, yt;
        double sxy = 0.0;
        double avgX = 0.0;
        double avgY = 0.0;
        int k = x.size();
        int n = 0;
        int j;
        for (j = 0; j < k; j++) {
            if ((!Double.isNaN(x.get(j))) && (!Double.isNaN(y.get(j)))) {
                avgX += x.get(j);
                avgY += y.get(j);
                n++;
            }
        }
        avgX /= n;
        avgY /= n;
        for (j = 0; j < k; j++) {
            if ((!Double.isNaN(x.get(j))) && (!Double.isNaN(y.get(j)))) {
                xt = x.get(j) - avgX;
                yt = y.get(j) - avgY;
                sxy += xt * yt;
            }
        }
        return (sxy / ((n - 1) * 1.0));
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean compare(double x, double y) {
        return Math.abs(x) < Math.abs(y);
    }

    @Override
    public double getMinValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getMaxValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSymmetric() {
        return true;
    }

    @Override
    public boolean isSubadditive() {
        return true;
    }

    /**
     * For the same vector will return 1.0 not 0.0
     *
     * @return
     */
    @Override
    public boolean isIndiscernible() {
        return false;
    }

    @Override
    public double measure(double[] x, double[] y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
