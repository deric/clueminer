package org.clueminer.math.matrix;

import java.util.Arrays;
import org.clueminer.math.Matrix;
import org.clueminer.math.MatrixVector;

/**
 * Jama = Java Matrix class.
 *
 *
 * <P>
 * The Java JMatrix Class provides the fundamental operations of numerical
 * linear algebra. Various constructors create Matrices from two dimensional
 * arrays of double precision floating point numbers. Various "gets" and "sets"
 * provide access to submatrices and matrix elements. Several methods implement
 * basic matrix arithmetic, including matrix addition and multiplication, matrix
 * norms, and element-by-element array operations. Methods for reading and
 * printing matrices are also included. All the operations in this version of
 * the JMatrix Class involve real matrices. Complex matrices may be handled in a
 * future version.
 *
 *
 * <P>
 * Five fundamental matrix decompositions, which consist of pairs or triples of
 * matrices, permutation vectors, and the like, produce results in five
 * decomposition classes. These decompositions are accessed by the JMatrix class
 * to compute solutions of simultaneous linear equations, determinants, inverses
 * and other matrix functions. The five decompositions are:
 *
 *
 * <P>
 * <UL> <LI>Cholesky Decomposition of symmetric, positive definite matrices.
 *
 * <LI>LU Decomposition of rectangular matrices.
 *
 * <LI>QR Decomposition of rectangular matrices.
 *
 * <LI>Singular Value Decomposition of rectangular matrices.
 *
 * <LI>Eigenvalue Decomposition of both symmetric and nonsymmetric square
 * matrices. </UL>
 *
 * <DL> <DT><B>Example of use:</B></DT>
 * <P>
 * <DD>Solve a linear system A x = b and compute the residual norm, ||b - A x||.
 * <P>
 * <
 * PRE>
 * double[][] vals = {{1.,2.,3},{4.,5.,6.},{7.,8.,10.}}; JMatrix A = new
 * JMatrix(vals); JMatrix b = JMatrix.random(3,1); JMatrix x = A.solve(b);
 * JMatrix r = A.times(x).minus(b); double rnorm = r.normInf();
 * </PRE></DD> </DL>
 *
 * @author The MathWorks, Inc. and the National Institute of Standards and
 * Technology.
 * @version 5 August 1998
 */
public class JMatrix extends AbstractMatrix implements Cloneable, java.io.Serializable, Matrix {

    private static final long serialVersionUID = -3461325682280184325L;

    /**
     * Array for internal storage of elements.
     *
     * @serial internal array storage.
     */
    private double[][] A;
    /**
     * Row and column dimensions.
     *
     * @serial row dimension.
     * @serial column dimension.
     */
    private final int m, n;

    /**
     * Construct an m-by-n matrix of zeros.
     *
     * @param m Number of rows.
     * @param n Number of columns.
     */
    public JMatrix(int m, int n) {
        this.m = m;
        this.n = n;
        A = new double[m][n];
    }

    /**
     * Construct an m-by-n constant matrix.
     *
     * @param m Number of rows.
     * @param n Number of columns.
     * @param s Fill the matrix with this scalar value.
     */
    public JMatrix(int m, int n, double s) {
        this.m = m;
        this.n = n;
        A = new double[m][n];
        for (int i = 0; i < m; i++) {
            Arrays.fill(A[i], s);
        }
    }

    /**
     * Construct a matrix from a 2-D array.
     *
     * @param A Two-dimensional array of doubles.
     * @exception IllegalArgumentException All rows must have the same length
     * @see #constructWithCopy
     */
    public JMatrix(double[][] A) {
        m = A.length;
        n = A[0].length;
        for (int i = 0; i < m; i++) {
            if (A[i].length != n) {
                throw new IllegalArgumentException("All rows must have the same length.");
            }
        }
        this.A = A;
    }

    /**
     * Construct a matrix quickly without checking arguments.
     *
     * @param A Two-dimensional array of doubles.
     * @param m Number of rows.
     * @param n Number of columns.
     */
    public JMatrix(double[][] A, int m, int n) {
        this.A = A;
        this.m = m;
        this.n = n;
    }

    /**
     * Construct a matrix from a one-dimensional packed array
     *
     * @param vals One-dimensional array of doubles, packed by columns (ala
     * Fortran).
     * @param m Number of rows.
     * @exception IllegalArgumentException Array length must be a multiple of m.
     */
    public JMatrix(double vals[], int m) {
        this.m = m;
        n = (m != 0 ? vals.length / m : 0);
        if (m * n != vals.length) {
            throw new IllegalArgumentException("Array length must be a multiple of m.");
        }
        A = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = vals[i + j * m];
            }
        }
    }

    /**
     * Construct a matrix from a copy of a 2-D array.
     *
     * @param A Two-dimensional array of doubles.
     * @return
     * @exception IllegalArgumentException All rows must have the same length
     */
    public static Matrix constructWithCopy(double[][] A) {
        int m = A.length;
        int n = A[0].length;
        JMatrix X = new JMatrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++) {
            if (A[i].length != n) {
                throw new IllegalArgumentException("All rows must have the same length.");
            }
            System.arraycopy(A[i], 0, C[i], 0, n);
        }
        return X;
    }

    /**
     * Make a deep copy of a matrix
     */
    @Override
    public Matrix copy() {
        Matrix X = new JMatrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++) {
            System.arraycopy(A[i], 0, C[i], 0, n);
        }
        return X;
    }

    /**
     * Clone the JMatrix object.
     *
     * @return
     */
    @Override
    public Object clone() {
        return this.copy();
    }

    /**
     * Access the internal two-dimensional array.
     *
     * @return Pointer to the two-dimensional array of matrix elements.
     */
    @Override
    public double[][] getArray() {
        return A;
    }

    /**
     * Copy the internal two-dimensional array.
     *
     * @return Two-dimensional array copy of matrix elements.
     */
    @Override
    public double[][] getArrayCopy() {
        double[][] C = new double[m][n];
        for (int i = 0; i < m; i++) {
            System.arraycopy(A[i], 0, C[i], 0, n);
        }
        return C;
    }

    /**
     * Make a one-dimensional column packed copy of the internal array.
     *
     * @return JMatrix elements packed in a one-dimensional array by columns.
     */
    @Override
    public double[] getColumnPackedCopy() {
        double[] vals = new double[m * n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                vals[i + j * m] = A[i][j];
            }
        }
        return vals;
    }

    /**
     * Make a one-dimensional row packed copy of the internal array.
     *
     * @return JMatrix elements packed in a one-dimensional array by rows.
     */
    @Override
    public double[] getRowPackedCopy() {
        double[] vals = new double[m * n];
        for (int i = 0; i < m; i++) {
            System.arraycopy(A[i], 0, vals, i * n, n);
        }
        return vals;
    }

    /**
     * Get row dimension.
     *
     * @return m, the number of rows.
     */
    @Override
    public int rowsCount() {
        return m;
    }

    /**
     * Get column dimension.
     *
     * @return n, the number of columns.
     */
    @Override
    public int columnsCount() {
        return n;
    }

    /**
     * Get a single element.
     *
     * @param i Row index.
     * @param j Column index.
     * @return A(i,j)
     * @exception ArrayIndexOutOfBoundsException
     */
    @Override
    public double get(int i, int j) {
        return A[i][j];
    }

    /**
     * Get a submatrix.
     *
     * @param i0 Initial row index
     * @param i1 Final row index
     * @param j0 Initial column index
     * @param j1 Final column index
     * @return A(i0:i1,j0:j1)
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    @Override
    public Matrix getMatrix(int i0, int i1, int j0, int j1) {
        Matrix X = new JMatrix(i1 - i0 + 1, j1 - j0 + 1);
        double[][] B = X.getArray();
        try {
            for (int i = i0; i <= i1; i++) {
                for (int j = j0; j <= j1; j++) {
                    B[i - i0][j - j0] = A[i][j];
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
     * Get a submatrix.
     *
     * @param r Array of row indices.
     * @param c Array of column indices.
     * @return A(r(:),c(:))
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    @Override
    public Matrix getMatrix(int[] r, int[] c) {
        Matrix X = new JMatrix(r.length, c.length);
        double[][] B = X.getArray();
        try {
            for (int i = 0; i < r.length; i++) {
                for (int j = 0; j < c.length; j++) {
                    B[i][j] = A[r[i]][c[j]];
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
     * Get a submatrix.
     *
     * @param i0 Initial row index
     * @param i1 Final row index
     * @param c Array of column indices.
     * @return A(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    @Override
    public Matrix getMatrix(int i0, int i1, int[] c) {
        Matrix X = new JMatrix(i1 - i0 + 1, c.length);
        double[][] B = X.getArray();
        try {
            for (int i = i0; i <= i1; i++) {
                for (int j = 0; j < c.length; j++) {
                    B[i - i0][j] = A[i][c[j]];
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
     * Get a submatrix.
     *
     * @param r Array of row indices.
     * @param i0 Initial column index
     * @param i1 Final column index
     * @return A(r(:),j0:j1)
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    @Override
    public Matrix getMatrix(int[] r, int i0, int i1) {
        Matrix X = new JMatrix(r.length, i1 - i0 + 1);
        double[][] B = X.getArray();
        try {
            for (int i = 0; i < r.length; i++) {
                for (int j = i0; j <= i1; j++) {
                    B[i][j - i0] = A[r[i]][j];
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
     * Set a single element.
     *
     * @param i Row index.
     * @param j Column index.
     * @param s A(i,j).
     * @exception ArrayIndexOutOfBoundsException
     */
    @Override
    public void set(int i, int j, double s) {
        A[i][j] = s;
    }

    /**
     * Set a submatrix.
     *
     * @param i0 Initial row index
     * @param i1 Final row index
     * @param j0 Initial column index
     * @param j1 Final column index
     * @param X A(i0:i1,j0:j1)
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    @Override
    public void setMatrix(int i0, int i1, int j0, int j1, Matrix X) {
        try {
            for (int i = i0; i <= i1; i++) {
                for (int j = j0; j <= j1; j++) {
                    A[i][j] = X.get(i - i0, j - j0);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     *
     * @param r Array of row indices.
     * @param c Array of column indices.
     * @param X A(r(:),c(:))
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    @Override
    public void setMatrix(int[] r, int[] c, Matrix X) {
        try {
            for (int i = 0; i < r.length; i++) {
                for (int j = 0; j < c.length; j++) {
                    A[r[i]][c[j]] = X.get(i, j);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     *
     * @param r Array of row indices.
     * @param j0 Initial column index
     * @param j1 Final column index
     * @param X A(r(:),j0:j1)
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    @Override
    public void setMatrix(int[] r, int j0, int j1, Matrix X) {
        try {
            for (int i = 0; i < r.length; i++) {
                for (int j = j0; j <= j1; j++) {
                    A[r[i]][j] = X.get(i, j - j0);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    /**
     * Set a submatrix.
     *
     * @param i0 Initial row index
     * @param i1 Final row index
     * @param c Array of column indices.
     * @param X A(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    @Override
    public void setMatrix(int i0, int i1, int[] c, Matrix X) {
        try {
            for (int i = i0; i <= i1; i++) {
                for (int j = 0; j < c.length; j++) {
                    A[i][c[j]] = X.get(i - i0, j);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    /**
     * Matrix transpose.
     *
     * @return A'
     */
    @Override
    public Matrix transpose() {
        Matrix X = new JMatrix(n, m);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[j][i] = A[i][j];
            }
        }
        return X;
    }

    /**
     * One norm
     *
     * @return maximum column sum.
     */
    @Override
    public double norm1() {
        double f = 0;
        for (int j = 0; j < n; j++) {
            double s = 0;
            for (int i = 0; i < m; i++) {
                s += Math.abs(A[i][j]);
            }
            f = Math.max(f, s);
        }
        return f;
    }

    /**
     * Infinity norm
     *
     * @return maximum row sum.
     */
    @Override
    public double normInf() {
        double f = 0;
        for (int i = 0; i < m; i++) {
            double s = 0;
            for (int j = 0; j < n; j++) {
                s += Math.abs(A[i][j]);
            }
            f = Math.max(f, s);
        }
        return f;
    }

    /**
     * Frobenius norm
     *
     * @return sqrt of sum of squares of all elements.
     */
    @Override
    public double normF() {
        double f = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                f = AbstractMatrix.hypot(f, A[i][j]);
            }
        }
        return f;
    }

    /**
     * Unary minus
     *
     * @return -A
     */
    @Override
    public Matrix uminus() {
        Matrix X = new JMatrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = -A[i][j];
            }
        }
        return X;
    }

    /**
     * C = A + B
     *
     * @param B another matrix
     * @return A + B
     */
    @Override
    public Matrix plus(Matrix B) {
        checkMatrixDimensions(B);
        Matrix C = new JMatrix(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C.set(i, j, A[i][j] + B.get(i, j));
            }
        }
        return C;
    }

    /**
     * A = A + B
     *
     * @param B another matrix
     * @return A + B
     */
    @Override
    public Matrix plusEquals(Matrix B) {
        checkMatrixDimensions(B);
        double[][] Barray = B.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = A[i][j] + Barray[i][j];
            }
        }
        return this;
    }

    /**
     * C = A - B
     *
     * @param B another matrix
     * @return A - B
     */
    @Override
    public Matrix minus(Matrix B) {
        checkMatrixDimensions(B);
        Matrix X = new JMatrix(m, n);
        double[][] C = X.getArray();
        double[][] Barray = B.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] - Barray[i][j];
            }
        }
        return X;
    }

    /**
     * A = A - B
     *
     * @param B another matrix
     * @return A - B
     */
    @Override
    public Matrix minusEquals(Matrix B) {
        checkMatrixDimensions(B);
        double[][] Barray = B.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = A[i][j] - Barray[i][j];
            }
        }
        return this;
    }

    /**
     * Element-by-element multiplication, C = A.*B
     *
     * @param B another matrix
     * @return A.*B
     */
    @Override
    public Matrix arrayTimes(Matrix B) {
        checkMatrixDimensions(B);
        Matrix X = new JMatrix(m, n);
        double[][] C = X.getArray();
        double[][] Barray = B.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] * Barray[i][j];
            }
        }
        return X;
    }

    /**
     * Element-by-element multiplication in place, A = A.*B
     *
     * @param B another matrix
     * @return A.*B
     */
    @Override
    public Matrix arrayTimesEquals(Matrix B) {
        checkMatrixDimensions(B);
        double[][] Barray = B.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = A[i][j] * Barray[i][j];
            }
        }
        return this;
    }

    /**
     * Element-by-element right division, C = A./B
     *
     * @param B another matrix
     * @return A./B
     */
    @Override
    public Matrix arrayRightDivide(Matrix B) {
        checkMatrixDimensions(B);
        Matrix X = new JMatrix(m, n);
        double[][] C = X.getArray();
        double[][] Barray = B.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] / Barray[i][j];
            }
        }
        return X;
    }

    /**
     * Element-by-element right division in place, A = A./B
     *
     * @param B another matrix
     * @return A./B
     */
    @Override
    public Matrix arrayRightDivideEquals(Matrix B) {
        checkMatrixDimensions(B);
        double[][] Barray = B.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = A[i][j] / Barray[i][j];
            }
        }
        return this;
    }

    /**
     * Element-by-element left division, C = A.\B
     *
     * @param B another matrix
     * @return A.\B
     */
    @Override
    public Matrix arrayLeftDivide(Matrix B) {
        checkMatrixDimensions(B);
        Matrix X = new JMatrix(m, n);
        double[][] C = X.getArray();
        double[][] Barray = B.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = Barray[i][j] / A[i][j];
            }
        }
        return X;
    }

    /**
     * Element-by-element left division in place, A = A.\B
     *
     * @param B another matrix
     * @return A.\B
     */
    @Override
    public Matrix arrayLeftDivideEquals(Matrix B) {
        checkMatrixDimensions(B);
        double[][] Barray = B.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = Barray[i][j] / A[i][j];
            }
        }
        return this;
    }

    /**
     * Multiply a matrix by a scalar, C = s*A
     *
     * @param s scalar
     * @return s*A
     */
    @Override
    public Matrix times(double s) {
        Matrix X = new JMatrix(m, n);
        double[][] C = X.getArray();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = s * A[i][j];
            }
        }
        return X;
    }

    /**
     * Multiply a matrix by a scalar in place, A = s*A
     *
     * @param s scalar
     * @return replace A by s*A
     */
    @Override
    public Matrix timesEquals(double s) {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = s * A[i][j];
            }
        }
        return this;
    }

    /**
     * Linear algebraic matrix multiplication, A * B
     *
     * @param B another matrix
     * @return Matrix product, A * B
     * @exception IllegalArgumentException JMatrix inner dimensions must agree.
     */
    @Override
    public Matrix times(Matrix B) {
        if (B.rowsCount() != n) {
            throw new IllegalArgumentException("Matrix inner dimensions must agree.");
        }
        Matrix X = new JMatrix(m, B.columnsCount());
        double[][] C = X.getArray();
        double[] Bcolj = new double[n];
        double[][] Barray = B.getArray();
        for (int j = 0; j < B.columnsCount(); j++) {
            for (int k = 0; k < n; k++) {
                Bcolj[k] = Barray[k][j];
            }
            for (int i = 0; i < m; i++) {
                double[] Arowi = A[i];
                double s = 0;
                for (int k = 0; k < n; k++) {
                    s += Arowi[k] * Bcolj[k];
                }
                C[i][j] = s;
            }
        }
        return X;
    }

    /**
     * JMatrix trace.
     *
     * @return sum of the diagonal elements.
     */
    @Override
    public double trace() {
        double t = 0;
        for (int i = 0; i < Math.min(m, n); i++) {
            t += A[i][i];
        }
        return t;
    }

    @Override
    public MatrixVector getRowVector(int i) {
        return new MatrixRowVector(this, i);
    }

    @Override
    public MatrixVector getColumnVector(int j) {
        return new MatrixColumnVector(this, j);
    }

    /**
     * {@inheritDoc}
     *
     * @param i
     * @param j
     * @return
     */
    @Override
    public boolean has(int i, int j) {
        return i < m && j < n && i >= 0 && j >= 0;
    }

    @Override
    public void setDiagonal(double value) {
        if (m != n) {
            throw new IllegalArgumentException("A square matrix is required. Got [" + m + "x" + n + "]");
        }
        for (int i = 0; i < m; i++) {
            A[i][i] = value;
        }
    }
}
