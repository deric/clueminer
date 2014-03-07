package org.clueminer.fit;

import jaolho.data.lma.LMAFunction;

/**
 * Chebyshev Polynomial of the First Kind
 *
 * T_0(x) = 1
 *
 * T_1(x) = x
 *
 * T_2(x) = 2x^2 - 1
 *
 * T_3(x) = 4x^3 - 3x
 *
 * ...
 *
 * Recursive form: T_n(x) = 2x T_{n-1}(x) - T_{n-2}(x)
 *
 *
 * @see http://mathworld.wolfram.com/ChebyshevPolynomialoftheFirstKind.html
 * @author Tomas Barton
 */
public class ChebyshevFit extends LMAFunction {

    /**
     * @return The partial derivate of the polynomial which is x to the power of
     * the parameter index.
     */
    @Override
    public double getPartialDerivate(double x, double[] a, int parameterIndex) {
        return pow(x, parameterIndex);
    }

    /**
     * PolynomialFit y = a_n * x^n + ... + a_2 * x^2 + a_1 * x + a_0
     *
     * @param a 0: a_0, 1: a_1, 2: a_2, ..., a_n
     */
    @Override
    public double getY(double x, double[] a) {
        double result = 0;
        for (int i = 0; i < a.length; i++) {
            result += pow(x, i) * a[i];
        }
        return result;
    }

    @Override
    public double[] constructWeights(double[][] dataPoints) {
        double[] result = new double[dataPoints[0].length];
        result = chebyshev(result, result.length - 1);
        return result;
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
     * Fast power when computation repeated approximately 10^3 times, for
     * repetitions above 10^4 Math.pow tends to be faster
     */
    protected static double pow(double x, int exp) {
        double result = 1;
        for (int i = 0; i < exp; i++) {
            result *= x;
        }
        return result;
    }
}
