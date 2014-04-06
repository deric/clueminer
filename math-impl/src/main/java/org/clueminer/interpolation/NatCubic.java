package org.clueminer.interpolation;

import java.awt.geom.Point2D;
import org.clueminer.math.Interpolator;
import org.clueminer.math.NumericBox;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Interpolator.class)
public class NatCubic extends AbstractInterpolator implements Interpolator {

    private static final String name = "NatCubic";
    private Cubic[] X;
    private Cubic[] Y;
    private double smooth = 0.33;

    @Override
    public String getName() {
        return name;
    }

    /**
     * calculates the natural cubic spline that interpolates y[0], y[1], ...
     * y[n] The first segment is returned as C[0].a + C[0].b*u + C[0].c*u^2 +
     * C[0].d*u^3 0<=u <1 the other segments are in C[1], C[2], ... C[n-1]
     * @param n @param x @return
     */
    public Cubic[] calcNaturalCubic(int n, NumericBox x) {
        double[] gamma = new double[n + 1];
        double[] delta = new double[n + 1];
        double[] D = new double[n + 1];
        int i;
        /* We solve the equation
         [2 1       ] [D[0]]   [3(x[1] - x[0])  ]
         |1 4 1     | |D[1]|   |3(x[2] - x[0])  |
         |  1 4 1   | | .  | = |      .         |
         |    ..... | | .  |   |      .         |
         |     1 4 1| | .  |   |3(x[n] - x[n-2])|
         [       1 2] [D[n]]   [3(x[n] - x[n-1])]

         by using row operations to convert the matrix to upper triangular
         and then back sustitution.  The D[i] are the derivatives at the knots.
         */

        gamma[0] = 1.0f / 2.0f;
        for (i = 1; i < n; i++) {
            gamma[i] = 1 / (4 - gamma[i - 1]);
        }
        gamma[n] = 1 / (2 - gamma[n - 1]);

        delta[0] = 3 * (x.get(1) - x.get(0)) * gamma[0];
        for (i = 1; i < n; i++) {
            delta[i] = (3 * (x.get(i + 1) - x.get(i - 1)) - delta[i - 1]) * gamma[i];
        }
        delta[n] = (3 * (x.get(n) - x.get(n - 1)) - delta[n - 1]) * gamma[n];

        D[n] = delta[n];
        for (i = n - 1; i >= 0; i--) {
            D[i] = delta[i] - gamma[i] * D[i + 1];
        }

        /* now compute the coefficients of the cubics */
        Cubic[] C = new Cubic[n];
        for (i = 0; i < n; i++) {
            C[i] = new Cubic((float) x.get(i), D[i], 3 * (x.get(i + 1) - x.get(i)) - 2 * D[i] - D[i + 1],
                             2 * (x.get(i) - x.get(i + 1)) + D[i] + D[i + 1]);
        }
        return C;
    }

    /**
     * not used right now, we could pre-generate set of interpolated points
     *
     * @param xpoints
     * @param ypoints
     * @param numPts
     * @param steps
     * @return
     */
    public Point2D.Double[] curvePoints(double[] xpoints, double[] ypoints, int numPts, int steps) {
        Point2D.Double[] curve;
        if (numPts >= 2) {

            curve = new Point2D.Double[X.length * steps + 1];
            curve[0] = new Point2D.Double(X[0].eval(0), Y[0].eval(0));
            int k = 1;
            for (int i = 0; i < X.length; i++) {
                for (int j = 1; j <= steps; j++) {
                    double u = j / (double) steps;
                    curve[k++] = new Point2D.Double(X[i].eval(u), Y[i].eval(u));
                }
            }
        } else {
            return new Point2D.Double[0];
        }
        return curve;
    }

    @Override
    public double value(double x, int lower, int upper) {
        if (lower < Y.length) {
            return Y[lower].eval(smooth);
        }
        return Double.NaN;
    }

    @Override
    public boolean hasData() {
        if (super.hasData()) {
            if (axisX.size() >= 2) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void changedX() {
        X = calcNaturalCubic(axisX.size() - 1, axisX);
    }

    @Override
    public void changedY() {
        Y = calcNaturalCubic(axisY.size() - 1, axisY);
    }

    public double getSmooth() {
        return smooth;
    }

    public void setSmooth(double smooth) {
        this.smooth = smooth;
    }

}
