package org.clueminer.distance;

import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.row.DoubleArrayDataRow;
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
public class MinkowskiDistanceTest {

    private static MinkowskiDistance test;
    private static Instance x, y;
    private static double delta = 1e-9;

    public MinkowskiDistanceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        test = new MinkowskiDistance(2);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        x = new DoubleArrayDataRow(new double[]{0, 0});
        y = new DoubleArrayDataRow(new double[]{3, 4});
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class MinkowskiDistance.
     */
    @Test
    public void testGetName() {
        assertEquals("Minkowski distance, power= 2.0", test.getName());
    }

    /**
     * Test of measure method, of class MinkowskiDistance.
     */
    @Test
    public void testMeasure_Instance_Instance() {
        assertEquals(5.0, test.measure(x, y), delta);
    }

    /**
     * Test of measure method, of class MinkowskiDistance.
     */
    @Test
    public void testMeasure_3args() {
        double[] weights = new double[]{1.0, 1.0};
        assertEquals(5.0, test.measure(x, y, weights), delta);
        weights = new double[]{0.0, 0.0};
        assertEquals(0.0, test.measure(x, y, weights), delta);
        weights = new double[]{0.5, 0.5};
        assertEquals(2.5, test.measure(x, y, weights), delta);
    }

    /**
     * Test of vector method, of class MinkowskiDistance.
     */
    @Test
    public void testVector() {
    }

    /**
     * Test of getSimilarityFactor method, of class MinkowskiDistance.
     */
    @Test
    public void testGetSimilarityFactor() {
    }

    /**
     * Test of getNodeOffset method, of class MinkowskiDistance.
     */
    @Test
    public void testGetNodeOffset() {
    }

    /**
     * Test of rows method, of class MinkowskiDistance.
     */
    @Test
    public void testRows() {
    }

    /**
     * Test of columns method, of class MinkowskiDistance.
     */
    @Test
    public void testColumns() {
    }
}