package org.clueminer.utils;

/**
 *
 * @author Tomas Barton
 */
public class MathUtil {

    public static double robustMin(double m1, double m2) {
        double min = Math.min(m1, m2);
        if (!Double.isNaN(min)) {
            return min;
        } else {
            if (Double.isNaN(m1)) {
                return m2;
            } else {
                return m1;
            }
        }
    }

    public static double robustMax(double m1, double m2) {
        double max = Math.max(m1, m2);
        if (!Double.isNaN(max)) {
            return max;
        } else {
            if (Double.isNaN(m1)) {
                return m2;
            } else {
                return m1;
            }
        }
    }

}
