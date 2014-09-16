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
public class CorrelationDistanceTest {

    private static final CorrelationDistance subject = new CorrelationDistance();
    private static final double delta = 1e-9;

    public CorrelationDistanceTest() {
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
    public void testGetSimilarityFactor() {
    }

    @Test
    public void testGetNodeOffset() {
    }

    @Test
    public void testRows() {
    }

    @Test
    public void testColumns() {
    }

    @Test
    public void testMeasure_Vector_Vector() {
        double dist = subject.measure(new DoubleArrayDataRow(new double[]{1, 2, 3}), new DoubleArrayDataRow(new double[]{3, 5, 10}));
        assertEquals(1 - (7 / (2 * Math.sqrt(13))), dist, delta);
    }

    @Test
    public void testMeasure_Vector_Vector2() {
        double dist;
        Vector x = new DoubleArrayDataRow(new double[]{1, 5, 2, 3, 10});
        Vector y = new DoubleArrayDataRow(new double[]{4, 15, 20, 5, 5});
        dist = subject.measure(x, y);
        /**
         * http://www.wolframalpha.com/input/?i=CorrelationDistance%5B%7B1%2C+5%2C+2%2C+3%2C+10%7D%2C+%7B4%2C+15%2C+20%2C+5%2C+5%7D%5D
         */
        assertEquals(1.21066351883988, dist, delta);
    }

    @Test
    public void testMeasure_3args() {
    }

    @Test
    public void testMeasureZeroDistance() {
        double dist = subject.measure(new DoubleArrayDataRow(new double[]{0, 0, 0}), new DoubleArrayDataRow(new double[]{0, 0, 0}));
        assertEquals(0.0, dist, delta);
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
    public void testIsSymmetric() {
        Vector x = new DoubleArrayDataRow(new double[]{1, 2, 3});
        Vector y = new DoubleArrayDataRow(new double[]{2, 4, 6});
        assertEquals(subject.isSymmetric(), subject.measure(x, y) == subject.measure(y, x));
    }

    @Test
    public void testIsIndiscernible() {
        Vector x = new DoubleArrayDataRow(new double[]{1, 2, 3});
        Vector y = new DoubleArrayDataRow(new double[]{1, 2, 3});

        assertEquals(subject.isIndiscernible(), 0.0 == subject.measure(x, y));
    }
}
