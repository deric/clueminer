package org.clueminer.math;

/**
 *
 * @author Tomas Barton
 */
public abstract class Standardisation {

    public abstract String getName();

    /**
     * Perform standardization of input data and return new array with adjusted
     * values
     *
     * @param data
     * @param m    number of rows
     * @param n    number of columns
     * @return standardized data
     */
    public abstract double[][] optimize(double[][] data, int m, int n);

    /**
     * Computes average (mean) value of the array
     *
     * @param array input data
     * @return
     */
    public double average(double[] array) {
        double total = 0.0;
        for (int i = 0; i < array.length; i++) {
            total += array[i];
        }
        return (total / array.length);
    }

}
