package org.clueminer.interpolation;

import java.util.List;
import org.clueminer.math.Interpolator;
import org.clueminer.math.Numeric;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Interpolator.class)
public class LinearInterpolator implements Interpolator {

    private static final String name = "linear";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getValue(Numeric[] axisX, Numeric[] axisY, double x, int l, int u) {
        int lower = l;
        int upper = u;
        /**
         * y = y_a + (y_b - y_a) * (x - x_a) / (x_b - x_a)
         */
        if (upper >= axisY.length) {
            upper = axisY.length - 1;
        }
        double res = axisY[lower].getValue() + (axisY[upper].getValue() - axisY[lower].getValue())
                * (x - axisX[lower].getValue()) / (axisX[upper].getValue() - axisX[lower].getValue());
        //System.out.println("x= "+x+" lower= "+lower+" upper= "+upper+", ax.l="+axisX.length+", ay.l="+axisY.length+" r= "+res);
        return res;
    }

    @Override
    public double getValue(double[] axisX, double[] axisY, double x, int l, int u) {
        int lower = l;
        int upper = u;
        /**
         * y = y_a + (y_b - y_a) * (x - x_a) / (x_b - x_a)
         */
        if (upper >= axisY.length) {
            upper = axisY.length - 1;
        }
        double res = axisY[lower] + (axisY[upper] - axisY[lower]) * (x - axisX[lower]) / (axisX[upper] - axisX[lower]);
        System.out.println("x= " + x + " lower= " + lower + " upper= " + upper + ", ax.l=" + axisX.length + ", ay.l=" + axisY.length + " r= " + res);
        return res;
    }

    @Override
    public double getValue(Numeric[] x, List<? extends Number> y, double z, int l, int u) {
        int lower = l;
        int upper = u;
        /**
         * y = y_a + (y_b - y_a) * (x - x_a) / (x_b - x_a)
         */
        if (upper >= y.size()) {
            upper = y.size() - 1;
        }
        double res = y.get(lower).doubleValue() + (y.get(upper).doubleValue() - y.get(lower).doubleValue())
                * (z - x[lower].getValue()) / (x[upper].getValue() - x[lower].getValue());
        //System.out.println("x= "+x+" lower= "+lower+" upper= "+upper+", ax.l="+axisX.length+", ay.l="+axisY.length+" r= "+res);
        return res;
    }
}
