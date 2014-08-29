package edu.hawaii.jmotif.logic.math;

import java.util.Formatter;
import java.util.Locale;

/**
 * Implements some of the algebra methods.
 *
 * @author Pavel Senin
 *
 */
public final class MatrixFactory {

    private static final String CR = "\n";

    /**
     * Constructor.
     */
    private MatrixFactory() {
        super();
    }

    /**
     * Creates array of doubles of length n filled by zeroes.
     *
     * @param n the desired array size.
     * @return the array of zeroes of size n.
     */
    public static double[][] zeros(int n) {
        return new double[1][n];
    }

    /**
     * Creates array of matrix of doubles of size n x m filled by zeroes.
     *
     * @param n the number of rows.
     * @param m the number of columns.
     * @return the matrix of zeroes.
     */
    public static double[][] zeros(int n, int m) {
        return new double[n][m];
    }

    /**
     * Compare two matrices by elements.
     *
     * @param a the first matrix.
     * @param b the second matrix.
     * @return true if matrices are equal.
     */
    public static boolean equals(double[][] a, double[][] b) {
        int rowsA = a.length;
        int colsA = a[0].length;

        int rowsB = b.length;
        int colsB = b[0].length;

        if ((rowsA == rowsB) && (colsA == colsB)) {
            for (int i = 0; i < rowsA; i++) {
                for (int j = 0; j < colsA; j++) {
                    if (a[i][j] != b[i][j]) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Duplicates the matrix.
     *
     * @param a the matrix to duplicate.
     * @return copy of the matrix.
     */
    public static double[][] clone(double[][] a) {
        double[][] res = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            System.arraycopy(a[i], 0, res[i], 0, a[0].length);
        }
        return res;
    }

    /**
     * Perform the matrix transposition.
     *
     * @param a input matrix.
     * @return transposed matrix.
     */
    public static double[][] transpose(double[][] a) {
        int rows = a.length;
        int cols = a[0].length;
        double[][] res = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res[j][i] = a[i][j];
            }
        }
        return res;
    }

    /**
     * Mimics Matlab function for reshape: returns the m-by-n matrix B whose
     * elements are taken column-wise from A. An error results if A does not
     * have m*n elements.
     *
     * @param a the source matrix.
     * @param n number of rows in the new matrix.
     * @param m number of columns in the new matrix.
     *
     * @return reshaped matrix.
     */
    public static double[][] reshape(double[][] a, int n, int m) {
        int cEl = 0;
        int aRows = a.length;

        double[][] res = new double[n][m];

        for (int j = 0; j < m; j++) {
            for (int i = 0; i < n; i++) {
                res[i][j] = a[cEl % aRows][cEl / aRows];
                cEl++;
            }
        }

        return res;
    }

    /**
     * Computes column means for the matrix.
     *
     * @param a the input matrix.
     * @return result.
     */
    public static double[] colMeans(double[][] a) {
        double[] res = new double[a[0].length];
        for (int j = 0; j < a[0].length; j++) {
            double sum = 0;
            int counter = 0;
            for (int i = 0; i < a.length; i++) {
                if (Double.isNaN(a[i][j]) || Double.isInfinite(a[i][j])) {
                    continue;
                }
                sum += a[i][j];
                counter += 1;
            }
            if (counter == 0) {
                res[j] = Double.NaN;
            } else {
                res[j] = sum / ((Integer) counter).doubleValue();
            }
        }
        return res;
    }

    /**
     * Prints out matrix.
     *
     * @param a the matrix to print.
     * @return ready for console output string.
     */
    public static String toString(double[][] a) {
        int rows = a.length;
        int cols = a[0].length;
        StringBuffer sb = new StringBuffer(4000);
        Formatter formatter = new Formatter(sb, Locale.US);

        sb.append("       ");
        for (int j = 0; j < cols; j++) {
            formatter.format("     [%1$3d]", j);
        }
        sb.append(CR);

        for (int i = 0; i < rows; i++) {
            formatter.format(" [%1$3d] ", i);
            for (int j = 0; j < cols; j++) {
                formatter.format(" %1$ 6f", a[i][j]);
            }
            sb.append(CR);
        }

        return sb.toString();
    }
}
