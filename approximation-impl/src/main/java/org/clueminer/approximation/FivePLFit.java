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
     * Constructs a function with the given number of summed exponentials.
     */
    public FivePLFit() {
    }

    @Override
    public double getY(double x, double[] p) {
        return p[3] + (p[0] - p[3]) / Math.pow(1 + Math.pow(x / p[2], p[1]), p[4]);
    }

    @Override
    public double getPartialDerivate(double x, double[] p, int parameterIndex) {
        switch (parameterIndex) {
            case 0: // a
                return Math.pow(Math.pow(x / p[2], p[1]) + 1, -p[4]);
            case 1: // b
                return p[4] * (-(p[0] - p[3])) * Math.pow(x / p[2], p[1]) * Math.log(x / p[2]) * Math.pow((Math.pow(x / p[2], p[1]) + 1), -p[4] - 1);
            case 2: // c
                return (p[1] * p[4] * (p[0] - p[3]) * Math.pow(x / p[2], p[1]) * Math.pow((Math.pow(x / p[2], p[1]) + 1), -p[4] - 1)) / p[2];
            case 3: // d
                return 1 - (Math.pow((Math.pow(x / p[2], p[1]) + 1), -p[4]));
            case 4: // g
                return (-(p[0] - p[3])) * Math.pow((Math.pow(x / p[2], p[1]) + 1), -p[4]) * Math.log(Math.pow(x / p[2], p[1]) + 1);
            default:
                throw new RuntimeException("unknown parameter");
        }
    }
}
