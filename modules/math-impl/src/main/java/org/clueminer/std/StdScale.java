package org.clueminer.std;

import org.clueminer.math.Standardisation;
import org.openide.util.lookup.ServiceProvider;

/**
 * Sometimes called z-score
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Standardisation.class)
public class StdScale extends Standardisation {

    public static final String name = "Min-Max";
    private double targetMin = -10;
    private double targetMax = 10;

    @Override
    public String getName() {
        return name;
    }

    /**
     * We expect to have parameters in columns, and samples in rows
     *
     * @param A
     * @param m
     * @param n
     * @return
     */
    @Override
    public double[][] optimize(double[][] A, int m, int n) {
        double[] minVal = new double[n];
        double[] maxVal = new double[n];
        int i, j;

        for (i = 0; i < n; i++) {
            minVal[i] = Double.MAX_VALUE;
            maxVal[i] = Double.MIN_VALUE;
        }
        /**
         * finds min and max in each column
         */
        for (i = 0; i < m; i++) {
            for (j = 0; j < n; j++) {
                if (A[i][j] > maxVal[j]) {
                    maxVal[j] = A[i][j];
                }
                if (A[i][j] < minVal[j]) {
                    minVal[j] = A[i][j];
                }
            }
        }
        double[][] B = new double[m][n];
        for (i = 0; i < m; i++) {
            for (j = 0; j < n; j++) {
                B[i][j] = scaleToRange(A[i][j], minVal[j], maxVal[j], getTargetMin(), getTargetMax());
            }
        }
        return B;
    }

    /**
     *
     * @param value
     * @param fromRangeMin
     * @param fromRangeMax
     * @param toRangeMin
     * @param toRangeMax
     * @return value scaled to given range
     */
    public double scaleToRange(double value, double fromRangeMin, double fromRangeMax, double toRangeMin, double toRangeMax) {
        return ((value - fromRangeMin) * (toRangeMax - toRangeMin) / (fromRangeMax - fromRangeMin) + toRangeMin);
    }

    /**
     * @return the targetMin
     */
    public double getTargetMin() {
        return targetMin;
    }

    /**
     * @param targetMin the targetMin to set
     */
    public void setTargetMin(double targetMin) {
        this.targetMin = targetMin;
    }

    /**
     * @return the targetMax
     */
    public double getTargetMax() {
        return targetMax;
    }

    /**
     * @param targetMax the targetMax to set
     */
    public void setTargetMax(double targetMax) {
        this.targetMax = targetMax;
    }
}
