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
public class MatrixRowVectorTest {

    private double[][] array = {{1, 2, 3, 4, 5},
        {0, 1, 2, 3, 4},
        {0, 2, 4, 6, 8},
        {0, 3, 6, 9, 12},
        {0, 4, 8, 12, 16}};
    private Matrix matrix;
    private MatrixVector vector;
    private static double eps = Math.pow(2.0, -52.0);

    public MatrixRowVectorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        matrix = new JMatrix(array);
        vector = matrix.getRowVector(0);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of add method, of class MatrixRowVector.
     */
    @Test
    public void testAdd_int_double() {
    }

    /**
     * Test of get method, of class MatrixRowVector.
     */
    @Test
    public void testGet() {
        
        //Dump.matrix(array, "array: ", 3);
        assertEquals(1.0, vector.get(0), eps);
        assertEquals(2.0, vector.get(1),  eps);
        assertEquals(3.0,vector.get(2),  eps);
        assertEquals(4.0,vector.get(3),  eps);
        assertEquals( 5.0, vector.get(4), eps);
        assertEquals(16.0, matrix.getRowVector(4).get(4),  eps);
    }

    /**
     * Test of getDouble method, of class MatrixRowVector.
     */
    @Test
    public void testGetValue() {
        //Dump.matrix(array, "array: ", 3);
        assertEquals(1.0, vector.getValue(0), eps);
        assertEquals(2.0, vector.getValue(1),  eps);
        assertEquals(3.0,vector.getValue(2),  eps);
        assertEquals(4.0,vector.getValue(3),  eps);
        assertEquals( 5.0, vector.getValue(4), eps);
        assertEquals(16.0, matrix.getRowVector(4).getValue(4),  eps);
    }

    /**
     * Test of set method, of class MatrixRowVector.
     */
    @Test
    public void testSet_int_double() {
        vector.set(0, 15);
        assertEquals(15, vector.getValue(0), eps);
        //change should be written to matrix
        assertEquals(15, matrix.get(0, 0), eps);
    }

    /**
     * Test of toArray method, of class MatrixRowVector.
     */
    @Test
    public void testToArray() {
        vector = matrix.getRowVector(0);
        double[] mat = vector.toArray();
        for (int i = 0; i < vector.size(); i++) {
            assertEquals(array[0][i], mat[i], eps);
        }
    }

    /**
     * Test of size method, of class MatrixRowVector.
     */
    @Test
    public void testSize() {
        assertEquals(5, matrix.getRowVector(2).size());
    }

    /**
     * Test of magnitude method, of class MatrixRowVector.
     */
    @Test
    public void testMagnitude() {
    }

    /**
     * Test of set method, of class MatrixRowVector.
     */
    @Test
    public void testSet_int_Number() {
    }

    /**
     * Test of add method, of class MatrixRowVector.
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
