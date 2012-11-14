package org.clueminer.interpolation;

import org.clueminer.math.Numeric;

/**
 * @link http://www.paulinternet.nl/?page=bicubic
 *
 * @author Tomas Barton
 */
public class CubicInterpolator extends Interpolator {

    public static double getValue(double[] p, double x) {
        return p[1] + 0.5 * x * (p[2] - p[0] + x * (2.0 * p[0] - 5.0 * p[1] + 4.0 * p[2] - p[3]
                + x * (3.0 * (p[1] - p[2]) + p[3] - p[0])));
    }

    /**
     * There must be existing 2 values above lower...
     *
     * @param axisX
     * @param axisY
     * @param x
     * @param lower
     * @param upper
     * @return
     */
    @Override
    public double getValue(Numeric[] axisX, Numeric[] axisY, double x, int lower, int upper) {
        /*if (upper >= axisY.length) {
            upper = axisY.length - 1;
        }*/
        double p0, p1, p2, p3;
        if (lower == 0) {
            p0 = axisY[lower].getValue();
        } else {
            p0 = axisY[lower - 1].getValue();
        }
        //System.out.println("diff= " + (axisY.length - lower) + " x= " + x + " axisY= " + axisY.length + " lower= " + lower);
        if ((lower + 1) == axisY.length) {
            p0 = axisY[lower - 4].getValue();
            p1 = axisY[lower - 3].getValue();
            p2 = axisY[lower - 2].getValue();
            p3 = axisY[lower - 1].getValue();
        } else if (lower + 2 == axisY.length) {
            p1 = axisY[lower].getValue();
            p2 = axisY[lower + 1].getValue();
            p3 = axisY[lower + 1].getValue(); //we don't have higher index           
        } else {
            p1 = axisY[lower].getValue();
            p2 = axisY[lower + 1].getValue();
            p3 = axisY[lower + 2].getValue();
        }

        return p1 + 0.5 * x * (p2 - p0 + x * (2.0 * p0 - 5.0 * p1 + 4.0 * p2 - p3
                + x * (3.0 * (p1 - p2) + p3 - p0)));
    }

    @Override
    public double getValue(double[] axisX, double[] axisY, double x, int lower, int upper) {
        double p0, p1, p2, p3;
        if (lower == 0) {
            p0 = axisY[lower];
        } else {
            p0 = axisY[lower - 1];
        }
        //System.out.println("diff= " + (axisY.length - lower) + " x= " + x + " axisY= " + axisY.length + " lower= " + lower);
        if ((lower + 1) == axisY.length) {
            p0 = axisY[lower - 4];
            p1 = axisY[lower - 3];
            p2 = axisY[lower - 2];
            p3 = axisY[lower - 1];
        } else if (lower + 2 == axisY.length) {
            p1 = axisY[lower];
            p2 = axisY[lower + 1];
            p3 = axisY[lower + 1]; //we don't have higher index           
        } else {
            p1 = axisY[lower];
            p2 = axisY[lower + 1];
            p3 = axisY[lower + 2];
        }

        return p1 + 0.5 * x * (p2 - p0 + x * (2.0 * p0 - 5.0 * p1 + 4.0 * p2 - p3
                + x * (3.0 * (p1 - p2) + p3 - p0)));
    }
}
