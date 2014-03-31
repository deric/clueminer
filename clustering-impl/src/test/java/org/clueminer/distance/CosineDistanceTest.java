package org.clueminer.distance;

import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.math.Vector;
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
public class CosineDistanceTest {

    private static CosineDistance subject;
    private static double delta = 1e-9;

    public CosineDistanceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        subject = new CosineDistance();
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

    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    @Test
    public void testColumns() {
    }

    @Test
    public void testRows() {
    }

    @Test
    public void testGetSimilarityFactor() {
    }

    @Test
    public void testGetNodeOffset() {
    }

    @Test
    public void testUseTreeHeight() {
    }

    @Test
    public void testMeasure_Vector_Vector() {
    }

    @Test
    public void testSimpleDistance() {
        double[] data = {1, 1, 1};
        double[] data2 = {0, 2, 4};
        Instance a = new DoubleArrayDataRow(data);
        Instance b = new DoubleArrayDataRow(data2);

        assertEquals(0, subject.measure(a, a), delta);
        assertTrue(subject.measure(a, b) > 0);
    }

    @Test
    public void testSimpleDistance2() {
        double[] x = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
        double[] y = {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1};
        Instance a = new DoubleArrayDataRow(x);
        Instance b = new DoubleArrayDataRow(y);

        assertEquals(0.6666666666666666, subject.measure(a, b), delta);
    }

    @Test
    public void testMeasure_3args() {
        Vector x = new DoubleArrayDataRow(new double[]{2, 1, 0, 2, 0, 1, 1, 1});
        Vector y = new DoubleArrayDataRow(new double[]{2, 1, 1, 1, 1, 0, 1, 1});

        double dist = subject.measure(x, y);
        //assertEquals(0.822, dist, delta);

        x = new DoubleArrayDataRow(new double[]{3, 2, 1, 2, 2});
        y = new DoubleArrayDataRow(new double[]{2, 1, 0, 1, 2});

        dist = subject.measure(x, y);
        //assertEquals(0.9439, dist, delta);
    }

    @Test
    public void testCompare() {
    }

    @Test
    public void testGetMinValue() {
    }

    @Test
    public void testGetMaxValue() {
    }

    @Test
    public void testIsSubadditive() {
        assertEquals(true, subject.isSubadditive());
    }

    @Test
    public void testIsIndiscernible() {
        assertEquals(true, subject.isIndiscernible());
    }

}
