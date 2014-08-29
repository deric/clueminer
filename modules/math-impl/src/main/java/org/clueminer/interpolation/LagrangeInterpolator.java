package org.clueminer.interpolation;

import org.clueminer.math.Interpolator;
import org.openide.util.lookup.ServiceProvider;

/**
 * @link http://mathworld.wolfram.com/LagrangeInterpolatingPolynomial.html
 * @author Tomas Barton
 */
@ServiceProvider(service = Interpolator.class)
public class LagrangeInterpolator extends AbstractInterpolator implements Interpolator {

    private static final String name = "Lagrange";

    @Override
    public String getName() {
        return name;
    }

    /**
     * @TODO estimations at the beginning and at the end of data are very
     * inaccurate
     *
     * @param x
     * @param lower
     * @param upper
     * @return
     */
    @Override
    public double value(double x, int lower, int upper) {
        int n = axisX.size();
        double wnz = 0, om = 1, w;
        for (int i = 0; i < n; i++) {
            om *= (x - axisX.get(i));
            w = 1.0;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    w *= (axisX.get(i) - axisX.get(j));
                }
            }
            wnz += axisY.get(i) / (w * (x - axisX.get(i)));
        }
        return wnz * om;
    }

    @Override
    public void changedX() {

    }

    @Override
    public void changedY() {

    }

}
