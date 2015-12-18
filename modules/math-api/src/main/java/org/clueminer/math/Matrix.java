package org.clueminer.math;

import java.io.PrintWriter;
import java.io.Serializable;
import java.text.NumberFormat;

/**
 * Interface for matrix computation inspired by the JAMA matrix
 *
 * @author Tomas Barton
 */
public interface Matrix extends Serializable {

    /**
     * Make a deep copy of a matrix
     *
     * @return
     */
    Matrix copy();

    /**
     * Access the internal two-dimensional array.
     *
     * @return Pointer to the two-dimensional array of matrix elements.
     */
    double[][] getArray();

    /**
     * Deep copy the internal two-dimensional array.
     *
     * @return Two-dimensional array copy of matrix elements.
     */
    double[][] getArrayCopy();

    /**
     * Get row dimension.
     *
     * @return m, the number of rows.
     */
    int rowsCount();

    /**
     * Get column dimension.
     *
     * @return n, the number of columns.
     */
    int columnsCount();

    /**
     * Make a one-dimensional column packed copy of the internal array.
     *
     * @return Matrix elements packed in a one-dimensional array by columns.
     */
    double[] getColumnPackedCopy();

    /**
     * Make a one-dimensional row packed copy of the internal array.
     *
     * @return Matrix elements packed in a one-dimensional array by rows.
     */
    double[] getRowPackedCopy();

    /**
     * Matrix transpose.
     *
     * @return A'
     */
    Matrix transpose();

    /**
     * One norm
     *
     * @return maximum column sum.
     */
    double norm1();

    /**
     * Two norm
     *
     * @return maximum singular value.
     */
    double norm2();

    /**
     * Get a single element.
     *
     * @param i Row index.
     * @param j Column index.
     * @return A(i,j)
     * @exception ArrayIndexOutOfBoundsException
     */
    double get(int i, int j);

    /**
     * Should check whether number with given row, column number could be stored
     * in the matrix
     *
     * @param i
     * @param j
     * @return true when number is stored in matrix
     */
    boolean has(int i, int j);

    /**
     *
     * @param i
     * @return vector referencing Matrix row
     */
    MatrixVector getRowVector(int i);

    /**
     *
     * @param j
     * @return vector referencing Matrix column
     */
    MatrixVector getColumnVector(int j);

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
    Matrix getMatrix(int i0, int i1, int j0, int j1);

    /**
     * Get a submatrix.
     *
     * @param r Array of row indices.
     * @param c Array of column indices.
     * @return A(r(:),c(:))
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    Matrix getMatrix(int[] r, int[] c);

    /**
     * Get a submatrix.
     *
     * @param i0 Initial row index
     * @param i1 Final row index
     * @param c Array of column indices.
     * @return A(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    Matrix getMatrix(int i0, int i1, int[] c);

    /**
     * Get a submatrix.
     *
     * @param r Array of row indices.
     * @param i0 Initial column index
     * @param i1 Final column index
     * @return A(r(:),i0:i1)
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    Matrix getMatrix(int[] r, int i0, int i1);

    /**
     * Set a single element.
     *
     * @param i Row index.
     * @param j Column index.
     * @param s A(i,j).
     * @exception ArrayIndexOutOfBoundsException
     */
    void set(int i, int j, double s);

    /**
     * Set given value on matrix diagonal (works only with square matrix).
     *
     * @param value
     */
    void setDiagonal(double value);

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
    void setMatrix(int i0, int i1, int j0, int j1, Matrix X);

    /**
     * Set a submatrix.
     *
     * @param r Array of row indices.
     * @param c Array of column indices.
     * @param X A(r(:),c(:))
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    void setMatrix(int[] r, int[] c, Matrix X);

    /**
     * Set a submatrix.
     *
     * @param r Array of row indices.
     * @param j0 Initial column index
     * @param j1 Final column index
     * @param X A(r(:),j0:j1)
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    void setMatrix(int[] r, int j0, int j1, Matrix X);

    /**
     * Set a submatrix.
     *
     * @param i0 Initial row index
     * @param i1 Final row index
     * @param c Array of column indices.
     * @param X A(i0:i1,c(:))
     * @exception ArrayIndexOutOfBoundsException Submatrix indices
     */
    void setMatrix(int i0, int i1, int[] c, Matrix X);

    /**
     * Infinity norm
     *
     * @return maximum row sum.
     */
    double normInf();

    /**
     * Frobenius norm
     *
     * @return sqrt of sum of squares of all elements.
     */
    double normF();

    /**
     * Unary minus
     *
     * @return -A
     */
    Matrix uminus();

    /**
     * C = A + B
     *
     * @param B another matrix
     * @return A + B
     */
    Matrix plus(Matrix B);

    /**
     * A = A + B
     *
     * @param B another matrix
     * @return A + B
     */
    Matrix plusEquals(Matrix B);

    /**
     * C = A - B
     *
     * @param B another matrix
     * @return A - B
     */
    Matrix minus(Matrix B);

    /**
     * A = A - B
     *
     * @param B another matrix
     * @return A - B
     */
    Matrix minusEquals(Matrix B);

    /**
     * Element-by-element multiplication, C = A.*B
     *
     * @param B another matrix
     * @return A.*B
     */
    Matrix arrayTimes(Matrix B);

    /**
     * Element-by-element multiplication in place, A = A.*B
     *
     * @param B another matrix
     * @return A.*B
     */
    Matrix arrayTimesEquals(Matrix B);

    /**
     * Element-by-element right division, C = A./B
     *
     * @param B another matrix
     * @return A./B
     */
    Matrix arrayRightDivide(Matrix B);

    /**
     * Element-by-element right division in place, A = A./B
     *
     * @param B another matrix
     * @return A./B
     */
    Matrix arrayRightDivideEquals(Matrix B);

    /**
     * Element-by-element left division, C = A.\B
     *
     * @param B another matrix
     * @return A.\B
     */
    Matrix arrayLeftDivide(Matrix B);

    /**
     * Element-by-element left division in place, A = A.\B
     *
     * @param B another matrix
     * @return A.\B
     */
    Matrix arrayLeftDivideEquals(Matrix B);

    /**
     * Multiply a matrix by a scalar, C = s*A
     *
     * @param s scalar
     * @return s*A
     */
    Matrix times(double s);

    /**
     * Multiply a matrix by a scalar in place, A = s*A
     *
     * @param s scalar
     * @return replace A by s*A
     */
    Matrix timesEquals(double s);

    /**
     * Linear algebraic matrix multiplication, A * B
     *
     * @param B another matrix
     * @return Matrix product, A * B
     * @exception IllegalArgumentException Matrix inner dimensions must agree.
     */
    Matrix times(Matrix B);

    /**
     * LU Decomposition
     *
     * @return LUDecomposition
     * @see LUDecomposition
     */
    LUDecomposition lu();

    /**
     * QR Decomposition
     *
     * @return QRDecomposition
     * @see QRDecomposition
     */
    QRDecomposition qr();

    /**
     * Cholesky Decomposition
     *
     * @return CholeskyDecomposition
     * @see CholeskyDecomposition
     */
    CholeskyDecomposition chol();

    /**
     * Singular Value Decomposition
     *
     * @return SingularValueDecompositionImpl
     * @see SingularValueDecompositionImpl
     */
    SingularValueDecomposition svd();

    /**
     * Eigenvalue Decomposition
     *
     * @return EigenvalueDecomposition
     * @see EigenvalueDecomposition
     */
    EigenvalueDecomposition eig();

    /**
     * Solve A*X = B
     *
     * @param B right hand side
     * @return solution if A is square, least squares solution otherwise
     */
    Matrix solve(Matrix B);

    /**
     * Solve X*A = B, which is also A'*X' = B'
     *
     * @param B right hand side
     * @return solution if A is square, least squares solution otherwise.
     */
    Matrix solveTranspose(Matrix B);

    /**
     * Matrix inverse or pseudoinverse
     *
     * @return inverse(A) if A is square, pseudoinverse otherwise.
     */
    Matrix inverse();

    /**
     * Matrix determinant
     *
     * @return determinant
     */
    double det();

    /**
     * Matrix rank
     *
     * @return effective numerical rank, obtained from SVD.
     */
    int rank();

    /**
     * Matrix condition (2 norm)
     *
     * @return ratio of largest to smallest singular value.
     */
    double cond();

    /**
     * Matrix trace.
     *
     * @return sum of the diagonal elements.
     */
    double trace();

    /**
     * Print the matrix to stdout. Line the elements up in columns with a
     * Fortran-like 'Fw.d' style format.
     *
     * @param w Column width.
     * @param d Number of digits after the decimal.
     */
    void print(int w, int d);

    /**
     * Human friendly matrix format (with line numbers)
     *
     * @param w
     * @param d
     */
    void printFancy(int w, int d);

    /**
     * Print upper matrix without numbers on diagonal
     *
     * @param w column width
     * @param d number of decimal digits
     */
    void printUpper(int w, int d);

    /**
     * Print lower matrix without numbers on diagonal
     *
     * @param w column width
     * @param d number of decimal digits
     */
    void printLower(int w, int d);

    /**
     * Print the matrix to the output stream. Line the elements up in columns
     * with a Fortran-like 'Fw.d' style format.
     *
     * @param output Output stream.
     * @param w Column width.
     * @param d Number of digits after the decimal.
     */
    void print(PrintWriter output, int w, int d);

    /**
     * Print the matrix to stdout. Line the elements up in columns. Use the
     * format object, and right justify within columns of width characters. Note
     * that is the matrix is to be read back in, you probably will want to use a
     * NumberFormat that is set to US Locale.
     *
     * @param format A Formatting object for individual elements.
     * @param width Field width for each column.
     * @see java.text.DecimalFormat#setDecimalFormatSymbols
     */
    void print(NumberFormat format, int width);

    // DecimalFormat is a little disappointing coming from Fortran or C's printf.
    // Since it doesn't pad on the left, the elements will come out different
    // widths.  Consequently, we'll pass the desired column width in as an
    // argument and do the extra padding ourselves.
    /**
     * Print the matrix to the output stream. Line the elements up in columns.
     * Use the format object, and right justify within columns of width
     * characters. Note that is the matrix is to be read back in, you probably
     * will want to use a NumberFormat that is set to US Locale.
     *
     * @param output the output stream.
     * @param format A formatting object to format the matrix elements
     * @param width Column width.
     * @see java.text.DecimalFormat#setDecimalFormatSymbols
     */
    void print(PrintWriter output, NumberFormat format, int width);
}
