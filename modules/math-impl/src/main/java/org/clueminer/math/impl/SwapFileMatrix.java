package org.clueminer.math.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.NumberFormat;
import org.clueminer.math.CholeskyDecomposition;
import org.clueminer.math.EigenvalueDecomposition;
import org.clueminer.math.LUDecomposition;
import org.clueminer.math.Matrix;
import org.clueminer.math.MatrixVector;
import org.clueminer.math.QRDecomposition;
import org.clueminer.math.SingularValueDecomposition;

/**
 * A matrix that is stored in a file on disk.
 *
 * @author Thomas Abeel
 *
 */
final class SwapFileMatrix implements Matrix {

    private static final long serialVersionUID = -715479394793667312L;

    private RandomAccessFile matrix;

    private int rows;

    private int cols;

    public SwapFileMatrix(int cols, int rows) throws IOException {
        this.cols = cols;
        this.rows = rows;
        File swapFile = File.createTempFile("swap", "matrix");
        swapFile.deleteOnExit();
        matrix = new RandomAccessFile(swapFile, "rw");
    }

    @Override
    public int columnsCount() {
        return cols;
    }

    @Override
    public double get(int col, int row) {
        try {
            matrix.seek((col * row + row) * 8);
            return matrix.readDouble();
        } catch (IOException e) {
            System.err.println("Something went wrong, but we return 0 anyway.");
            return 0;
        }
    }

    @Override
    public void set(int col, int row, double value) {
        try {
            matrix.seek((col * row + row) * 8);
            matrix.writeDouble(value);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public int rowsCount() {
        return rows;
    }

    @Override
    public Matrix copy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double[][] getArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double[][] getArrayCopy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double[] getColumnPackedCopy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double[] getRowPackedCopy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix transpose() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double norm1() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double norm2() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix getMatrix(int i0, int i1, int j0, int j1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix getMatrix(int[] r, int[] c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix getMatrix(int i0, int i1, int[] c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix getMatrix(int[] r, int j0, int j1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMatrix(int i0, int i1, int j0, int j1, Matrix X) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMatrix(int[] r, int[] c, Matrix X) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMatrix(int[] r, int j0, int j1, Matrix X) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMatrix(int i0, int i1, int[] c, Matrix X) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double normInf() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double normF() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix uminus() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix plus(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix plusEquals(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix minus(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix minusEquals(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix arrayTimes(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix arrayTimesEquals(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix arrayRightDivide(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix arrayRightDivideEquals(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix arrayLeftDivide(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix arrayLeftDivideEquals(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix times(double s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix timesEquals(double s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix times(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LUDecomposition lu() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public QRDecomposition qr() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CholeskyDecomposition chol() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SingularValueDecomposition svd() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EigenvalueDecomposition eig() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix solve(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix solveTranspose(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix inverse() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double det() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int rank() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double cond() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double trace() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void print(int w, int d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void print(PrintWriter output, int w, int d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void print(NumberFormat format, int width) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void print(PrintWriter output, NumberFormat format, int width) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MatrixVector getRowVector(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MatrixVector getColumnVector(int j) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void printUpper(int w, int d) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void printLower(int w, int d) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean has(int i, int j) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void printFancy(int w, int d) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDiagonal(double value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
