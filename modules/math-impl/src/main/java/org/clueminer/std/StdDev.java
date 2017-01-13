/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

    public static final String name = "z-score";

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
            // Bessel's correction: (n-1) instead of just n (same as sd() in R)
            dev[j] = Math.sqrt(dev[j] / (m - 1));
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
