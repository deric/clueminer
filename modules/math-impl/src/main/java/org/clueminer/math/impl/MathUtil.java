package org.clueminer.math.impl;

import org.clueminer.math.Numeric;

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
     * only
     * (0 otherwise).
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

}
