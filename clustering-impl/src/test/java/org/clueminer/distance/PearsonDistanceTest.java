package org.clueminer.distance;

import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.math.DoubleVector;
import org.clueminer.math.impl.DenseVector;
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
public class PearsonDistanceTest {

    private static PearsonDistance test;
    private DoubleVector u, w, v;
    private Instance ui, wi, vi;
    private static double delta = 1e-4;

    public PearsonDistanceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        test = new PearsonDistance();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class PearsonDistance.
     */
    @Test
    public void testGetName() {
    }

    /**
     * Test of columns method, of class PearsonDistance.
     */
    @Test
    public void testColumns() {
    }

    /**
     * Test of rows method, of class PearsonDistance.
     */
    @Test
    public void testRows() {
    }

    /**
     * Test of vector method, of class PearsonDistance.
     */
    @Test
    public void testVector() {
        u = new DenseVector(new double[]{1, 0, -1});
        v = new DenseVector(new double[]{1, 1, 0});
        w = new DenseVector(new double[]{0, 1, 1});


        double d = test.vector(u, w);
        assertEquals(1.8660, d, delta);
        //triangle inequality
        //System.out.println("two sides: "+ test.vector(u, v) + test.vector(v, w));
        //System.out.println("third side: "+ test.vector(u, w));
        boolean b = test.vector(u, v) + test.vector(v, w) > test.vector(u, w) ? true : false;
        assertEquals(false, b); // correlation does not satisfy the triangle inequality
        assertTrue(test.vector(u, w) + test.vector(w, v) > test.vector(u, v));
        assertEquals(1.6340, test.vector(u, v) + test.vector(v, w), delta);
    }

    /**
     * Test of getSimilarityFactor method, of class PearsonDistance.
     */
    @Test
    public void testGetSimilarityFactor() {
    }

    /**
     * Test of getNodeOffset method, of class PearsonDistance.
     */
    @Test
    public void testGetNodeOffset() {
    }

    /**
     * Test of measure method, of class PearsonDistance.
     */
    @Test
    public void testMeasure_Instance_Instance() {

        ui = new DoubleArrayDataRow(new double[]{1, 0, -1});
        vi = new DoubleArrayDataRow(new double[]{1, 1, 0});
        wi = new DoubleArrayDataRow(new double[]{0, 1, 1});

        double d = test.measure(ui, wi);
        assertEquals(1.8660, d, delta);
        //triangular inequality
        assertEquals(1.6340, test.measure(ui, vi) + test.measure(vi, wi), delta);
    }

    /**
     * Test of measure method, of class PearsonDistance.
     */
    @Test
    public void testMeasure_3args() {
        ui = new DoubleArrayDataRow(new double[]{1, 0, -1});
        vi = new DoubleArrayDataRow(new double[]{1, 1, 0});
        wi = new DoubleArrayDataRow(new double[]{0, 1, 1});


        double[] weights = new double[]{1., 1., 1.};
        double d = test.measure(ui, wi, weights);
        assertEquals(1.8660, d, delta);
        //triangular inequality
        assertEquals(1.6340, test.measure(ui, vi, weights) + test.measure(vi, wi, weights), delta);

        weights = new double[]{.5, .5, .5};
        d = test.measure(ui, wi, weights);
        assertEquals(1.6123, d, delta);
        //triangular inequality
        assertEquals(1.13762, test.measure(ui, vi, weights) + test.measure(vi, wi, weights), delta);
    }

    @Test
    public void testFullCorrelation1() {
        u = new DenseVector(new double[]{4., 2.});
        v = new DenseVector(new double[]{2., 1.});

        assertEquals(0.0, test.vector(v, u), delta);
    }

    @Test
    public void testFullCorrelation2() {
        u = new DenseVector(new double[]{3., -2.});
        v = new DenseVector(new double[]{3., -2.});

        assertEquals(0.0, test.vector(v, u), delta);
    }

    @Test
    public void testNoCorrelation() {
        u = new DenseVector(new double[]{3., -2.});
        v = new DenseVector(new double[]{-3., 2.});

        assertEquals(2.0, test.vector(u, v), delta);
    }

    @Test
    public void testPerfectCorrelation() {
        u = new DenseVector(new double[]{3., 3.});
        v = new DenseVector(new double[]{3., 3.});

        double correlation = test.vector(u, v);
        // Yeah, undefined in this case
        assertTrue(Double.isNaN(correlation));
    }

    @Test
    public void testNoCorrelation2() {
        u = new DenseVector(new double[]{Double.NaN, 1.0, Double.NaN});
        v = new DenseVector(new double[]{Double.NaN, Double.NaN, 1.0});

        assertEquals(Double.NaN, test.vector(u, v), delta);
    }

    /**
     * Test of compare method, of class PearsonDistance.
     */
    @Test
    public void testCompare() {
    }

    /**
     * Test of getMinValue method, of class PearsonDistance.
     */
    @Test
    public void testGetMinValue() {
    }

    /**
     * Test of getMaxValue method, of class PearsonDistance.
     */
    @Test
    public void testGetMaxValue() {
    }
}