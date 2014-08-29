package org.clueminer.math.impl;

/**
 *
 * @author Tomas Barton
 */
public class Stats {

    /**
     * Returns the correlation coefficient of two double vectors.
     *
     * @param y1 double vector 1
     * @param y2 double vector 2
     * @return the correlation coefficient
     */
    public static double correlation(double y1[], double y2[]) {
        int n = y1.length;
        if (n != y2.length) {
            throw new RuntimeException("vectors should have the same lenght");
        }

        int i;
        double av1 = 0.0, av2 = 0.0, y11 = 0.0, y22 = 0.0, y12 = 0.0, c;

        if (n <= 1) {
            return 1.0;
        }
        for (i = 0; i < n; i++) {
            av1 += y1[i];
            av2 += y2[i];
        }
        av1 /= (double) n;
        av2 /= (double) n;
        double diffX, diffY;
        for (i = 0; i < n; i++) {
            diffX = y1[i] - av1;
            diffY = y2[i] - av2;
            y11 += Math.pow(diffX,2);
            y22 += Math.pow(diffY,2);
            y12 += diffX * diffY;
        }
        if (y11 * y22 == 0.0) {
            c = 1.0;
        } else {
            c = y12 / Math.sqrt(Math.abs(y11 * y22));
        }

        return c;
    }

    /**
     * Computes the variance for an array of doubles.
     *
     * @param vector the array
     * @return the variance
     */
    public static double variance(double[] vector) {

        double sum = 0, sumSquared = 0;

        if (vector.length <= 1) {
            return 0;
        }
        for (int i = 0; i < vector.length; i++) {
            sum += vector[i];
            sumSquared += (vector[i] * vector[i]);
        }
        double result = (sumSquared - (sum * sum / (double) vector.length))
                / (double) (vector.length - 1);

        // We don't like negative variance
        if (result < 0) {
            return 0;
        } else {
            return result;
        }
    }
}
