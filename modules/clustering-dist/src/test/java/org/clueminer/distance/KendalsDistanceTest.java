package org.clueminer.distance;

import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.math.Vector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class KendalsDistanceTest {

    private static final KendalsDistance subject = new KendalsDistance();
    private static final double delta = 1e-9;

    public KendalsDistanceTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testMeasure_Vector_Vector() {
        double dist = subject.measure(new DoubleArrayDataRow(new double[]{1, 2, 3}), new DoubleArrayDataRow(new double[]{2, 4, 6}));
        assertEquals(1.0, dist, delta);

        Vector x = new DoubleArrayDataRow(new double[]{1, 5, 2, 3, 10});
        Vector y = new DoubleArrayDataRow(new double[]{4, 15, 20, 5, 5});
        dist = subject.measure(x, y);
        assertEquals(0.10540925533894598, dist, delta);
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
    public void testUseTreeHeight() {
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
    public void testIsSymmetric() {
        Vector x = new DoubleArrayDataRow(new double[]{1, 2, 3});
        Vector y = new DoubleArrayDataRow(new double[]{2, 4, 6});
        double xy = subject.measure(x, y);
        double yx = subject.measure(y, x);
        assertEquals(subject.isSymmetric(), xy == yx);
    }

    @Test
    public void testIsSubadditive() {
        Vector x = new DoubleArrayDataRow(new double[]{1, 2, 3});
        Vector y = new DoubleArrayDataRow(new double[]{2, 4, 6});
        Vector z = new DoubleArrayDataRow(new double[]{3, 6, 9});

        double xy = subject.measure(x, y);
        double yz = subject.measure(y, z);
        double xz = subject.measure(x, z);

        assertEquals(subject.isSubadditive(), xz <= (xy + yz));
    }

    @Test
    public void testIsIndiscernible() {
        Vector x = new DoubleArrayDataRow(new double[]{1, 2, 3});
        Vector y = new DoubleArrayDataRow(new double[]{1, 2, 3});
        assertEquals(subject.isIndiscernible(), 0.0 == subject.measure(x, y));
    }

    @Test
    public void testMeasureZeroDistance() {
        double dist = subject.measure(new DoubleArrayDataRow(new double[]{0, 0, 0}), new DoubleArrayDataRow(new double[]{0, 0, 0}));
        assertEquals(0.0, dist, delta);
    }

}
