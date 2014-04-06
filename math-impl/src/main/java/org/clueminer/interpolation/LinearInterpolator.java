package org.clueminer.interpolation;

import org.clueminer.math.Interpolator;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Interpolator.class)
public class LinearInterpolator extends AbstractInterpolator implements Interpolator {

    private static final String name = "linear";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double value(double x, int l, int u) {
        int lower = l;
        int upper = u;

        /**
         * y = y_a + (y_b - y_a) * (x - x_a) / (x_b - x_a)
         */
        if (upper >= axisY.size()) {
            upper = axisY.size() - 1;
        }
        double res = axisY.get(lower) + (axisY.get(upper) - axisY.get(lower))
                * (x - axisX.get(upper)) / (axisX.get(upper) - axisX.get(lower));
        //System.out.println("x= "+x+" lower= "+lower+" upper= "+upper+", ax.l="+axisX.length+", ay.l="+axisY.length+" r= "+res);
        return res;
    }

    @Override
    public void changedX() {

    }

    @Override
    public void changedY() {

    }

}
