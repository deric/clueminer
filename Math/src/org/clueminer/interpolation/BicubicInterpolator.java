package org.clueminer.interpolation;

/**
 *
 * @author Tomas Barton
 */
public class BicubicInterpolator extends CubicInterpolator {

    private double[] arr = new double[4];

    public double getValue(double[][] p, double x, double y) {
        arr[0] = getValue(p[0], y);
        arr[1] = getValue(p[1], y);
        arr[2] = getValue(p[2], y);
        arr[3] = getValue(p[3], y);
        return getValue(arr, x);
    }
}
