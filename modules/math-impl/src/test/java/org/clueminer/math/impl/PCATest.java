package org.clueminer.math.impl;

import org.clueminer.math.matrix.JMatrix;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.math.EigenvalueDecomposition;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Dump;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class PCATest {

    public PCATest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getSpaces method, of class PCA.
     */
    @Test
    public void testPCA() {
        PrintStream out = System.out;

        try {
            CommonFixture tf = new CommonFixture();

            // Open the matrix file
            FileInputStream is = new FileInputStream(tf.irisData());
            BufferedReader bis = new BufferedReader(new InputStreamReader(is));
            StreamTokenizer st = new StreamTokenizer(bis);

            // Row and column sizes, read in first
            st.nextToken();
            int n = (int) st.nval;
            st.nextToken();
            int m = (int) st.nval;

            System.out.println(" No. of rows, n = " + n);
            System.out.println(" No. of cols, m = " + m);

            // Input array, values to be read in successively, float
            double[][] indat = new double[n][m];
            double inval;

            // New read in input array values, successively
            System.out.println(" Input data sample follows as a check, first 4 values.");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    st.nextToken();
                    inval = st.nval;
                    indat[i][j] = inval;
                    if (i < 2 && j < 2) {
                        System.out.println(" value = " + inval);
                    }
                }
            }
            System.out.println();

            //-------------------------------------------------------------------
            // Data preprocessing - standardization and determining correlations
            double[][] indatstd = PCA.Standardize(n, m, indat);
            // use Jama matrix class
            Matrix X = new JMatrix(indatstd);

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

            // Print out SSCP matrix
            // Parameters: floating value width, and no. of decimal places
            // Comment out this line for large data sets, and/or diff. precision
            System.out.println();
            System.out.println(" SSCP or sums-of-squares and cross-products matrix:");
            System.out.println(" (Note: correlations in this implementation)");
            SSCP.print(6, 2);

            //-------------------------------------------------------------------
            // Eigen decomposition
            EigenvalueDecomposition evaldec = SSCP.eig();
            Matrix evecs = evaldec.getV();
            double[] evals = evaldec.getRealEigenvalues();

            // print out eigenvectors
            System.out.println(" Eigenvectors (leftmost col <--> largest eval):");
            // evecs contains the cols. ordered right to left
            // Evecs is the more natural order with cols. ordered left to right
            // So to repeat: leftmost col. of Evecs is assoc with largest Evals
            // Evals and Evecs ordered from left to right

            double tot = 0.0;
            for (int j = 0; j < evals.length; j++) {
                tot += evals[j];
            }

            // reverse order of evals into Evals
            double[] Evals = new double[m];
            for (int j = 0; j < m; j++) {
                Evals[j] = evals[m - j - 1];
            }
            // reverse order of JMatrix evecs into JMatrix Evecs
            double[][] tempold = evecs.getArray();
            double[][] tempnew = new double[m][m];
            for (int j1 = 0; j1 < m; j1++) {
                for (int j2 = 0; j2 < m; j2++) {
                    tempnew[j1][j2] = tempold[j1][m - j2 - 1];
                }
            }
            JMatrix Evecs = new JMatrix(tempnew);
            Evecs.print(10, 4);

            System.out.println();
            System.out.println(" Eigenvalues and as cumulative percentages:");
            double runningtotal = 0.0;
            double[] percentevals = new double[m];
            for (int j = 0; j < Evals.length; j++) {
                percentevals[j] = runningtotal + 100.0 * Evals[j] / tot;
                runningtotal = percentevals[j];
            }
            Dump.vector(Evals, 4, 10);
            Dump.vector(percentevals, 4, 10);

            //-------------------------------------------------------------------
            // Projections - row, and col
            // Row projections in new space, X U  Dims: (n x m) x (m x m)
            System.out.println();
            System.out.println(" Row projections in new principal component space:");
            Matrix rowproj = X.times(Evecs);
            rowproj.print(10, 4);

            // Col projections (X'X) U    (4x4) x4  And col-wise div. by sqrt(evals)
            Matrix colproj = SSCP.times(Evecs);

            // We need to leave colproj JMatrix class and instead use double array
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
            System.out.println();
            System.out.println(" Column projections in new princ. comp. space.");
            Dump.printMatrix(m, m, ynew, 4, 8);

            //-------------------------------------------------------------------
            // That's it.
        } catch (IOException e) {
            out.println("error: " + e);
            System.exit(1);
        }

    }
}
