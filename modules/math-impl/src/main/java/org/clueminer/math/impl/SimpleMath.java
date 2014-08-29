package org.clueminer.math.impl;

/**
 *
 * @author Tomas Barton
 */
public class SimpleMath {

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

}
