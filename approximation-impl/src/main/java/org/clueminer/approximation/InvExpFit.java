package org.clueminer.approximation;

import jaolho.data.lma.LMAFunction;

/**
 *
 * @author Tomas Barton
 */
/** 
 * A summed exponential function of the form: 
 * y(t) = a1*e^(-b1*t) + c1*x  ... + an*e^(-bn*t) + d. 
 */
public class InvExpFit extends LMAFunction {

    /** Number of exponentials to fit. */
    private int numExp = 1;

    /** Constructs a function with the given number of summed exponentials. */
    public InvExpFit(int num) {
        numExp = num;
    }

    @Override
    public double getY(double x, double[] a) {
        double sum = 0;
        for (int i = 0; i < numExp; i++) {
            int e = 4 * i;
            sum += a[e] * Math.exp(a[e + 1] * x) + a[e + 2] * x + a[e + 3];
        }
        return sum;
    }

    @Override
    public double getPartialDerivate(double x, double[] a, int parameterIndex) {
        int e = parameterIndex / 4;
        int off = parameterIndex % 4;
        switch (off) {
            case 0:
                return 1;
            case 1:
                //exp stays the same an we derivate the exponent
                return a[e + 1] * Math.exp(a[e + 1] * x);
            case 2:
                return a[e + 2];
            case 3:
                return 1;
            default:
                throw new RuntimeException("No such parameter index: "
                        + parameterIndex);
        }
    }
}