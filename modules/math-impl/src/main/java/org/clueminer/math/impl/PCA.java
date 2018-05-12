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
package org.clueminer.math.impl;

import org.clueminer.math.EigenvalueDecomposition;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.JamaMatrix;

/**
 * PCA - Principal Component Analysis
 *
 * @author F. Murtagh, f.murtagh@qub.ac.uk
 */
public class PCA {

    public static double[][] compute(int m, int n, double[][] indat) {
        // Data preprocessing - standardization and determining correlations
        double[][] indatstd = PCA.Standardize(n, m, indat);
        // use Jama matrix class
        Matrix X = new JamaMatrix(indatstd);

        // Sums of squares and cross-products matrix
        Matrix Xprime = X.transpose();
        Matrix SSCP = Xprime.times(X);
        // Note the following:
        // - with no preprocessing of the input data, we have an SSCP matrix
        // - with centering of columns (i.e. each col. has col. mean
        //   [vector in row-space] subtracted) we have variances/covariances
        // - with centering and reduction to unit variance [i.e. centered
        //   cols. are divided by std. dev.] we have correlations
        // Note: the current version supports correlations only

        //-------------------------------------------------------------------
        // Eigen decomposition
        EigenvalueDecomposition evaldec = SSCP.eig();
        Matrix evecs = evaldec.getV();
        double[] evals = evaldec.getRealEigenvalues();

        // evecs contains the cols. ordered right to left
        // Evecs is the more natural order with cols. ordered left to right
        // So to repeat: leftmost col. of Evecs is assoc with largest Evals
        // Evals and Evecs ordered from left to right

        // reverse order of evals into Evals
        double[] Evals = new double[m];
        for (int j = 0; j < m; j++) {
            Evals[j] = evals[m - j - 1];
        }
        // reverse order of JamaMatrix evecs into JamaMatrix Evecs
        double[][] tempold = evecs.getArray();
        double[][] tempnew = new double[m][m];
        for (int j1 = 0; j1 < m; j1++) {
            for (int j2 = 0; j2 < m; j2++) {
                tempnew[j1][j2] = tempold[j1][m - j2 - 1];
            }
        }
        Matrix Evecs = new JamaMatrix(tempnew);
        //Evecs.print(10, 4);

        // Col projections (X'X) U    (4x4) x4  And col-wise div. by sqrt(evals)
        Matrix colproj = SSCP.times(Evecs);

        // We need to leave colproj JamaMatrix class and instead use double array
        double[][] ynew = colproj.getArray();
        for (int j1 = 0; j1 < m; j1++) {
            for (int j2 = 0; j2 < m; j2++) {
                if (Evals[j2] > 0.00005) {
                    ynew[j1][j2] = ynew[j1][j2] / Math.sqrt(Evals[j2]);
                }
                if (Evals[j2] <= 0.00005) {
                    ynew[j1][j2] = 0.0;
                }
            }
        }
        /* System.out.println();
         System.out.println(" Column projections in new princ. comp. space.");
         Dump.printMatrix(m, m, ynew, 4, 8);*/
        return ynew;
    }

    /**
     * Method for standardizing the input data <p> Note the formalas used (since
     * these vary between implementations):<br> reduction: (vect -
     * meanvect)/sqrt(nrow)*colstdev <br> colstdev: sum_cols ((vect -
     * meanvect)^2/nrow) <br> if colstdev is close to 0, then set it to 1.
     *
     * @param nrow number of rows in input matrix
     * @param ncol number of columns in input matrix
     * @param A input matrix values
     */
    public static double[][] Standardize(int nrow, int ncol, double[][] A) {
        double[] colmeans = new double[ncol];
        double[] colstdevs = new double[ncol];
        // Adat will contain the standardized data and will be returned
        double[][] Adat = new double[nrow][ncol];
        double[] tempcol = new double[nrow];
        double tot;

        // Determine means and standard deviations of variables/columns
        for (int j = 0; j < ncol; j++) {
            tot = 0.0;
            for (int i = 0; i < nrow; i++) {
                tempcol[i] = A[i][j];
                tot += tempcol[i];
            }

            // For this col, det mean
            colmeans[j] = tot / (double) nrow;
            for (int i = 0; i < nrow; i++) {
                colstdevs[j] += Math.pow(tempcol[i] - colmeans[j], 2.0);
            }
            colstdevs[j] = Math.sqrt(colstdevs[j] / ((double) nrow));
            if (colstdevs[j] < 0.0001) {
                colstdevs[j] = 1.0;
            }
        }

        /*   System.out.println(" Variable means:");
         Dump.vector(colmeans, 4, 8);
         System.out.println(" Variable standard deviations:");
         Dump.vector(colstdevs, 4, 8);*/

        // Now ceter to zero mean, and reduce to unit standard deviation
        for (int j = 0; j < ncol; j++) {
            for (int i = 0; i < nrow; i++) {
                Adat[i][j] = (A[i][j] - colmeans[j])
                        / (Math.sqrt((double) nrow) * colstdevs[j]);
            }
        }
        return Adat;
    } // Standardize
}
