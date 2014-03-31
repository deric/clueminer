package org.clueminer.distance;

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
public class ManhattanDistanceTest {

    private static ManhattanDistance subject = new ManhattanDistance();
    private static double delta = 1e-9;

    public ManhattanDistanceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
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
    }

    @Test
    public void testColumns() {
    }

    @Test
    public void testRows() {
    }

    @Test
    public void testMeasure_Vector_Vector() {
        Vector x = new DoubleArrayDataRow(new double[]{0, 0});
        Vector y = new DoubleArrayDataRow(new double[]{1.0, 1.0});
        assertEquals(2.0, subject.measure(x, y), delta);
    }

    @Test
    public void testMeasure_3args() {
    }

    @Test
    public void testGetSimilarityFactor() {
    }

    @Test
    public void testGetNodeOffset() {
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
