package org.clueminer.distance;

import org.clueminer.distance.api.AbstractDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Vector;
import org.openide.util.lookup.ServiceProvider;

/**
 * Returns greatest difference between two points, sometimes called Chebyshev
 * distance.
 *
 * Chebyshev Distance is the L<sub>&#8734;</sub> norm.
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Distance.class)
public class SupremumDistance extends AbstractDistance {

    private static final String NAME = "Supremum (Chebyshev)";
    private static final long serialVersionUID = 5537883377318684946L;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y) {
        checkInput(x, y);
        int k = x.size();
        double max = 0.0;
        double diff;
        for (int i = 0; i < k; i++) {
            if ((!Double.isNaN(x.get(i))) && (!Double.isNaN(y.get(i)))) {
                diff = Math.abs((x.get(i) - y.get(i)));
                if (diff > max) {
                    max = diff;
                }
            }
        }
        return max;
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean compare(double x, double y) {
        return x < y;
    }

    @Override
    public double getMinValue() {
        return 0.0;
    }

    @Override
    public double getMaxValue() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public boolean isSymmetric() {
        return true;
    }

    @Override
    public boolean isSubadditive() {
        return true;
    }

    @Override
    public boolean isIndiscernible() {
        return true;
    }

    @Override
    public double measure(double[] x, double[] y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
