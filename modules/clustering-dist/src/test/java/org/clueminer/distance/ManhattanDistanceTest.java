package org.clueminer.distance;

import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.math.Vector;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ManhattanDistanceTest {

    private static final ManhattanDistance subject = new ManhattanDistance();
    private static final double DELTA = 1e-9;

    public ManhattanDistanceTest() {
    }

    @Test
    public void testMeasure_Vector_Vector() {
        Vector x = new DoubleArrayDataRow(new double[]{0, 0});
        Vector y = new DoubleArrayDataRow(new double[]{1.0, 1.0});
        assertEquals(2.0, subject.measure(x, y), DELTA);
    }

    @Test
    public void testMeasureZeroDistance() {
        double dist = subject.measure(new DoubleArrayDataRow(new double[]{0, 0, 0}), new DoubleArrayDataRow(new double[]{0, 0, 0}));
        assertEquals(0.0, dist, DELTA);
    }

    @Test
    public void testCompare() {
        assertEquals(true, subject.compare(1, 5));
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
