package org.clueminer.approximation;

/**
 *
 * @author Tomas Barton
 */
public class ChebyshevFunction {

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
}
