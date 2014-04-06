package org.clueminer.interpolation;

import java.awt.geom.Point2D;
import org.clueminer.math.Interpolator;

/**
 * @link http://www.paulinternet.nl/?page=bicubic
 *
 * @author Tomas Barton
 */
public class CubicInterpolator extends AbstractInterpolator implements Interpolator {

    private static final String name = "cubic";

    @Override
    public String getName() {
        return name;
    }

    public static double getValue(double[] p, double x) {
        return p[1] + 0.5 * x * (p[2] - p[0] + x * (2.0 * p[0] - 5.0 * p[1] + 4.0 * p[2] - p[3]
                + x * (3.0 * (p[1] - p[2]) + p[3] - p[0])));
    }

    /**
     * There must be existing 2 values above lower...
     *
     * @param x
     * @param lower
     * @param upper
     * @return
     */
    @Override
    public double value(double x, int lower, int upper) {
        /*if (upper >= axisY.length) {
         upper = axisY.length - 1;
         }*/
        double p0, p1, p2, p3;
        if (lower == 0) {
            p0 = axisY.get(lower);
        } else {
            p0 = axisY.get(lower - 1);
        }
        //System.out.println("diff= " + (axisY.length - lower) + " x= " + x + " axisY= " + axisY.length + " lower= " + lower);
        if ((lower + 1) == axisY.size()) {
            p0 = axisY.get(lower - 4);
            p1 = axisY.get(lower - 3);
            p2 = axisY.get(lower - 2);
            p3 = axisY.get(lower - 1);
        } else if (lower + 2 == axisY.size()) {
            p1 = axisY.get(lower);
            p2 = axisY.get(lower + 1);
            p3 = axisY.get(lower + 1); //we don't have higher index
        } else {
            p1 = axisY.get(lower);
            p2 = axisY.get(lower + 1);
            p3 = axisY.get(lower + 2);
        }

        return p1 + 0.5 * x * (p2 - p0 + x * (2.0 * p0 - 5.0 * p1 + 4.0 * p2 - p3
                + x * (3.0 * (p1 - p2) + p3 - p0)));
    }

    @Override
    public void changedX() {

    }

    @Override
    public void changedY() {

    }

    @Override
    public Point2D.Double[] curvePoints(int steps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
