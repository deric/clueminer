package org.clueminer.math.matrix;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import org.clueminer.math.CholeskyDecomposition;
import org.clueminer.math.LUDecomposition;
import org.clueminer.math.Matrix;
import org.clueminer.math.MatrixVector;
import org.clueminer.math.QRDecomposition;
import org.clueminer.math.SingularValueDecomposition;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractMatrix implements Matrix {

    private static final long serialVersionUID = 3676474060880566964L;

    /**
     * One norm
     *
     * @return maximum column sum.
     */
    @Override
    public double norm1() {
        double f = 0;
        for (int j = 0; j < columnsCount(); j++) {
            double s = 0;
            for (int i = 0; i < rowsCount(); i++) {
                s += Math.abs(get(i, j));
            }
            f = Math.max(f, s);
        }
        return f;
    }

    @Override
    public MatrixVector getRowVector(int row) {
        return new MatrixRowVector(this, row);
    }

    @Override
    public MatrixVector getColumnVector(int column) {
        return new MatrixColumnVector(this, column);
    }

    /**
     * Check if size(A) == size(B)
     *
     *
     * @param B
     */
    protected void checkMatrixDimensions(Matrix B) {
        if (B.rowsCount() != this.rowsCount() || B.columnsCount() != this.columnsCount()) {
            throw new IllegalArgumentException("Matrix dimensions must agree. ("
                    + this.rowsCount() + ", " + this.columnsCount() + ") vs. (" + B.rowsCount() + ", " + B.columnsCount() + ")");
        }
    }

    /**
     * Two norm
     *
     * @return maximum singular value.
     */
    @Override
    public double norm2() {
        return (new SingularValueDecompositionImpl(this).norm2());
    }

    /**
     * LU Decomposition
     *
     * @return LUDecompositionJama
     * @see LUDecompositionJama
     */
    @Override
    public LUDecomposition lu() {
        return new LUDecompositionJama(this);
    }

    /**
     * QR Decomposition
     *
     * @return QRDecompositionJama
     * @see QRDecompositionJama
     */
    @Override
    public QRDecomposition qr() {
        return new QRDecompositionJama(this);
    }

    /**
     * Cholesky Decomposition
     *
     * @return CholeskyDecompositionJama
     * @see CholeskyDecompositionJama
     */
    @Override
    public CholeskyDecomposition chol() {
        return new CholeskyDecompositionJama(this);
    }

    /**
     * Singular Value Decomposition
     *
     * @return SingularValueDecomposition
     * @see SingularValueDecomposition
     */
    @Override
    public SingularValueDecomposition svd() {
        return new SingularValueDecompositionImpl(this);
    }

    /**
     * Eigenvalue Decomposition
     *
     * @return EigenvalueDecompositionJama
     * @see EigenvalueDecompositionJama
     */
    @Override
    public EigenvalueDecompositionJama eig() {
        return new EigenvalueDecompositionJama(this);
    }

    /**
     * Solve A*X = B
     *
     * @param B right hand side
     * @return solution if A is square, least squares solution otherwise
     */
    @Override
    public Matrix solve(Matrix B) {
        return (rowsCount() == columnsCount() ? (new LUDecompositionJama(this)).solve(B)
                : (new QRDecompositionJama(this)).solve(B));
    }

    /**
     * Solve X*A = B, which is also A'*X' = B'
     *
     * @param B right hand side
     * @return solution if A is square, least squares solution otherwise.
     */
    @Override
    public Matrix solveTranspose(Matrix B) {
        return transpose().solve(B.transpose());
    }

    /**
     * JMatrix inverse or pseudoinverse
     *
     * @return inverse(A) if A is square, pseudoinverse otherwise.
     */
    @Override
    public Matrix inverse() {
        return solve(MatrixHelper.identity(rowsCount(), rowsCount()));
    }

    /**
     * JMatrix determinant
     *
     * @return determinant
     */
    @Override
    public double det() {
        return new LUDecompositionJama(this).det();
    }

    /**
     * JMatrix rank
     *
     * @return effective numerical rank, obtained from SVD.
     */
    @Override
    public int rank() {
        return new SingularValueDecompositionImpl(this).rank();
    }

    /**
     * JMatrix condition (2 norm)
     *
     * @return ratio of largest to smallest singular value.
     */
    @Override
    public double cond() {
        return new SingularValueDecompositionImpl(this).cond();
    }

    /**
     * Print the matrix to stdout. Line the elements up in columns with a
     * Fortran-like 'Fw.d' style format.
     *
     * @param w Column width.
     * @param d Number of digits after the decimal.
     */
    @Override
    public void print(int w, int d) {
        print(new PrintWriter(System.out, true), w, d);
    }

    /**
     * Print the matrix to the output stream. Line the elements up in columns
     * with a Fortran-like 'Fw.d' style format.
     *
     * @param output Output stream.
     * @param w      Column width.
     * @param d      Number of digits after the decimal.
     */
    @Override
    public void print(PrintWriter output, int w, int d) {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        print(output, format, w + 2);
    }

    /**
     * Print the matrix to stdout. Line the elements up in columns. Use the
     * format object, and right justify within columns of width characters. Note
     * that is the matrix is to be read back in, you probably will want to use a
     * NumberFormat that is set to US Locale.
     *
     * @param format A Formatting object for individual elements.
     * @param width  Field width for each column.
     * @see java.text.DecimalFormat#setDecimalFormatSymbols
     */
    @Override
    public void print(NumberFormat format, int width) {
        print(new PrintWriter(System.out, true), format, width);
    }

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
     * @param width  Column width.
     * @see java.text.DecimalFormat#setDecimalFormatSymbols
     */
    @Override
    public void print(PrintWriter output, NumberFormat format, int width) {
        String s;
        int padding;
        output.println();  // start on new line.
        for (int i = 0; i < rowsCount(); i++) {
            for (int j = 0; j < columnsCount(); j++) {
                s = format.format(get(i, j)); // format the number
                padding = Math.max(1, width - s.length()); // At _least_ 1 space
                for (int k = 0; k < padding; k++) {
                    output.print(' ');
                }
                output.print(s);
            }
            output.println();
        }
        output.println();   // end with blank line.
    }

    @Override
    public void printFancy(int w, int d) {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        printFancy(new PrintWriter(System.out, true), format, w + 2);
    }

    public void printFancy(PrintWriter output, NumberFormat format, int width) {
        String s;
        int padding;
        output.println();  // start on new line.
        for (int i = 0; i < rowsCount(); i++) {
            //print row label
            s = String.valueOf(i);
            padding = Math.max(1, width - s.length() - 1);
            for (int k = 0; k < padding; k++) {
                output.print(' ');
            }
            output.print(s);
            output.print(" |");
            for (int j = 0; j < columnsCount(); j++) {
                s = format.format(get(i, j)); // format the number
                padding = Math.max(1, width - s.length()); // At _least_ 1 space
                for (int k = 0; k < padding; k++) {
                    output.print(' ');
                }
                output.print(s);
            }
            output.println();
        }
        //footer
        for (int i = 0; i < width * (columnsCount() + 1); i++) {
            output.print('-');
        }
        output.println();
        for (int k = 0; k < width; k++) {
            output.print(' ');
        }
        for (int i = 0; i < rowsCount(); i++) {
            s = String.valueOf(i); // format the number
            padding = Math.max(1, width - s.length()); // At _least_ 1 space
            for (int k = 0; k < padding; k++) {
                output.print(' ');
            }
            output.print(s);
        }
        output.println();
    }

    @Override
    public void printUpper(int w, int d) {
        printUL(new PrintWriter(System.out, true), w, d, true);
    }

    @Override
    public void printLower(int w, int d) {
        printUL(new PrintWriter(System.out, true), w, d, false);
    }

    public void printUL(PrintWriter output, int w, int d, boolean upper) {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        if (upper) {
            printUpper(output, format, w + 2);
        } else {
            printLower(output, format, w + 2);
        }
    }

    public void printUpper(PrintWriter output, NumberFormat format, int width) {
        output.println();  // start on new line.
        //header
        String s;
        int padding;
        for (int i = 0; i < rowsCount(); i++) {
            s = String.valueOf(i); // format the number
            padding = Math.max(1, width - s.length()); // At _least_ 1 space
            for (int k = 0; k < padding; k++) {
                output.print(' ');
            }
            output.print(s);
        }
        output.println();
        for (int i = 0; i < width * columnsCount(); i++) {
            output.print('-');
        }
        output.println();
        for (int i = 0; i < rowsCount(); i++) {
            // diagonal should be zero
            //fill space before actual matrix
            for (int k = 0; k < (width * i); k++) {
                output.print(' ');
            }
            for (int j = i; j < columnsCount(); j++) {
                s = format.format(get(i, j)); // format the number
                padding = Math.max(1, width - s.length()); // At _least_ 1 space
                for (int k = 0; k < padding; k++) {
                    output.print(' ');
                }
                output.print(s);
            }
            //print row label
            s = String.valueOf(i);
            //padding = Math.max(1, width - s.length() - 1);
            output.print(" |");
            /*    for (int k = 0; k < padding; k++) {                output.print(' ');
             }*/
            output.print(s);
            output.println();
        }
        output.println();   // end with blank line.
    }

    public void printLower(PrintWriter output, NumberFormat format, int width) {
        String s;
        int padding;
        output.println();  // start on new line.
        for (int i = 0; i < rowsCount(); i++) {
            //print row label
            s = String.valueOf(i);
            padding = Math.max(1, width - s.length() - 1);
            for (int k = 0; k < padding; k++) {
                output.print(' ');
            }
            output.print(s);
            output.print(" |");
            // diagonal should be zero
            for (int j = 0; j <= i; j++) {
                s = format.format(get(i, j)); // format the number
                padding = Math.max(1, width - s.length()); // At _least_ 1 space
                for (int k = 0; k < padding; k++) {
                    output.print(' ');
                }
                output.print(s);
            }
            output.println();
        }
        //footer
        for (int i = 0; i < width * (columnsCount() + 1); i++) {
            output.print('-');
        }
        output.println();
        for (int k = 0; k < width; k++) {
            output.print(' ');
        }
        for (int i = 0; i < rowsCount(); i++) {
            s = String.valueOf(i); // format the number
            padding = Math.max(1, width - s.length()); // At _least_ 1 space
            for (int k = 0; k < padding; k++) {
                output.print(' ');
            }
            output.print(s);
        }
        output.println();
    }

    /**
     * sqrt(a^2 + b^2) without under/overflow.
     */
    public static double hypot(double a, double b) {
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
        return r;
    }
}
