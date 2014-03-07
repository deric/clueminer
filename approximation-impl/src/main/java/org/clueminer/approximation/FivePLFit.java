package org.clueminer.approximation;

import jaolho.data.lma.LMAFunction;

/**
 * params: 0: a, 1: b, 2: c, 3: d, 4: g
 *
 * f(x; a, b, c, d, g) = d + [ (a - d) / (1 + (x / c)^b )^g ]
  *
 * @author Tomas Barton
 */
public class FivePLFit extends LMAFunction {

    /**
     * Number of exponentials to fit.
     */
    private final int numParam = 5;

    /**
     * Constructs a function with the given number of summed exponentials.
     */
    public FivePLFit() {
    }

    @Override
    public double getY(double x, double[] p) {
        return p[3] + (p[0] - p[3]) / Math.pow(1 + Math.pow(x / p[2], p[1]), p[4]);
    }

    @Override
    public double getPartialDerivate(double x, double[] a, int parameterIndex) {
        return 1e-20;
    }
}
