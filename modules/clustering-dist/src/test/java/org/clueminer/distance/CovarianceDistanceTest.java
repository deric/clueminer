package org.clueminer.distance;

import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.math.Vector;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class CovarianceDistanceTest {

    private static final CovarianceDistance subject = new CovarianceDistance();
    private static final double delta = 1e-9;

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
        Vector x = new DoubleArrayDataRow(new double[]{1, 2, 3});
        Vector y = new DoubleArrayDataRow(new double[]{2, 4, 6});
        double dist = subject.measure(x, y);
        assertEquals(2, dist, delta);
        Vector z = new DoubleArrayDataRow(new double[]{3, 6, 9});
        assertEquals(3, subject.measure(x, z), delta);
    }

    @Test
    public void testMeasure_3args() {
    }

    @Test
    public void testCompare() {
        Vector x, y, z;

        x = new DoubleArrayDataRow(new double[]{1, 2, 3});
        y = new DoubleArrayDataRow(new double[]{3, 5, 10});
        z = new DoubleArrayDataRow(new double[]{50, 1, 29});
        double dist = subject.measure(x, y);
        double dist2 = subject.measure(x, z);

        assertEquals(3.5, dist, delta);
        assertEquals(true, subject.compare(dist, dist2));
    }

    @Test
    public void testGetMinValue() {
    }

    @Test
    public void testGetMaxValue() {
    }

    @Test
    public void testMeasureZeroDistance() {
        double dist = subject.measure(new DoubleArrayDataRow(new double[]{0, 0, 0}), new DoubleArrayDataRow(new double[]{0, 0, 0}));
        assertEquals(0.0, dist, delta);
    }

    @Test
    public void testIsSymmetric() {
        Vector x = new DoubleArrayDataRow(new double[]{1, 2, 3});
        Vector y = new DoubleArrayDataRow(new double[]{2, 4, 6});
        double xy = subject.measure(x, y);
        double yx = subject.measure(y, x);
        assertEquals(xy, yx, delta);
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

}
