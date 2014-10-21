package org.clueminer.math.matrix;

import org.clueminer.math.Matrix;
import org.clueminer.utils.Dump;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class SymmetricMatrixTest {

    private SymmetricMatrix A, B, C;
    private static double eps = Math.pow(2.0, -52.0);

    private org.clueminer.math.Matrix Z, O, I, R, S, X, SUB, M, T, SQ, DEF, SOL;

    private double tmp, s;
    private double[] columnwise = {1., 2., 3., 4., 5., 6., 7., 8., 9., 10., 11., 12.};
    private double[] rowwise = {1., 4., 7., 10., 2., 5., 8., 11., 3., 6., 9., 12.};
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
    int raggedr = 0; /*
     * (raggedr,raggedc) should be out of bounds in ragged array
     */

    int raggedc = 4;
    int validld = 3; /*
     * leading dimension of intended test Matrices
     */

    int nonconformld = 4; /*
     * leading dimension which is valid, but nonconforming
     */

    int ib = 1, ie = 2, jb = 1, je = 3; /*
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

    public SymmetricMatrixTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        A = new SymmetricMatrix(3, 3);
        B = new SymmetricMatrix(5, 5);
        C = new SymmetricMatrix(10, 10);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getArray method, of class SymmetricMatrix.
     */
    @Test
    public void testGetArray() {
        double[][] m = {{0, 2, 3}, {2, 0, 4}, {3, 4, 0}};
        A = new SymmetricMatrix(3, 3);
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < i; j++) {
                A.set(i, j, m[i][j]);
            }
        }
        A.printLower(2, 1);
        double[][] copy = A.getArray();
        Dump.matrix(copy, "copy", 3);

        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                assertEquals(m[i][j], copy[i][j], eps);
            }

        }
    }

    /**
     * Test of getArrayCopy method, of class SymmetricMatrix.
     */
    @Test
    public void testGetArrayCopy() {
    }

    /**
     * Test of rowsCount method, of class SymmetricMatrix.
     */
    @Test
    public void testRowsCount() {
        assertEquals(3, A.rowsCount());
        assertEquals(5, B.rowsCount());
        assertEquals(10, C.rowsCount());
    }

    /**
     * Test of columnsCount method, of class SymmetricMatrix.
     */
    @Test
    public void testColumnsCount() {
        assertEquals(3, A.columnsCount());
        assertEquals(5, B.columnsCount());
        assertEquals(10, C.columnsCount());
    }

    /**
     * Test of getColumnPackedCopy method, of class SymmetricMatrix.
     */
    @Test
    public void testGetColumnPackedCopy() {
    }

    /**
     * Test of getRowPackedCopy method, of class SymmetricMatrix.
     */
    @Test
    public void testGetRowPackedCopy() {
    }

    /**
     * Test of transpose method, of class SymmetricMatrix.
     */
    @Test
    public void testTranspose() {
    }

    /**
     * Test of norm1 method, of class JMatrix.
     */
    @Test
    public void testNorm1() {
        R = SymmetricMatrix.random(5);
        A = (SymmetricMatrix) R;

        //difference of identical Matrices should be zero
        //assertEquals(A.minus(R).norm1(), 0.0, eps);
    }

    /**
     * Test of get method, of class SymmetricMatrix.
     */
    @Test
    public void testGet() {
    }

    /**
     * Test of getMatrix method, of class SymmetricMatrix.
     */
    @Test
    public void testGetMatrix_4args() {
    }

    /**
     * Test of getMatrix method, of class SymmetricMatrix.
     */
    @Test
    public void testGetMatrix_intArr_intArr() {
    }

    /**
     * Test of getMatrix method, of class SymmetricMatrix.
     */
    @Test
    public void testGetMatrix_3args_1() {
    }

    /**
     * Test of getMatrix method, of class SymmetricMatrix.
     */
    @Test
    public void testGetMatrix_3args_2() {
    }

    /**
     * Test of set method, of class SymmetricMatrix.
     */
    @Test
    public void testSet() {
        int k = 0;
        for (int i = 0; i < A.rowsCount(); i++) {
            for (int j = 0; j < i; j++) {
                A.set(i, j, k);
                assertEquals(A.get(i, j), k, eps);
                k++;
            }
        }
        A.print(3, 0);
    }

    /**
     * Test of setMatrix method, of class SymmetricMatrix.
     */
    @Test
    public void testSetMatrix_5args() {
    }

    /**
     * Test of setMatrix method, of class SymmetricMatrix.
     */
    @Test
    public void testSetMatrix_3args() {
    }

    /**
     * Test of setMatrix method, of class SymmetricMatrix.
     */
    @Test
    public void testSetMatrix_4args_1() {
    }

    /**
     * Test of setMatrix method, of class SymmetricMatrix.
     */
    @Test
    public void testSetMatrix_4args_2() {
    }

    /**
     * Test of normInf method, of class SymmetricMatrix.
     */
    @Test
    public void testNormInf() {
    }

    /**
     * Test of normF method, of class SymmetricMatrix.
     */
    @Test
    public void testNormF() {
    }

    /**
     * Test of uminus method, of class SymmetricMatrix.
     */
    @Test
    public void testUminus() {
    }

    /**
     * Test of plus method, of class SymmetricMatrix.
     */
    @Test
    public void testPlus() {
    }

    /**
     * Test of plusEquals method, of class SymmetricMatrix.
     */
    @Test
    public void testPlusEquals() {
    }

    /**
     * Test of minus method, of class SymmetricMatrix.
     */
    @Test
    public void testMinus() {
    }

    /**
     * Test of minusEquals method, of class SymmetricMatrix.
     */
    @Test
    public void testMinusEquals() {
    }

    /**
     * Test of arrayTimes method, of class SymmetricMatrix.
     */
    @Test
    public void testArrayTimes() {
    }

    /**
     * Test of arrayTimesEquals method, of class SymmetricMatrix.
     */
    @Test
    public void testArrayTimesEquals() {
    }

    /**
     * Test of arrayRightDivide method, of class SymmetricMatrix.
     */
    @Test
    public void testArrayRightDivide() {
    }

    /**
     * Test of arrayRightDivideEquals method, of class SymmetricMatrix.
     */
    @Test
    public void testArrayRightDivideEquals() {
    }

    /**
     * Test of arrayLeftDivide method, of class SymmetricMatrix.
     */
    @Test
    public void testArrayLeftDivide() {
    }

    /**
     * Test of arrayLeftDivideEquals method, of class SymmetricMatrix.
     */
    @Test
    public void testArrayLeftDivideEquals() {
    }

    /**
     * Test of times method, of class SymmetricMatrix.
     */
    @Test
    public void testTimes_double() {
    }

    /**
     * Test of timesEquals method, of class SymmetricMatrix.
     */
    @Test
    public void testTimesEquals() {
    }

    /**
     * Test of times method, of class SymmetricMatrix.
     */
    @Test
    public void testTimes_Matrix() {
    }

    /**
     * Test of trace method, of class SymmetricMatrix.
     */
    @Test
    public void testTrace() {
    }

    @Test
    public void testHas() {
        Matrix m = new SymmetricMatrix(2, 2);
        //check whether number could be stored in matrix on given indexes
        assertEquals(true, m.has(0, 0));
        assertEquals(false, m.has(-1, 0));
        assertEquals(false, m.has(2, 0));
        assertEquals(false, m.has(0, 2));
        assertEquals(false, m.has(1, 6));
    }
}
