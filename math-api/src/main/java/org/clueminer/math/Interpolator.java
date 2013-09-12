package org.clueminer.math;

import java.util.List;

/**
 *
 * @author Tomas Barton
 */
public abstract class Interpolator {

    /**
     * Estimates value on axis Y for given x
     *
     * @param axisX - values on axis X
     * @param axisY - values on axis Y
     * @param x - point on X (time) axis for which we're trying to find out
     * value
     * @param lower neighbour on left
     * @param upper neighbour on right
     * @return
     */
    public abstract double getValue(Numeric[] axisX, Numeric[] axisY, double x, int lower, int upper);

    public abstract double getValue(double[] x, double[] y, double z, int lower, int upper);

    public abstract double getValue(Numeric[] x, List<? extends Numeric> y, double z, int lower, int upper);
}
