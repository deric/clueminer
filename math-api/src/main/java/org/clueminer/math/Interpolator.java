package org.clueminer.math;

import java.util.List;

/**
 * General interface for interpolation / approximation
 *
 * @author Tomas Barton
 */
public interface Interpolator {

    /**
     * Unique identifier for interpolator
     *
     * @return human readable name
     */
    String getName();

    /**
     * X coordinates as double array
     *
     * @param x
     */
    void setX(double[] x);

    /**
     * known Y coordinates (must have same length as X coordinates)
     *
     * @param y
     */
    void setY(double[] y);

    /**
     *
     * @param x
     */
    void setX(List x);

    /**
     *
     * @param y
     */
    void setY(List y);

    /**
     *
     * @param x
     */
    void setX(Numeric[] x);

    /**
     *
     * @param x
     */
    void setX(Number[] x);

    /**
     *
     * @param y
     */
    void setY(Number[] y);

    /**
     *
     * @param y
     */
    void setY(Numeric[] y);

    /**
     * Provide X values wrapped in a NumericBox (so that we don't have to
     * convert it to doubles all the time)
     *
     * @param x
     */
    void setX(NumericBox x);

    /**
     * Provide Y values wrapped in a NumericBox. Wrapper for non-primitive type,
     * so that we don't have to convert it to doubles all the time
     *
     * @param y
     */
    void setY(NumericBox y);

    /**
     * Basically we need data for X and Y axis, some interpolator need e.g. at
     * least 4 points in order to work
     *
     * @return true when is able to interpolate
     */
    boolean hasData();

    /**
     * Given x (e.g. time) coordinate interpolator will return adequate value
     * for y
     *
     * @param x
     * @return
     */
    double value(double x);

    /**
     * Return interpolated value of Y for given X restricted by neighbor's
     * bounds
     *
     * @param x     point on X (time) axis for which we're trying to find out Y
     *              value
     * @param lower neighbor on left
     * @param upper neighbor on right
     * @return
     */
    double value(double x, int lower, int upper);

}
