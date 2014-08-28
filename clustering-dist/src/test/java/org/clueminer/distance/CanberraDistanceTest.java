package org.clueminer.distance;

import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.math.Vector;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class CanberraDistanceTest {

    private static CanberraDistance subject = new CanberraDistance();
    private static double delta = 1e-9;

    public CanberraDistanceTest() {
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
    public void testGetSimilarityFactor() {
    }

    @Test
    public void testGetNodeOffset() {
    }

    @Test
    public void testMeasure_Vector_Vector() {
        double dist = subject.measure(new DoubleArrayDataRow(new double[]{1, 2, 3}), new DoubleArrayDataRow(new double[]{2, 4, 6}));
        assertEquals(1.0, dist, delta);
    }

    @Test
    public void testMeasure_3args() {
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
    }

    @Test
    public void testIsIndiscernible() {
        Vector x = new DoubleArrayDataRow(new double[]{1, 2, 3});
        Vector y = new DoubleArrayDataRow(new double[]{2, 4, 6});
        double dist = subject.measure(x, y);
        assertEquals(dist, subject.measure(y, x), delta);
    }

}
