package org.clueminer.approximation;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 *
 * @author Tomas Barton
 */
public class ChebyshevFunction implements UnivariateFunction {

    double[] coeff;

    public ChebyshevFunction(int n) {
        initialize(n, -1.0, 1.0, -1.0, 1.0);
    }

    private void initialize(int n, double xmin, double xmax, double ymin, double ymax) {
        coeff = new double[n + 1];
        chebyshev(coeff, n);
    }

    /**
     * Construct coefficients for Chebyshev Polynomial of the First Kind
     *
     * @param coeff
     * @param n
     * @return
     */
    protected double[] chebyshev(double[] coeff, int n) {
        if (n == 0 || n == 1) {
            coeff[n] = 1;
        }
        if (n > 1) {
            double[] a = new double[coeff.length];
            System.arraycopy(coeff, 0, a, 0, coeff.length);

            coeff = arrayMinus(xTimes(2, chebyshev(coeff, n - 1)), chebyshev(a, n - 2));
        }
        return coeff;
    }

    /**
     * Multiplies polynomial T(x) by {multi} * x
     *
     * @param coeff array of polynomial coefficients
     * @param multi multiplying constant
     */
    protected double[] xTimes(double multi, double[] coeff) {
        coeff = arrayShiftLeft(coeff, 1);
        for (int i = 0; i < coeff.length; i++) {
            coeff[i] = multi * coeff[i];
        }
        return coeff;
    }

    /**
     * Shifts elements in array from right to left by {amount} positions. Most
     * left element gets lost (it's not rotating)
     *
     * @param coeff
     * @param amount
     * @return
     */
    protected double[] arrayShiftLeft(double[] coeff, int amount) {
        for (int i = 0; i < amount; i++) {
            for (int j = coeff.length - 1; j > 0; j--) {
                coeff[j] = coeff[j - 1];
            }
            //annulate last coefficient
            coeff[0] = 0;
        }
        return coeff;
    }

    /**
     * Substract second array {b} from the first one {a}
     *
     * @param a
     * @param b
     * @return
     */
    protected double[] arrayMinus(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new RuntimeException("array must have same length");
        }
        for (int i = 0; i < a.length; i++) {
            a[i] -= b[i];
        }
        return a;
    }

    /**
     * To maintain integral properties we have to scale data to interval where
     * Chebyshev function have expected properties
     *
     * @param x should be in range <-1; 1>
     * @return value of Chebyshev function at given x
     */
    @Override
    public double value(double x) {
        double value = 0.0;
        for (int i = 0; i < coeff.length; i++) {
            if (coeff[i] != 0) {
                if (i > 0) {
                    value += coeff[i] * Math.pow(x, i);
                }else{
                    value += coeff[i];
                }
            }
        }
        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int j = 0;
        for (int i = coeff.length - 1; i >= 0; i--) {
            if (coeff[i] != 0) {
                if (j > 0 && coeff[i] > 0) {
                    sb.append(" + ");
                }
                j++; //how many non-null coefficients we have
                sb.append(coeff[i]);
                if (i > 0) {
                    sb.append("*x^").append(i);
                }
            }
        }
        return sb.toString();
    }
}
