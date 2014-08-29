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
public class EuclideanDistanceTest {

    private static EuclideanDistance subject;
    private static Instance x, y;
    private static double delta = 1e-9;

    public EuclideanDistanceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        subject = new EuclideanDistance();
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
     * Test of getName method, of class EuclideanDistance.
     */
    @Test
    public void testGetName() {
    }

    /**
     * Test of columns method, of class EuclideanDistance.
     */
    @Test
    public void testColumns() {
    }

    /**
     * Test of rows method, of class EuclideanDistance.
     */
    @Test
    public void testRows() {
    }

    /**
     * Test of vector method, of class EuclideanDistance.
     */
    @Test
    public void testVector() {
    }

    /**
     * Test of getSimilarityFactor method, of class EuclideanDistance.
     */
    @Test
    public void testGetSimilarityFactor() {
    }

    /**
     * Test of getNodeOffset method, of class EuclideanDistance.
     */
    @Test
    public void testGetNodeOffset() {
    }

    @Test
    public void testMeasureZeroDistance() {
        double dist = subject.measure(new DoubleArrayDataRow(new double[]{0, 0, 0}), new DoubleArrayDataRow(new double[]{0, 0, 0}));
        assertEquals(0.0, dist, delta);
    }

    /**
     * Test of measure method, of class EuclideanDistance.
     */
    @Test
    public void testMeasure_Instance_Instance() {
        assertEquals(5.0, subject.measure(x, y), delta);
    }

    /**
     * Test of measure method, of class EuclideanDistance.
     */
    @Test
    public void testMeasure_3args() {
        double[] weights = new double[]{1.0, 1.0};
        assertEquals(5.0, subject.measure(x, y, weights), delta);
        weights = new double[]{0.0, 0.0};
        assertEquals(0.0, subject.measure(x, y, weights), delta);
        weights = new double[]{0.5, 0.5};
        assertEquals(2.5, subject.measure(x, y, weights), delta);
    }
}
