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
}
