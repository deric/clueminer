package org.clueminer.interpolation;

import java.util.List;
import org.clueminer.math.Interpolator;
import org.clueminer.math.Numeric;

/**
 * @link http://mathworld.wolfram.com/LagrangeInterpolatingPolynomial.html
 * @author Tomas Barton
 */
public class LagrangeInterpolator implements Interpolator {

    private static final String name = "Lagrange";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getValue(Numeric[] x, Numeric[] y, double z, int lower, int upper) {
        int n = x.length;
        double sum = 0, om = 1, w;
        for (int i = 0; i < n; i++) {
            om *= (z - x[i].getValue());
            w = 1.0;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    w *= (x[i].getValue() - x[j].getValue());
                }
            }
            sum += y[i].getValue() / (w * (z - x[i].getValue()));
        }
        return sum * om;
    }

    @Override
    public double getValue(double[] x, double[] y, double z, int lower, int upper) {
        int n = x.length;
        double wnz = 0, om = 1, w;
        for (int i = 0; i < n; i++) {
            om *= (z - x[i]);
            w = 1.0;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    w *= (x[i] - x[j]);
                }
            }
            wnz += y[i] / (w * (z - x[i]));
        }
        return wnz * om;
    }

    @Override
    public double getValue(Numeric[] x, List<? extends Number> y, double z, int lower, int upper) {
        int n = x.length;
        double sum = 0, om = 1, w;
        for (int i = 0; i < n; i++) {
            om *= (z - x[i].getValue());
            w = 1.0;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    w *= (x[i].getValue() - x[j].getValue());
                }
            }
            sum += y.get(i).doubleValue() / (w * (z - x[i].getValue()));
        }
        return sum * om;
    }
}
