package org.clueminer.math.matrix;

import java.util.Arrays;
import org.clueminer.math.Matrix;

/**
 * This matrix should be used for saving memory in case when you compute
 * frequently symmetrical similarity matrices it could make quite a difference.
 *
 * Space used: n * (n - 1) / 2 ~ O(n^2)
 * (all values are stored in one dimensional array)
 *
 * It pretends to be normal matrix, so you can perform all operations as with
 * normal matrix. We don't even consider assigning number to diagonal elements
 * because diagonal should be equal to 1
 *
 * @author Tomas Barton
 */
public class SymmetricMatrix extends AbstractMatrix implements Matrix {

    private static final long serialVersionUID = -1162819274048880670L;
    private double[] A;
    private int n;
    /**
     * by default we suppose that on diagonal are ones
     */
    private double diagonalValue = 0.0;

    /**
     * Initialize a square matrix
     *
     * @param dim
     */
    public SymmetricMatrix(int dim) {
        initMatrix(dim, dim);
    }

    /**
     *
     * @param rows
     * @param cols
     */
    public SymmetricMatrix(int rows, int cols) {
        initMatrix(rows, cols);
    }

    /**
     * Initialize square matrix with
     *
     * @param rows
     * @param cols
     * @param value default value in whole matrix
     */
    public SymmetricMatrix(int rows, int cols, double value) {
        initMatrix(rows, cols);
        //initialize matrix with given value
        diagonalValue = value;
        Arrays.fill(A, value);
    }

    private void initMatrix(int rows, int cols) {
        if (rows != cols) {
            throw new IllegalArgumentException("invalid dimensions for SymmetricalMatrix. it must be a squared matrix " + rows + "x" + cols);
        }
        if (rows < 2) {
            throw new IllegalArgumentException("invalid dimension, matrix of size " + rows + " doesn't make much sense");
        }

        n = rows;
        /**
         * actual needed space is n * (n / 2 - 1) instead of n^2
         */
        A = new double[triangleSize(n)];
    }

    /**
     * Compute size of triangular matrix (n x n) minus diagonal
     *
     * @param n number of rows (or columns) for square matrix
     * @return
     */
    protected int triangleSize(int n) {
        return ((n - 1) * n) >>> 1;
    }

    /**
     * Return an index where is actually item stored
     *
     * A simple hash function for storing lower triangular matrix in one
     * dimensional array
     *
     * i should not be equal to j (diagonal numbers are not stored!)
     *
     * @param i row index
     * @param j column index
     * @return index in one-dimensional array
     */
    private int map(int i, int j) {
        if (i < j) {
            /**
             * swap variables, matrix is symmetrical, we work with lower
             * triangular matrix
             */
            int tmp = i;
            i = j;
            j = tmp;
        }
        /**
         * it's basically a sum of arithmetic row (we need to know how many
         * numbers could be allocated before given position [x,y])
         */
        return triangleSize(i) + j;
    }

    @Override
    public Matrix copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Shallow copy is not supported
     *
     * @return deep copy of matrix
     */
    @Override
    public double[][] getArray() {
        double[][] res = new double[n][n];
        double val;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                val = this.get(i, j);
                res[i][j] = val;
                res[j][i] = val;
            }
        }
        return res;
    }

    /**
     * Same as getArray()
     *
     * @return deep copy of matrix
     */
    @Override
    public double[][] getArrayCopy() {
        return getArray();
    }

    @Override
    public int rowsCount() {
        return n;
    }

    @Override
    public int columnsCount() {
        return n;
    }

    @Override
    public double[] getColumnPackedCopy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] getRowPackedCopy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix transpose() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double norm1() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double get(int i, int j) {
        if (i == j) {
            return diagonalValue;
        }
        return A[map(i, j)];
    }

    @Override
    public void set(int i, int j, double s) {
        if (i == j && s != diagonalValue) {
            throw new IllegalArgumentException("diagonal items are not writable");
        }
        A[map(i, j)] = s;
    }

    @Override
    public Matrix getMatrix(int i0, int i1, int j0, int j1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix getMatrix(int[] r, int[] c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix getMatrix(int i0, int i1, int[] c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix getMatrix(int[] r, int j0, int j1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMatrix(int i0, int i1, int j0, int j1, Matrix X) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMatrix(int[] r, int[] c, Matrix X) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMatrix(int[] r, int j0, int j1, Matrix X) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMatrix(int i0, int i1, int[] c, Matrix X) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double normInf() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double normF() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix uminus() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix plus(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix plusEquals(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix minus(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix minusEquals(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix arrayTimes(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix arrayTimesEquals(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix arrayRightDivide(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix arrayRightDivideEquals(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix arrayLeftDivide(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix arrayLeftDivideEquals(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix times(double s) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix timesEquals(double s) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix times(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double trace() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static SymmetricMatrix random(int m) {
        return random(m, m);
    }

    /**
     * Generate matrix with random elements
     *
     * @param m Number of rows.
     * @param n Number of columns.
     * @return An m-by-n matrix with uniformly distributed random elements.
     */
    public static SymmetricMatrix random(int m, int n) {
        SymmetricMatrix A = new SymmetricMatrix(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < i; j++) {
                A.set(i, j, Math.random());
            }
        }
        return A;
    }

    @Override
    public boolean has(int i, int j) {
        int mapped = map(i, j);
        return mapped < A.length && mapped >= 0;
    }

    @Override
    public void setDiagonal(double value) {
        throw new UnsupportedOperationException("Changing diagonal elemets is not supported. Use {@link SymmetricMatrixDiag} instead.");
    }
}
