/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.math.matrix;

import org.clueminer.math.Matrix;

public class Maths {

    public static float hypotF(float a, float b) {
        double r;
        if (Math.abs(a) > Math.abs(b)) {
            r = b / a;
            r = Math.abs(a) * Math.sqrt(1 + r * r);
        } else if (b != 0) {
            r = a / b;
            r = Math.abs(b) * Math.sqrt(1 + r * r);
        } else {
            r = 0.0;
        }
        return (float) r;
    }

    /**
     * Computes covariance of a matrix
     *
     * @param m
     * @return
     */
    public static Matrix covariance(Matrix m) {
        final int rows = m.rowsCount();
        final int cols = m.columnsCount();

        double[] mean = new double[cols];
        Matrix covar = new JamaMatrix(cols, cols);

        // mean
        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {
                mean[i] = mean[i] + m.get(j, i);
            }
        }
        for (int i = 0; i < cols; i++) {
            mean[i] /= rows;
        }

        // covar
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < cols; j++) {
                double qij = 0;

                for (int k = 0; k < rows; k++) {
                    qij += (m.get(k, i) - mean[i]) * (m.get(k, i) - mean[j]);
                }

                covar.set(i, j, qij / (rows - 1));
            }
        }
        return covar;
    }
}
