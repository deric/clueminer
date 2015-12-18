/*
 * Copyright (C) 2011-2015 clueminer.org
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

import java.util.Arrays;
import org.clueminer.math.Matrix;

/**
 * Symmetrical matrix with values on diagonal
 *
 * @author deric
 */
public class SymmetricMatrixDiag extends AbstractMatrix implements Matrix {

    private static final long serialVersionUID = -6828075338981173574L;
    private double[] A;
    private int n;

    /**
     * Initialize a square matrix
     *
     * @param dim
     */
    public SymmetricMatrixDiag(int dim) {
        initMatrix(dim, dim);
    }

    /**
     *
     * @param rows
     * @param cols
     */
    public SymmetricMatrixDiag(int rows, int cols) {
        initMatrix(rows, cols);
    }

    /**
     * Initialize square matrix with
     *
     * @param rows
     * @param cols
     * @param value default value in whole matrix
     */
    public SymmetricMatrixDiag(int rows, int cols, double value) {
        initMatrix(rows, cols);
        //initialize matrix with given value
        Arrays.fill(A, value);
    }

    private void initMatrix(int rows, int cols) {
        if (rows != cols) {
            throw new IllegalArgumentException("invalid dimensions for SymmetricalMatrix. it must be a squared matrix " + rows + "x" + cols);
        }
        if (rows < 2) {
            throw new IllegalArgumentException("invalid dimension, matrix of size " + rows + "doesn't make much sense");
        }

        n = rows;
        /**
         * actual needed space is n * (n - 1) instead of n^2
         */
        A = new double[triangleSize(n)];
    }

    /**
     * Compute size of triangular matrix (n x n) including diagonal
     *
     * @param n number of rows (or columns) for square matrix
     * @return
     */
    protected int triangleSize(int n) {
        return ((n * (n - 1)) >>> 1) + n;
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
    protected int map(int i, int j) {
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
        return A[map(i, j)];
    }

    @Override
    public void set(int i, int j, double s) {
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
        checkMatrixDimensions(B);
        Matrix C = new JMatrix(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C.set(i, j, get(i, j) + B.get(i, j));
            }
        }
        return C;
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
        Matrix X = new JMatrix(n, n);
        double value;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                value = s * get(i, j);
                X.set(i, j, value);
                if (i != j) {
                    X.set(j, i, value);
                }
            }
        }
        return X;
    }

    @Override
    public Matrix timesEquals(double s) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix times(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Sum of the diagonal elements
     *
     * @return
     */
    @Override
    public double trace() {
        double t = 0;
        for (int i = 0; i < n; i++) {
            t += get(i, i);
        }
        return t;
    }

    public static SymmetricMatrixDiag random(int m) {
        return random(m, m);
    }

    /**
     * Generate matrix with random elements
     *
     * @param m Number of rows.
     * @param n Number of columns.
     * @return An m-by-n matrix with uniformly distributed random elements.
     */
    public static SymmetricMatrixDiag random(int m, int n) {
        SymmetricMatrixDiag A = new SymmetricMatrixDiag(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j <= i; j++) {
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
        for (int i = 0; i < n; i++) {
            set(i, i, value);
        }
    }

}
