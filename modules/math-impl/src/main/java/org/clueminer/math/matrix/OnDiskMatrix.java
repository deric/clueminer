package org.clueminer.math.matrix;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.DoubleBuffer;
import java.nio.channels.FileChannel;
import java.text.NumberFormat;
import org.clueminer.math.CholeskyDecomposition;
import org.clueminer.math.DoubleVector;
import org.clueminer.math.EigenvalueDecomposition;
import org.clueminer.math.LUDecomposition;
import org.clueminer.math.Matrix;
import org.clueminer.math.MatrixVector;
import org.clueminer.math.QRDecomposition;
import org.clueminer.math.SingularValueDecomposition;
import org.clueminer.math.SparseVector;
import org.clueminer.utils.Duple;

/**
 * A Matrix implementation that uses a binary file to read and write Returns a
 * copy of the specified rowvalues of the matrix. The matrix is stored in
 * row-column order on disk, so in-order column accesses to elments in a row
 * will perform much better than sequential row accesses to the same column.
 *
 * <p>
 *
 * If a {@link IOException} is ever raised as a part of executing an the methods
 * of an instance, the exception is rethrown as a {@link IOError}.
 *
 * @author David Jurgens
 */
public class OnDiskMatrix implements Matrix {

    /**
     * The number of bytes in a double.
     */
    private static final int BYTES_PER_DOUBLE = 8;
    private static final int MAX_ELEMENTS_PER_REGION
            = Integer.MAX_VALUE / BYTES_PER_DOUBLE;
    private static final long serialVersionUID = -4602524593724901641L;
    /**
     * The on-disk storage space for the matrix
     */
    //private final RandomAccessFile matrix;
    private final DoubleBuffer[] matrixRegions;
    /**
     * The {@code File} instances that back the matrix regions
     */
    private final File[] backingFiles;
    /**
     * The number of rows stored in this {@code Matrix}.
     */
    private final int rows;
    /**
     * The number of columns stored in this {@code Matrix}.
     */
    private final int cols;

    /**
     * Create a matrix of the provided size using a temporary file.
     *
     * @throws IOError if the backing file for this matrix cannot be created
     */
    public OnDiskMatrix(int rows, int cols) {

        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("dimensions must be positive");
        }

        this.rows = rows;
        this.cols = cols;

        // Determine how big the array will need to be
        // Note that to map the array into memory, we have to avoid the case
        // where any mapped part of the array is larger than Integer.MAX_VALUE.
        // Therefore, divide the array up into regions less than this size.
        int numRegions
                = (int) (((long) rows * cols) / MAX_ELEMENTS_PER_REGION) + 1;
        matrixRegions = new DoubleBuffer[numRegions];
        backingFiles = new File[numRegions];
        for (int region = 0; region < numRegions; ++region) {
            int sizeInBytes = (region + 1 == numRegions)
                    ? (int) ((((long) rows * cols)
                    % MAX_ELEMENTS_PER_REGION) * BYTES_PER_DOUBLE)
                    : MAX_ELEMENTS_PER_REGION * BYTES_PER_DOUBLE;
            Duple<DoubleBuffer, File> d = createTempBuffer(sizeInBytes);
            matrixRegions[region] = d.x;
            backingFiles[region] = d.y;
        }
    }

    /**
     *
     * @param size the size of the buffer in bytes
     */
    private static Duple<DoubleBuffer, File> createTempBuffer(int size) {
        try {
            File f = File.createTempFile("OnDiskMatrix", ".matrix");
            // Make sure the temp file goes away since it can get fairly large
            // for big matrices
            f.deleteOnExit();
            RandomAccessFile raf = new RandomAccessFile(f, "rw");
            FileChannel fc = raf.getChannel();
            DoubleBuffer contextBuffer
                    = fc.map(FileChannel.MapMode.READ_WRITE, 0, size).asDoubleBuffer();
            fc.close();
            return new Duple<DoubleBuffer, File>(contextBuffer, f);
        } catch (IOException ioe) {
            throw new IOError(ioe);
        }
    }

    /**
     * Checks that the indices are within the bounds of the matrix and throws an
     * exception if they are not.
     */
    private void checkIndices(int row, int col) {
        if (row < 0 || row >= rows) {
            throw new ArrayIndexOutOfBoundsException("row: " + row);
        } else if (col < 0 || col >= cols) {
            throw new ArrayIndexOutOfBoundsException("column: " + col);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double get(int row, int col) {
        int region = getMatrixRegion(row, col);
        int regionOffset = getRegionOffset(row, col);
        return matrixRegions[region].get(regionOffset);
    }

    /**
     * {@inheritDoc}
     */
    public double[] getColumn(int column) {
        double[] values = new double[rows];
        for (int row = 0; row < rows; ++row) {
            values[row] = get(row, column);
        }
        return values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatrixVector getColumnVector(int column) {
        return new MatrixColumnVector(this, column);
    }

    /**
     * {@inheritDoc}
     */
    public double[] getRow(int row) {
        int rowStartRegion = getMatrixRegion(row, 0);
        int rowEndRegion = getMatrixRegion(row + 1, 0);
        double[] rowVal = new double[cols];
        if (rowStartRegion == rowEndRegion) {
            int rowStartIndex = getRegionOffset(row, 0);
            DoubleBuffer region = matrixRegions[rowStartRegion];
            for (int col = 0; col < cols; ++col) {
                rowVal[col] = region.get(col + rowStartIndex);
            }
        } else {
            DoubleBuffer firstRegion = matrixRegions[rowStartRegion];
            DoubleBuffer secondRegion = matrixRegions[rowEndRegion];
            int rowStartIndex = getRegionOffset(row, 0);
            int rowOffset = 0;
            for (; rowStartIndex + rowOffset < MAX_ELEMENTS_PER_REGION;
                    ++rowOffset) {
                rowVal[rowOffset] = firstRegion.get(rowOffset + rowStartIndex);
            }
            // Fill from the second region
            for (int i = 0; rowOffset < rowVal.length; ++i, ++rowOffset) {
                rowVal[rowOffset] = secondRegion.get(i);
            }
        }
        return rowVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatrixVector getRowVector(int row) {
        return new MatrixRowVector(this, row);
    }

    /**
     * {@inheritDoc}
     */
    public int columns() {
        return cols;
    }

    private int getMatrixRegion(long row, long col) {
        long element = row * cols + col;
        return (int) (element / MAX_ELEMENTS_PER_REGION);
    }

    private int getRegionOffset(long row, long col) {
        long element = row * cols + col;
        return (int) (element % MAX_ELEMENTS_PER_REGION);
    }

    /**
     * {@inheritDoc}
     */
    public void set(int row, int col, double val) {
        int region = getMatrixRegion(row, col);
        int regionOffset = getRegionOffset(row, col);
        matrixRegions[region].put(regionOffset, val);
    }

    /**
     * {@inheritDoc}
     */
    public void setColumn(int column, double[] values) {
        for (int row = 0; row < rows; ++row) {
            set(row, column, values[row]);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setColumn(int column, DoubleVector values) {
        for (int row = 0; row < rows; ++row) {
            set(row, column, values.get(row));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setRow(int row, double[] vals) {
        if (vals.length != cols) {
            throw new IllegalArgumentException(
                    "The number of values does not match the number of columns");
        }
        for (int i = 0; i < vals.length; ++i) {
            set(row, i, vals[i]);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setRow(int row, DoubleVector values) {
        if (values.size() != cols) {
            throw new IllegalArgumentException(
                    "The number of values does not match the number of columns");
        }

        if (values instanceof SparseVector) {
            SparseVector sv = (SparseVector) values;
            for (int i : sv.getNonZeroIndices()) {
                set(row, i, values.get(i));
            }
        } else {
            for (int i = 0; i < values.size(); ++i) {
                set(row, i, values.get(i));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public double[][] toDenseArray() {
        if (matrixRegions.length > 1) {
            throw new UnsupportedOperationException(
                    "matrix is too large to fit into memory");
        }
        double[][] m = new double[rows][cols];
        DoubleBuffer b = matrixRegions[0];
        b.rewind();
        for (int row = 0; row < rows; ++row) {
            b.get(m[row]);
        }
        return m;
    }

    /**
     * {@inheritDoc}
     */
    public int rows() {
        return rows;
    }

    /**
     * Upon finalize, deletes all of the backing files. This is most necessary
     * when the JVM is long-running with many {@code OnDiskMatrix} instances
     * that are not deleted until exit.
     */
    @Override
    protected void finalize() {
        // Delete all of the backing files, silently catching all errors
        for (File f : backingFiles) {
            try {
                f.delete();
            } catch (Throwable t) {
                // silent
            }
        }
    }

    @Override
    public Matrix copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[][] getArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[][] getArrayCopy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int rowsCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int columnsCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public double norm2() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public LUDecomposition lu() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public QRDecomposition qr() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CholeskyDecomposition chol() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SingularValueDecomposition svd() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EigenvalueDecomposition eig() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix solve(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix solveTranspose(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix inverse() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double det() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int rank() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double cond() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double trace() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print(int w, int d) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print(PrintWriter output, int w, int d) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print(NumberFormat format, int width) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print(PrintWriter output, NumberFormat format, int width) {
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
