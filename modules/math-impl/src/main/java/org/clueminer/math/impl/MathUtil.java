package org.clueminer.math.impl;

import org.clueminer.math.Matrix;
import org.clueminer.math.Numeric;
import org.clueminer.math.matrix.JamaMatrix;

/**
 *
 * @author Tomas Barton
 */
public class MathUtil {

    public static double[] toDouble(Numeric[] numeric) {
        double[] res = new double[numeric.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = numeric[i].getValue();

        }
        return res;
    }

    /**
     * Find the next power of 2.
     *
     * Classic bit operation, for signed 32-bit. Valid for positive integers
     * only (0 otherwise).
     *
     * @param x original integer
     * @return Next power of 2
     */
    public static int nextPow2Int(int x) {
        --x;
        x |= x >>> 1;
        x |= x >>> 2;
        x |= x >>> 4;
        x |= x >>> 8;
        x |= x >>> 16;
        return ++x;
    }

    /**
     * The squared Euclidean distance.
     */
    public static double squaredDistance(int[] x, int[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("Input vector sizes are different.");
        }

        double sum = 0.0;
        for (int i = 0; i < x.length; i++) {
            sum += sqr(x[i] - y[i]);
        }

        return sum;
    }

    /**
     * The squared Euclidean distance.
     */
    public static double squaredDistance(float[] x, float[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("Input vector sizes are different.");
        }

        double sum = 0.0;
        for (int i = 0; i < x.length; i++) {
            sum += sqr(x[i] - y[i]);
        }

        return sum;
    }

    /**
     * The squared Euclidean distance.
     */
    public static double squaredDistance(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("Input vector sizes are different.");
        }

        double sum = 0.0;
        for (int i = 0; i < x.length; i++) {
            sum += sqr(x[i] - y[i]);
        }

        return sum;
    }

    public static double sqr(double x) {
        return x * x;
    }

    /**
     * Simple integer power
     *
     *
     * @param base
     * @param exp
     * @return base^(exp)
     */
    public static int pow(int base, int exp) {
        int result = 1;
        while (exp != 0) {
            if ((exp & 1) != 0) {
                result *= base;
            }
            exp >>= 1;
            base *= base;
        }

        return result;
    }

    public static float hypotF(float a, float b) {
        double r;
        if (Math.abs(a) > Math.abs(b)) {
            r = b / a;
            r = Math.abs(a) * Math.sqrt(1 + r * r);
        } else if (b != 0) {
            r = a / b;
            r = Math.abs(b) * Math.sqrt(1 + r * r);
        } else {
            r = 0.0;
        }
        return (float) r;
    }

    /**
     * Computes covariance of a matrix
     *
     * @param m
     * @return
     */
    public static Matrix covariance(Matrix m) {
        final int rows = m.rowsCount();
        final int cols = m.columnsCount();

        double[] mean = new double[cols];
        Matrix covar = new JamaMatrix(cols, cols);

        // mean
        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {
                mean[i] = mean[i] + m.get(j, i);
            }
        }
        for (int i = 0; i < cols; i++) {
            mean[i] /= rows;
        }

        // covar
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < cols; j++) {
                double qij = 0;

                for (int k = 0; k < rows; k++) {
                    qij += (m.get(k, i) - mean[i]) * (m.get(k, i) - mean[j]);
                }

                covar.set(i, j, qij / (rows - 1));
            }
        }
        return covar;
    }

    /**
     * Returns the trigonometric cosine of an angle.
     */
    public static double cos(double a) {
        return java.lang.Math.cos(a);
    }

    /**
     * Returns the hyperbolic cosine of a double value.
     */
    public static double cosh(double x) {
        return java.lang.Math.cosh(x);
    }

    /**
     * Returns Euler's number e raised to the power of a double value.
     */
    public static double exp(double a) {
        return java.lang.Math.exp(a);
    }

    /**
     * Returns e<sup>x</sup>-1.
     */
    public static double expm1(double x) {
        return java.lang.Math.expm1(x);
    }

    /**
     * Returns the largest (closest to positive infinity) double value that is
     * less than or equal to the argument and is equal to a mathematical
     * integer.
     */
    public static double floor(double a) {
        return java.lang.Math.floor(a);
    }

    /**
     * Returns the unbiased exponent used in the representation of a double.
     */
    public static int getExponent(double d) {
        return java.lang.Math.getExponent(d);
    }

}
