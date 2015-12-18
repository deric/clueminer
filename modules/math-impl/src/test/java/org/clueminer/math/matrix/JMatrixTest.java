package org.clueminer.math.matrix;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.clueminer.math.CholeskyDecomposition;
import org.clueminer.math.EigenvalueDecomposition;
import org.clueminer.math.LUDecomposition;
import org.clueminer.math.Matrix;
import org.clueminer.math.QRDecomposition;
import org.clueminer.math.SingularValueDecomposition;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TestMatrix tests the functionality of the Jama Matrix class and associated
 * decompositions.
 * <P>
 * Run the test from the command line using <BLOCKQUOTE><PRE><CODE>
 * java Jama.test.TestMatrix
 * </CODE></PRE></BLOCKQUOTE>
 *
 *
 * Detailed output is provided indicating the functionality being tested and
 * whether the functionality is correctly implemented. Exception handling is
 * also tested.
 * <P>
 * The test is designed to run to completion and give a summary of any
 * implementation errors encountered. The final output should be:
 *
 *
 * <BLOCKQUOTE><PRE><CODE>
 * TestMatrix completed.
 * Total errors reported: n1
 * Total warning reported: n2
 * </CODE></PRE></BLOCKQUOTE>
 *
 *
 * If the test does not run to completion, this indicates that there is a
 * substantial problem within the implementation that was not anticipated in the
 * test design. The stopping point should give an indication of where the
 * problem exists.
 *
 */
public class JMatrixTest {

    private static final double DELTA = 1e-9;

    org.clueminer.math.Matrix A, B, C, Z, O, I, R, S, X, SUB, M, T, SQ, DEF, SOL;
    // Uncomment this to test IO in a different locale.
    // Locale.setDefault(Locale.GERMAN);
    int errorCount = 0;
    int warningCount = 0;
    double tmp, s;
    double[] columnwise = {1., 2., 3., 4., 5., 6., 7., 8., 9., 10., 11., 12.};
    double[] rowwise = {1., 4., 7., 10., 2., 5., 8., 11., 3., 6., 9., 12.};
    double[][] avals = {{1., 4., 7., 10.}, {2., 5., 8., 11.}, {3., 6., 9., 12.}};
    double[][] rankdef = avals;
    double[][] tvals = {{1., 2., 3.}, {4., 5., 6.}, {7., 8., 9.}, {10., 11., 12.}};
    double[][] subavals = {{5., 8., 11.}, {6., 9., 12.}};
    double[][] rvals = {{1., 4., 7.}, {2., 5., 8., 11.}, {3., 6., 9., 12.}};
    double[][] pvals = {{4., 1., 1.}, {1., 2., 3.}, {1., 3., 6.}};
    double[][] ivals = {{1., 0., 0., 0.}, {0., 1., 0., 0.}, {0., 0., 1., 0.}};
    double[][] evals = {{0., 1., 0., 0.}, {1., 0., 2.e-7, 0.}, {0., -2.e-7, 0., 1.}, {0., 0., 1., 0.}};
    double[][] square = {{166., 188., 210.}, {188., 214., 240.}, {210., 240., 270.}};
    double[][] sqSolution = {{13.}, {15.}};
    double[][] condmat = {{1., 3.}, {7., 9.}};
    int rows = 3, cols = 4;
    int invalidld = 5;
    /*
     * should trigger bad shape for construction with val
     */
    int raggedr = 0;
    /*
     * (raggedr,raggedc) should be out of bounds in ragged array
     */

    int raggedc = 4;
    int validld = 3;
    /*
     * leading dimension of intended test Matrices
     */

    int nonconformld = 4;
    /*
     * leading dimension which is valid, but nonconforming
     */

    int ib = 1, ie = 2, jb = 1, je = 3;
    /*
     * index ranges for sub Matrix
     */

    int[] rowindexset = {1, 2};
    int[] badrowindexset = {1, 3};
    int[] columnindexset = {1, 2, 3};
    int[] badcolumnindexset = {1, 2, 4};
    double columnsummax = 33.;
    double rowsummax = 30.;
    double sumofdiagonals = 15;
    double sumofsquares = 650;
    //epsilon
    private static double eps = Math.pow(2.0, -52.0);

    public JMatrixTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Should fill matrix with scalar number
     */
    @Test
    public void testConstructorFillWithScalar() {
        int m = 10;
        int n = 15;
        Matrix mat = new JMatrix(m, n, 5.0);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                assertEquals(5.0, mat.get(i, j), eps);
            }
        }
    }

    /**
     * Test of constructWithCopy method, of class JMatrix.
     */
    @Test
    public void testConstructWithCopy() {

        /**
         * Constructors and constructor-like methods: double[], int double[][]
         * int, int int, int, double int, int, double[][]
         * constructWithCopy(double[][]) random(int,int) identity(int)
         *
         */
        print("\nTesting constructors and constructor-like methods...\n");
        try {
            /**
             * check that exception is thrown in packed constructor with invalid
             * length *
             */
            A = new JMatrix(columnwise, invalidld);
            errorCount = try_failure(errorCount, "Catch invalid length in packed constructor... ",
                    "exception not thrown for invalid input");
        } catch (IllegalArgumentException e) {
            try_success("Catch invalid length in packed constructor... ",
                    e.getMessage());
        }
        try {
            /**
             * check that exception is thrown in default constructor if input
             * array is 'ragged' *
             */
            A = new JMatrix(rvals);
            tmp = A.get(raggedr, raggedc);
        } catch (IllegalArgumentException e) {
            try_success("Catch ragged input to default constructor... ",
                    e.getMessage());
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            errorCount = try_failure(errorCount, "Catch ragged input to constructor... ",
                    "exception not thrown in construction...ArrayIndexOutOfBoundsException thrown later");
        }
        try {
            /**
             * check that exception is thrown in constructWithCopy if input
             * array is 'ragged' *
             */
            A = JMatrix.constructWithCopy(rvals);
            tmp = A.get(raggedr, raggedc);
        } catch (IllegalArgumentException e) {
            try_success("Catch ragged input to constructWithCopy... ", e.getMessage());
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            errorCount = try_failure(errorCount, "Catch ragged input to constructWithCopy... ", "exception not thrown in construction...ArrayIndexOutOfBoundsException thrown later");
        }

        A = new JMatrix(columnwise, validld);
        B = new JMatrix(avals);
        tmp = B.get(0, 0);
        avals[0][0] = 0.0;
        C = B.minus(A);
        avals[0][0] = tmp;
        B = JMatrix.constructWithCopy(avals);
        tmp = B.get(0, 0);
        avals[0][0] = 0.0;
        if ((tmp - B.get(0, 0)) != 0.0) {
            /**
             * check that constructWithCopy behaves properly *
             */
            errorCount = try_failure(errorCount, "constructWithCopy... ", "copy not effected... data visible outside");
        } else {
            try_success("constructWithCopy... ", "");
        }
        avals[0][0] = columnwise[0];
        I = new JMatrix(ivals);
        try {
            check(I, MatrixHelper.identity(3, 4));
            try_success("identity... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "identity... ", "identity JMatrix not successfully created");
        }

        /**
         * Access Methods: columnsCount() rowsCount() getArray() getArrayCopy()
         * getColumnPackedCopy() getRowPackedCopy() get(int,int)
         * getMatrix(int,int,int,int) getMatrix(int,int,int[])
         * getMatrix(int[],int,int) getMatrix(int[],int[]) set(int,int,double)
         * setMatrix(int,int,int,int,Matrix) setMatrix(int,int,int[],Matrix)
         * setMatrix(int[],int,int,Matrix) setMatrix(int[],int[],Matrix)
         *
         */
        print("\nTesting access methods...\n");

        /**
         * Various get methods:
         *
         */
        B = new JMatrix(avals);
        double[][] barray = B.getArray();
        if (barray != avals) {
            errorCount = try_failure(errorCount, "getArray... ", "");
        } else {
            try_success("getArray... ", "");
        }
        barray = B.getArrayCopy();
        if (barray == avals) {
            errorCount = try_failure(errorCount, "getArrayCopy... ", "data not (deep) copied");
        }
        try {
            check(barray, avals);
            try_success("getArrayCopy... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "getArrayCopy... ", "data not successfully (deep) copied");
        }
        double[] bpacked = B.getColumnPackedCopy();
        try {
            check(bpacked, columnwise);
            try_success("getColumnPackedCopy... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "getColumnPackedCopy... ", "data not successfully (deep) copied by columns");
        }
        bpacked = B.getRowPackedCopy();
        try {
            check(bpacked, rowwise);
            try_success("getRowPackedCopy... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "getRowPackedCopy... ", "data not successfully (deep) copied by rows");
        }
        try {
            tmp = B.get(B.rowsCount(), B.columnsCount() - 1);
            errorCount = try_failure(errorCount, "get(int,int)... ", "OutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                tmp = B.get(B.rowsCount() - 1, B.columnsCount());
                errorCount = try_failure(errorCount, "get(int,int)... ", "OutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("get(int,int)... OutofBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "get(int,int)... ", "OutOfBoundsException expected but not thrown");
        }
        try {
            if (B.get(B.rowsCount() - 1, B.columnsCount() - 1)
                    != avals[B.rowsCount() - 1][B.columnsCount() - 1]) {
                errorCount = try_failure(errorCount, "get(int,int)... ", "JMatrix entry (i,j) not successfully retreived");
            } else {
                try_success("get(int,int)... ", "");
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            errorCount = try_failure(errorCount, "get(int,int)... ", "Unexpected ArrayIndexOutOfBoundsException");
        }
        SUB = new JMatrix(subavals);
        try {
            M = B.getMatrix(ib, ie + B.rowsCount() + 1, jb, je);
            errorCount = try_failure(errorCount, "getMatrix(int,int,int,int)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                M = B.getMatrix(ib, ie, jb, je + B.columnsCount() + 1);
                errorCount = try_failure(errorCount, "getMatrix(int,int,int,int)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("getMatrix(int,int,int,int)... ArrayIndexOutOfBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "getMatrix(int,int,int,int)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        }
        try {
            M = B.getMatrix(ib, ie, jb, je);
            try {
                check(SUB, M);
                try_success("getMatrix(int,int,int,int)... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "getMatrix(int,int,int,int)... ", "submatrix not successfully retreived");
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            errorCount = try_failure(errorCount, "getMatrix(int,int,int,int)... ", "Unexpected ArrayIndexOutOfBoundsException");
        }

        try {
            M = B.getMatrix(ib, ie, badcolumnindexset);
            errorCount = try_failure(errorCount, "getMatrix(int,int,int[])... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                M = B.getMatrix(ib, ie + B.rowsCount() + 1, columnindexset);
                errorCount = try_failure(errorCount, "getMatrix(int,int,int[])... ", "ArrayIndexOutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("getMatrix(int,int,int[])... ArrayIndexOutOfBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "getMatrix(int,int,int[])... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        }
        try {
            M = B.getMatrix(ib, ie, columnindexset);
            try {
                check(SUB, M);
                try_success("getMatrix(int,int,int[])... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "getMatrix(int,int,int[])... ", "submatrix not successfully retreived");
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            errorCount = try_failure(errorCount, "getMatrix(int,int,int[])... ", "Unexpected ArrayIndexOutOfBoundsException");
        }
        try {
            M = B.getMatrix(badrowindexset, jb, je);
            errorCount = try_failure(errorCount, "getMatrix(int[],int,int)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                M = B.getMatrix(rowindexset, jb, je + B.columnsCount() + 1);
                errorCount = try_failure(errorCount, "getMatrix(int[],int,int)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("getMatrix(int[],int,int)... ArrayIndexOutOfBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "getMatrix(int[],int,int)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        }
        try {
            M = B.getMatrix(rowindexset, jb, je);
            try {
                check(SUB, M);
                try_success("getMatrix(int[],int,int)... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "getMatrix(int[],int,int)... ", "submatrix not successfully retreived");
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            errorCount = try_failure(errorCount, "getMatrix(int[],int,int)... ", "Unexpected ArrayIndexOutOfBoundsException");
        }
        try {
            M = B.getMatrix(badrowindexset, columnindexset);
            errorCount = try_failure(errorCount, "getMatrix(int[],int[])... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                M = B.getMatrix(rowindexset, badcolumnindexset);
                errorCount = try_failure(errorCount, "getMatrix(int[],int[])... ", "ArrayIndexOutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("getMatrix(int[],int[])... ArrayIndexOutOfBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "getMatrix(int[],int[])... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        }
        try {
            M = B.getMatrix(rowindexset, columnindexset);
            try {
                check(SUB, M);
                try_success("getMatrix(int[],int[])... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "getMatrix(int[],int[])... ", "submatrix not successfully retreived");
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            errorCount = try_failure(errorCount, "getMatrix(int[],int[])... ", "Unexpected ArrayIndexOutOfBoundsException");
        }

        /**
         * Various set methods:
         *
         */
        try {
            B.set(B.rowsCount(), B.columnsCount() - 1, 0.);
            errorCount = try_failure(errorCount, "set(int,int,double)... ", "OutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                B.set(B.rowsCount() - 1, B.columnsCount(), 0.);
                errorCount = try_failure(errorCount, "set(int,int,double)... ", "OutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("set(int,int,double)... OutofBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "set(int,int,double)... ", "OutOfBoundsException expected but not thrown");
        }
        try {
            B.set(ib, jb, 0.);
            tmp = B.get(ib, jb);
            try {
                check(tmp, 0.);
                try_success("set(int,int,double)... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "set(int,int,double)... ", "Matrix element not successfully set");
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
            errorCount = try_failure(errorCount, "set(int,int,double)... ", "Unexpected ArrayIndexOutOfBoundsException");
        }
        M = new JMatrix(2, 3, 0.);
        try {
            B.setMatrix(ib, ie + B.rowsCount() + 1, jb, je, M);
            errorCount = try_failure(errorCount, "setMatrix(int,int,int,int,Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                B.setMatrix(ib, ie, jb, je + B.columnsCount() + 1, M);
                errorCount = try_failure(errorCount, "setMatrix(int,int,int,int,Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("setMatrix(int,int,int,int,Matrix)... ArrayIndexOutOfBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "setMatrix(int,int,int,int,Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        }
        try {
            B.setMatrix(ib, ie, jb, je, M);
            try {
                check(M.minus(B.getMatrix(ib, ie, jb, je)), M);
                try_success("setMatrix(int,int,int,int,Matrix)... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "setMatrix(int,int,int,int,Matrix)... ", "submatrix not successfully set");
            }
            B.setMatrix(ib, ie, jb, je, SUB);
        } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
            errorCount = try_failure(errorCount, "setMatrix(int,int,int,int,Matrix)... ", "Unexpected ArrayIndexOutOfBoundsException");
        }
        try {
            B.setMatrix(ib, ie + B.rowsCount() + 1, columnindexset, M);
            errorCount = try_failure(errorCount, "setMatrix(int,int,int[],Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                B.setMatrix(ib, ie, badcolumnindexset, M);
                errorCount = try_failure(errorCount, "setMatrix(int,int,int[],Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("setMatrix(int,int,int[],Matrix)... ArrayIndexOutOfBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "setMatrix(int,int,int[],Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        }
        try {
            B.setMatrix(ib, ie, columnindexset, M);
            try {
                check(M.minus(B.getMatrix(ib, ie, columnindexset)), M);
                try_success("setMatrix(int,int,int[],Matrix)... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "setMatrix(int,int,int[],Matrix)... ", "submatrix not successfully set");
            }
            B.setMatrix(ib, ie, jb, je, SUB);
        } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
            errorCount = try_failure(errorCount, "setMatrix(int,int,int[],Matrix)... ", "Unexpected ArrayIndexOutOfBoundsException");
        }
        try {
            B.setMatrix(rowindexset, jb, je + B.columnsCount() + 1, M);
            errorCount = try_failure(errorCount, "setMatrix(int[],int,int,Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                B.setMatrix(badrowindexset, jb, je, M);
                errorCount = try_failure(errorCount, "setMatrix(int[],int,int,Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("setMatrix(int[],int,int,Matrix)... ArrayIndexOutOfBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "setMatrix(int[],int,int,Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        }
        try {
            B.setMatrix(rowindexset, jb, je, M);
            try {
                check(M.minus(B.getMatrix(rowindexset, jb, je)), M);
                try_success("setMatrix(int[],int,int,Matrix)... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "setMatrix(int[],int,int,Matrix)... ", "submatrix not successfully set");
            }
            B.setMatrix(ib, ie, jb, je, SUB);
        } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
            errorCount = try_failure(errorCount, "setMatrix(int[],int,int,Matrix)... ", "Unexpected ArrayIndexOutOfBoundsException");
        }
        try {
            B.setMatrix(rowindexset, badcolumnindexset, M);
            errorCount = try_failure(errorCount, "setMatrix(int[],int[],Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                B.setMatrix(badrowindexset, columnindexset, M);
                errorCount = try_failure(errorCount, "setMatrix(int[],int[],Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("setMatrix(int[],int[],Matrix)... ArrayIndexOutOfBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "setMatrix(int[],int[],Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        }
        try {
            B.setMatrix(rowindexset, columnindexset, M);
            try {
                check(M.minus(B.getMatrix(rowindexset, columnindexset)), M);
                try_success("setMatrix(int[],int[],Matrix)... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "setMatrix(int[],int[],Matrix)... ", "submatrix not successfully set");
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
            errorCount = try_failure(errorCount, "setMatrix(int[],int[],Matrix)... ", "Unexpected ArrayIndexOutOfBoundsException");
        }

        /**
         * Array-like methods: minus minusEquals plus plusEquals arrayLeftDivide
         * arrayLeftDivideEquals arrayRightDivide arrayRightDivideEquals
         * arrayTimes arrayTimesEquals uminus
         *
         */
        print("\nTesting array-like methods...\n");
        S = new JMatrix(columnwise, nonconformld);
        R = MatrixHelper.random(A.rowsCount(), A.columnsCount());
        A = R;
        try {
            S = A.minus(S);
            errorCount = try_failure(errorCount, "minus conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("minus conformance check... ", "");
        }
        A = R.copy();
        A.minusEquals(R);
        Z = new JMatrix(A.rowsCount(), A.columnsCount());
        try {
            A.minusEquals(S);
            errorCount = try_failure(errorCount, "minusEquals conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("minusEquals conformance check... ", "");
        }
        if (A.minus(Z).norm1() != 0.) {
            errorCount = try_failure(errorCount, "minusEquals... ", "(difference of identical Matrices is nonzero,\nSubsequent use of minus should be suspect)");
        } else {
            try_success("minusEquals... ", "");
        }

        A = R.copy();
        B = MatrixHelper.random(A.rowsCount(), A.columnsCount());
        C = A.minus(B);
        try {
            S = A.plus(S);
            errorCount = try_failure(errorCount, "plus conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("plus conformance check... ", "");
        }
        try {
            check(C.plus(B), A);
            try_success("plus... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "plus... ", "(C = A - B, but C + B != A)");
        }
        C = A.minus(B);
        C.plusEquals(B);
        try {
            A.plusEquals(S);
            errorCount = try_failure(errorCount, "plusEquals conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("plusEquals conformance check... ", "");
        }
        try {
            check(C, A);
            try_success("plusEquals... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "plusEquals... ", "(C = A - B, but C = C + B != A)");
        }
        A = R.uminus();
        try {
            check(A.plus(R), Z);
            try_success("uminus... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "uminus... ", "(-A + A != zeros)");
        }
        A = R.copy();
        O = new JMatrix(A.rowsCount(), A.columnsCount(), 1.0);
        C = A.arrayLeftDivide(R);
        try {
            S = A.arrayLeftDivide(S);
            errorCount = try_failure(errorCount, "arrayLeftDivide conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("arrayLeftDivide conformance check... ", "");
        }
        try {
            check(C, O);
            try_success("arrayLeftDivide... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "arrayLeftDivide... ", "(M.\\M != ones)");
        }
        try {
            A.arrayLeftDivideEquals(S);
            errorCount = try_failure(errorCount, "arrayLeftDivideEquals conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("arrayLeftDivideEquals conformance check... ", "");
        }
        A.arrayLeftDivideEquals(R);
        try {
            check(A, O);
            try_success("arrayLeftDivideEquals... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "arrayLeftDivideEquals... ", "(M.\\M != ones)");
        }
        A = R.copy();
        try {
            A.arrayRightDivide(S);
            errorCount = try_failure(errorCount, "arrayRightDivide conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("arrayRightDivide conformance check... ", "");
        }
        C = A.arrayRightDivide(R);
        try {
            check(C, O);
            try_success("arrayRightDivide... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "arrayRightDivide... ", "(M./M != ones)");
        }
        try {
            A.arrayRightDivideEquals(S);
            errorCount = try_failure(errorCount, "arrayRightDivideEquals conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("arrayRightDivideEquals conformance check... ", "");
        }
        A.arrayRightDivideEquals(R);
        try {
            check(A, O);
            try_success("arrayRightDivideEquals... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "arrayRightDivideEquals... ", "(M./M != ones)");
        }
        A = R.copy();
        B = MatrixHelper.random(A.rowsCount(), A.columnsCount());
        try {
            S = A.arrayTimes(S);
            errorCount = try_failure(errorCount, "arrayTimes conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("arrayTimes conformance check... ", "");
        }
        C = A.arrayTimes(B);
        try {
            check(C.arrayRightDivideEquals(B), A);
            try_success("arrayTimes... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "arrayTimes... ", "(A = R, C = A.*B, but C./B != A)");
        }
        try {
            A.arrayTimesEquals(S);
            errorCount = try_failure(errorCount, "arrayTimesEquals conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("arrayTimesEquals conformance check... ", "");
        }
        A.arrayTimesEquals(B);
        try {
            check(A.arrayRightDivideEquals(B), R);
            try_success("arrayTimesEquals... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "arrayTimesEquals... ", "(A = R, A = A.*B, but A./B != R)");
        }

        /**
         * LA methods: transpose times cond rank det trace norm1 norm2 normF
         * normInf solve solveTranspose inverse chol eig lu qr svd
         *
         */
        print("\nTesting linear algebra methods...\n");
        A = new JMatrix(columnwise, 3);

        A.transpose();
        try {
            check(A.norm1(), columnsummax);
            try_success("norm1...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "norm1()...", "incorrect norm calculation");
        }
        try {
            check(A.normInf(), rowsummax);
            try_success("normInf()...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "normInf()...", "incorrect norm calculation");
        }
        try {
            check(A.normF(), Math.sqrt(sumofsquares));
            try_success("normF...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "normF()...", "incorrect norm calculation");
        }
        try {
            check(A.trace(), sumofdiagonals);
            try_success("trace()...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "trace()...", "incorrect trace calculation");
        }
        try {
            check(A.getMatrix(0, A.rowsCount() - 1, 0, A.rowsCount() - 1).det(), 0.);
            try_success("det()...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "det()...", "incorrect determinant calculation");
        }
        SQ = new JMatrix(square);
        try {
            check(A.times(A.transpose()), SQ);
            try_success("times(Matrix)...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "times(Matrix)...", "incorrect Matrix-Matrix product calculation");
        }
        try {
            check(A.times(0.), Z);
            try_success("times(double)...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "times(double)...", "incorrect Matrix-scalar product calculation");
        }

        print("\nTestMatrix completed.\n");
        print("Total errors reported: " + Integer.toString(errorCount) + "\n");
        print("Total warnings reported: " + Integer.toString(warningCount) + "\n");
    }

    /**
     * private utility routines *
     */
    /**
     * Check magnitude of difference of scalars. *
     */
    private static void check(double x, double y) {
        if (x == 0 & Math.abs(y) < 10 * eps) {
            return;
        }
        if (y == 0 & Math.abs(x) < 10 * eps) {
            return;
        }
        if (Math.abs(x - y) > 10 * eps * Math.max(Math.abs(x), Math.abs(y))) {
            throw new RuntimeException("The difference x-y is too large: x = " + Double.toString(x) + "  y = " + Double.toString(y));
        }
    }

    /**
     * Check norm of difference of "vectors". *
     */
    private static void check(double[] x, double[] y) {
        if (x.length == y.length) {
            for (int i = 0; i < x.length; i++) {
                check(x[i], y[i]);
            }
        } else {
            throw new RuntimeException("Attempt to compare vectors of different lengths");
        }
    }

    /**
     * Check norm of difference of arrays. *
     */
    private static void check(double[][] x, double[][] y) {
        JMatrix A = new JMatrix(x);
        JMatrix B = new JMatrix(y);
        check(A, B);
    }

    /**
     * Check norm of difference of Matrices. *
     */
    private static void check(Matrix X, Matrix Y) {
        if (X.norm1() == 0. & Y.norm1() < 10 * eps) {
            return;
        }
        if (Y.norm1() == 0. & X.norm1() < 10 * eps) {
            return;
        }
        if (X.minus(Y).norm1() > 1000 * eps * Math.max(X.norm1(), Y.norm1())) {
            throw new RuntimeException("The norm of (X-Y) is too large: " + Double.toString(X.minus(Y).norm1()));
        }
    }

    /**
     * Shorten spelling of print. *
     */
    private static void print(String s) {
        System.out.print(s);
    }

    /**
     * Print appropriate messages for successful outcome try *
     */
    private static void try_success(String s, String e) {
        print(">    " + s + "success\n");
        if (!"".equals(e)) {
            print(">      Message: " + e + "\n");
        }
    }

    /**
     * Print appropriate messages for unsuccessful outcome try *
     */
    private static int try_failure(int count, String s, String e) {
        print(">    " + s + "*** failure ***\n>      Message: " + e + "\n");
        return ++count;
    }

    /**
     * Print appropriate messages for unsuccessful outcome try *
     */
    private static int try_warning(int count, String s, String e) {
        print(">    " + s + "*** warning ***\n>      Message: " + e + "\n");
        return ++count;
    }

    /**
     * Print a row vector. *
     */
    private static void print(double[] x, int w, int d) {
        // Use format Fw.d for all elements.
        System.out.print("\n");
        new JMatrix(x, 1).print(w, d);
        print("\n");
    }

    /**
     * Test of copy method, of class JMatrix.
     */
    @Test
    public void testCopy() {
    }

    /**
     * Test of clone method, of class JMatrix.
     */
    @Test
    public void testClone() {
    }

    /**
     * Test of getArray method, of class JMatrix.
     */
    @Test
    public void testGetArray() {
    }

    /**
     * Test of getArrayCopy method, of class JMatrix.
     */
    @Test
    public void testGetArrayCopy() {
    }

    /**
     * Test of getColumnPackedCopy method, of class JMatrix.
     */
    @Test
    public void testGetColumnPackedCopy() {
    }

    /**
     * Test of getRowPackedCopy method, of class JMatrix.
     */
    @Test
    public void testGetRowPackedCopy() {
    }

    /**
     * Test of rowsCount method, of class JMatrix.
     */
    @Test
    public void testGetRowCount() {
        B = new JMatrix(avals);
        assertEquals(rows, B.rowsCount());
    }

    /**
     * Test of columnsCount method, of class JMatrix.
     */
    @Test
    public void testGetColumnsCount() {
        B = new JMatrix(avals);
        assertEquals(cols, B.columnsCount());
    }

    /**
     * Test of get method, of class JMatrix.
     */
    @Test
    public void testGet() {
    }

    /**
     * Test of getMatrix method, of class JMatrix.
     */
    @Test
    public void testGetMatrix_4args() {
    }

    /**
     * Test of getMatrix method, of class JMatrix.
     */
    @Test
    public void testGetMatrix_intArr_intArr() {
    }

    /**
     * Test of getMatrix method, of class JMatrix.
     */
    @Test
    public void testGetMatrix_3args_1() {
    }

    /**
     * Test of getMatrix method, of class JMatrix.
     */
    @Test
    public void testGetMatrix_3args_2() {
    }

    /**
     * Test of set method, of class JMatrix.
     */
    @Test
    public void testSet() {
    }

    /**
     * Test of setMatrix method, of class JMatrix.
     */
    @Test
    public void testSetMatrix_5args() {
    }

    /**
     * Test of setMatrix method, of class JMatrix.
     */
    @Test
    public void testSetMatrix_3args() {
    }

    /**
     * Test of setMatrix method, of class JMatrix.
     */
    @Test
    public void testSetMatrix_4args_1() {
    }

    /**
     * Test of setMatrix method, of class JMatrix.
     */
    @Test
    public void testSetMatrix_4args_2() {
    }

    /**
     * Test of transpose method, of class JMatrix.
     */
    @Test
    public void testTranspose() {
        A = new JMatrix(columnwise, 3);
        T = new JMatrix(tvals);
        T = A.transpose();
        check(A.transpose(), T);
        try_success("transpose...", "");
    }

    /**
     * Test of norm1 method, of class JMatrix.
     */
    @Test
    public void testNorm1() {
        A = new JMatrix(columnwise, validld);
        R = MatrixHelper.random(A.rowsCount(), A.columnsCount());
        A = R;

        //difference of identical Matrices should be zero
        assertEquals(A.minus(R).norm1(), 0.0, eps);
    }

    /**
     * Test of norm2 method, of class JMatrix.
     */
    @Test
    public void testNorm2() {
    }

    /**
     * Test of normInf method, of class JMatrix.
     */
    @Test
    public void testNormInf() {
    }

    /**
     * Test of normF method, of class JMatrix.
     */
    @Test
    public void testNormF() {
    }

    /**
     * Test of uminus method, of class JMatrix.
     */
    @Test
    public void testUminus() {
    }

    /**
     * Test of plus method, of class JMatrix.
     */
    @Test
    public void testPlus() {
    }

    /**
     * Test of plusEquals method, of class JMatrix.
     */
    @Test
    public void testPlusEquals() {
    }

    /**
     * Test of minus method, of class JMatrix.
     */
    @Test
    public void testMinus() {
    }

    /**
     * Test of minusEquals method, of class JMatrix.
     */
    @Test
    public void testMinusEquals() {
    }

    /**
     * Test of arrayTimes method, of class JMatrix.
     */
    @Test
    public void testArrayTimes() {
    }

    /**
     * Test of arrayTimesEquals method, of class JMatrix.
     */
    @Test
    public void testArrayTimesEquals() {
    }

    /**
     * Test of arrayRightDivide method, of class JMatrix.
     */
    @Test
    public void testArrayRightDivide() {
    }

    /**
     * Test of arrayRightDivideEquals method, of class JMatrix.
     */
    @Test
    public void testArrayRightDivideEquals() {
    }

    /**
     * Test of arrayLeftDivide method, of class JMatrix.
     */
    @Test
    public void testArrayLeftDivide() {
    }

    /**
     * Test of arrayLeftDivideEquals method, of class JMatrix.
     */
    @Test
    public void testArrayLeftDivideEquals() {
    }

    /**
     * Test of times method, of class JMatrix.
     */
    @Test
    public void testTimes_double() {
    }

    /**
     * Test of timesEquals method, of class JMatrix.
     */
    @Test
    public void testTimesEquals() {
    }

    /**
     * Test of times method, of class JMatrix.
     */
    @Test
    public void testTimes_Matrix() {
    }

    /**
     * LU Decomposition Test of lu method, of class JMatrix.
     */
    @Test
    public void testLu() {
        A = new JMatrix(columnwise, 4);
        int n = A.columnsCount();
        A = A.getMatrix(0, n - 1, 0, n - 1);
        A.set(0, 0, 0.);
        LUDecomposition LU = A.lu();
        check(A.getMatrix(LU.getPivot(), 0, n - 1), LU.getL().times(LU.getU()));
    }

    /**
     * QRDecomposition Test of qr method, of class JMatrix.
     */
    @Test
    public void testQr() {
        A = new JMatrix(columnwise, 4);
        QRDecomposition QR = A.qr();
        R = QR.getR();
        check(A, QR.getQ().times(R));
    }

    /**
     * Cholesky Decomposition Test of chol method, of class JMatrix.
     */
    @Test
    public void testChol() {
        A = new JMatrix(pvals);
        CholeskyDecomposition Chol = A.chol();
        Matrix L = Chol.getL();
        check(A, L.times(L.transpose()));
        X = Chol.solve(MatrixHelper.identity(3, 3));
        check(A.times(X), MatrixHelper.identity(3, 3));
    }

    /**
     * Test of svd method, of class JMatrix.
     */
    @Test
    public void testSvd() {
        A = new JMatrix(columnwise, 4);
        SingularValueDecomposition SVD = A.svd();
        check(A, SVD.getU().times(SVD.getS().times(SVD.getV().transpose())));
    }

    /**
     * Test of eig method, of class JMatrix.
     */
    @Test
    public void testEig() {
        A = new JMatrix(pvals);
        EigenvalueDecomposition Eig = A.eig();
        Matrix D = Eig.getD();
        Matrix V = Eig.getV();

        //EigenvalueDecomposition (symmetric)...
        check(A.times(V), V.times(D));
        A = new JMatrix(evals);
        Eig = A.eig();
        D = Eig.getD();
        V = Eig.getV();
        //EigenvalueDecomposition (nonsymmetric)...
        check(A.times(V), V.times(D));
    }

    /**
     * Test of solve method, of class JMatrix.
     */
    @Test
    public void testSolve() {
        SUB = new JMatrix(subavals);
        O = new JMatrix(SUB.rowsCount(), 1, 1.0);
        SOL = new JMatrix(sqSolution);
        SQ = SUB.getMatrix(0, SUB.rowsCount() - 1, 0, SUB.rowsCount() - 1);
        check(SQ.solve(SOL), O);
    }

    /**
     * Test of solveTranspose method, of class JMatrix.
     */
    @Test
    public void testSolveTranspose() {
    }

    /**
     * Test of inverse method, of class JMatrix.
     */
    @Test
    public void testInverse() {
        A = new JMatrix(columnwise, 4);
        int n = A.columnsCount();
        A = A.getMatrix(0, n - 1, 0, n - 1);
        A.set(0, 0, 0.);
        X = A.inverse();
        check(A.times(X), MatrixHelper.identity(3, 3));
    }

    /**
     * Test of det method, of class JMatrix.
     */
    @Test
    public void testDet() {
    }

    @Test
    public void testHas() {
        Matrix a = new JMatrix(2, 2);
        //check whether number could be stored in matrix on given indexes
        assertEquals(true, a.has(0, 0));
        assertEquals(false, a.has(-1, 0));
        assertEquals(false, a.has(2, 0));
        assertEquals(false, a.has(0, 2));
    }

    /**
     * Test of rank method, of class JMatrix.
     */
    @Test
    public void testRank() {
        DEF = new JMatrix(rankdef);
        check(DEF.rank(), Math.min(DEF.rowsCount(), DEF.columnsCount()) - 1);
    }

    /**
     * Test of cond method, of class JMatrix.
     */
    @Test
    public void testCond() {
        A = new JMatrix(columnwise, 4);
        SingularValueDecomposition SVD = A.svd();
        B = new JMatrix(condmat);
        SVD = B.svd();
        double[] singularvalues = SVD.getSingularValues();
        check(B.cond(), singularvalues[0] / singularvalues[Math.min(B.rowsCount(), B.columnsCount()) - 1]);
    }

    /**
     * Test of trace method, of class JMatrix.
     */
    @Test
    public void testTrace() {
    }

    /**
     * Test of random method, of class JMatrix.
     */
    @Test
    public void testRandom() {
    }

    /**
     * Test of identity method, of class JMatrix.
     */
    @Test
    public void testIdentity() {
    }

    /**
     * Test of print method, of class JMatrix.
     */
    @Test
    public void testPrint_int_int() {
        A = new JMatrix(columnwise, validld);
        R = MatrixHelper.random(A.rowsCount(), A.columnsCount());

        /**
         * I/O methods: read print serializable: writeObject readObject
         *
         */
        print("\nTesting I/O methods...\n");
        try {
            DecimalFormat fmt = new DecimalFormat("0.0000E00");
            fmt.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
            File tmpfile = File.createTempFile("JamaTestMatrix", ".out");
            PrintWriter FILE = new PrintWriter(new FileOutputStream(tmpfile));
            A.print(FILE, fmt, 10);
            FILE.close();
            R = MatrixHelper.read(new BufferedReader(new FileReader(tmpfile)));
            if (A.minus(R).norm1() < .001) {
                try_success("print()/read()...", "");
            } else {
                errorCount = try_failure(errorCount, "print()/read()...", "Matrix read from file does not match Matrix printed to file");
            }
        } catch (java.io.IOException ioe) {
            warningCount = try_warning(warningCount, "print()/read()...", "unexpected I/O error, unable to run print/read test;  check write permission in current directory and retry");
        } catch (Exception e) {
            try {
                e.printStackTrace(System.out);
                warningCount = try_warning(warningCount, "print()/read()...", "Formatting error... will try JDK1.1 reformulation...");
                DecimalFormat fmt = new DecimalFormat("0.0000");
                File tmpfile = File.createTempFile("JamaTestMatrix", ".out");
                PrintWriter FILE = new PrintWriter(new FileOutputStream(tmpfile));
                A.print(FILE, fmt, 10);
                FILE.close();
                R = MatrixHelper.read(new BufferedReader(new FileReader(tmpfile)));
                if (A.minus(R).norm1() < .001) {
                    try_success("print()/read()...", "");
                } else {
                    errorCount = try_failure(errorCount, "print()/read() (2nd attempt) ...", "Matrix read from file does not match Matrix printed to file");
                }
            } catch (java.io.IOException ioe) {
                warningCount = try_warning(warningCount, "print()/read()...", "unexpected I/O error, unable to run print/read test;  check write permission in current directory and retry");
            }
        }
    }

    /**
     * Test of print method, of class JMatrix.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testPrint_3args_2() throws IOException {
        A = new JMatrix(columnwise, validld);
        R = MatrixHelper.random(A.rowsCount(), A.columnsCount());
        File tmpfile = File.createTempFile("TMPMATRIX", ".out");
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            out.writeObject(R);
            ObjectInputStream sin = new ObjectInputStream(new FileInputStream(tmpfile));
            A = (Matrix) sin.readObject();

            try {
                check(A, R);
                try_success("writeObject(Matrix)/readObject(Matrix)...", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "writeObject(Matrix)/readObject(Matrix)...", "Matrix not serialized correctly");
            }
        } catch (java.io.IOException ioe) {
            warningCount = try_warning(warningCount, "writeObject()/readObject()...", "unexpected I/O error, unable to run serialization test;  check write permission in current directory and retry");
        } catch (Exception e) {
            errorCount = try_failure(errorCount, "writeObject(Matrix)/readObject(Matrix)...", "unexpected error in serialization test");
        }
    }

    @Test
    public void testSetDiagonal() {
        int n = 5;
        Matrix M = new JMatrix(n, n);
        M.setDiagonal(10);

        for (int i = 0; i < n; i++) {
            assertEquals(10.0, M.get(i, i), DELTA);
        }
    }

}
