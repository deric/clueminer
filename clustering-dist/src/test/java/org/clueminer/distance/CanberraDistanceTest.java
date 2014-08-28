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

    private static final CanberraDistance subject = new CanberraDistance();
    private static final double delta = 1e-9;

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

        Vector x = new DoubleArrayDataRow(new double[]{1, 5, 2, 3, 10});
        Vector y = new DoubleArrayDataRow(new double[]{4, 15, 20, 5, 5});
        dist = subject.measure(x, y);
        assertEquals(2.5015151515151515, dist, delta);

    }

    @Test
    public void testMeasureZeroDistance() {
        double dist = subject.measure(new DoubleArrayDataRow(new double[]{0, 0, 0}), new DoubleArrayDataRow(new double[]{0, 0, 0}));
        assertEquals(0.0, dist, delta);
    }

    @Test
    public void testMeasure_3args() {
    }

    @Test
    public void testCompare() {
        assertEquals(true, subject.compare(1, 5));
    }

    @Test
    public void testGetMinValue() {
        assertEquals(0.0, subject.getMinValue(), delta);
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
