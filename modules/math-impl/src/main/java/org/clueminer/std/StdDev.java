package org.clueminer.std;

import org.clueminer.math.Standardisation;
import org.openide.util.lookup.ServiceProvider;

/**
 * Standard deviation
 * 
 * @author Tomas Barton
 */
@ServiceProvider(service = Standardisation.class)
public class StdDev extends Standardisation {

    private static String name = "z-score";

    @Override
    public String getName() {
        return name;
    }

    protected void computeAvg(double[][] a, double[] avg, int m, int n) {
        int i, j;
        //average value in columns
        for (j = 0; j < n; j++) {
            for (i = 0; i < m; i++) {
                avg[j] += a[i][j];
            }
            avg[j] = avg[j] / m;
        }
    }

    protected void computeDev(double[][] a, double[] avg, double[] dev, int m, int n) {
        int i, j;
        //square computeDev from average
        for (j = 0; j < n; j++) {
            for (i = 0; i < m; i++) {
                dev[j] += Math.pow(a[i][j] - avg[j], 2);
            }
            dev[j] = Math.sqrt(dev[j] / m);
        }
    }

    @Override
    public double[][] optimize(double[][] a, int m, int n) {
        double[][] res = new double[m][n];
        //average value in columns
        double[] avg = new double[n];
        //square computeDev from average
        double[] dev = new double[n];
        int i, j;

        computeAvg(a, avg, m, n);
        computeDev(a, avg, dev, m, n);

        for (i = 0; i < m; i++) {
            for (j = 0; j < n; j++) {
                res[i][j] = ((a[i][j] - avg[j]) / dev[j]);
            }
        }

        return res;
    }
}
