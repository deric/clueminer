package org.clueminer.math.matrix;

import org.clueminer.math.Matrix;
import org.clueminer.math.MatrixVector;
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
public class MatrixColumnVectorTest {

    private final double[][] array = {
        {1, 2, 3, 4, 5},
        {0, 1, 2, 3, 4},
        {0, 2, 4, 6, 8},
        {0, 3, 6, 9, 12},
        {0, 4, 8, 12, 16},
        {0, 6, 10, 14, 18}
    };
    private Matrix matrix;
    private MatrixVector vector;
    private static final double eps = Math.pow(2.0, -52.0);

    public MatrixColumnVectorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        matrix = new JamaMatrix(array);
        vector = matrix.getColumnVector(4);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of add method, of class MatrixColumnVector.
     */
    @Test
    public void testAdd_int_double() {
    }

    /**
     * Test of get method, of class MatrixColumnVector.
     */
    @Test
    public void testGet() {
        //should get last column of matrix
        assertEquals(5.0, vector.get(0), eps);
        assertEquals(4.0, vector.get(1), eps);
        assertEquals(8.0, vector.get(2), eps);
        assertEquals(12.0, vector.get(3), eps);
        assertEquals(16.0, vector.get(4), eps);
    }

    /**
     * Test of getDouble method, of class MatrixColumnVector.
     */
    @Test
    public void testGetValue() {
    }

    /**
     * Test of set method, of class MatrixColumnVector.
     */
    @Test
    public void testSet_int_double() {
        vector.set(0, 15);
        assertEquals(15, vector.getValue(0), eps);
        //change should be written to matrix
        assertEquals(15, matrix.get(0, 4), eps);
    }

    /**
     * Test of toArray method, of class MatrixColumnVector.
     */
    @Test
    public void testToArray() {
        double expected[] = {5, 4, 8, 12, 16};
        double[] copy = vector.toArray();
        //should be deep copy, changing vector now shouldn't affect it's copy
        vector.set(0, 99);
        for (int i = 0; i < expected.length; i++) {
            double d = expected[i];
            assertEquals(expected[i], copy[i], eps);
        }

    }

    /**
     * Test of size method, of class MatrixColumnVector.
     */
    @Test
    public void testSize() {
        assertEquals(6, vector.size());
        assertEquals(6, matrix.getColumnVector(0).size());
        assertEquals(5, matrix.getRowVector(0).size());
    }

    /**
     * Test of magnitude method, of class MatrixColumnVector.
     */
    @Test
    public void testMagnitude() {
    }

    /**
     * Test of set method, of class MatrixColumnVector.
     */
    @Test
    public void testSet_int_Number() {
    }

    /**
     * Test of add method, of class MatrixColumnVector.
     */
    @Test
    public void testAdd_Vector() {
    }

    /**
     * Test of getMatrix method, of class MatrixRowVector.
     */
    @Test
    public void testGetMatrix() {
        assertEquals(matrix.hashCode(), vector.getMatrix().hashCode());
    }
}
