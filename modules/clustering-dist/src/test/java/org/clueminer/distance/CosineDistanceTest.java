package org.clueminer.distance;

import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.math.Vector;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class CosineDistanceTest {

    private static CosineDistance subject;
    private static final double DELTA = 1e-9;

    public CosineDistanceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        subject = new CosineDistance();
    }

    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    @Test
    public void testSimpleDistance() {
        double[] data = {1, 1, 1};
        double[] data2 = {0, 2, 4};
        Instance a = new DoubleArrayDataRow(data);
        Instance b = new DoubleArrayDataRow(data2);

        assertEquals(0, subject.measure(a, a), DELTA);
        assertTrue(subject.measure(a, b) > 0);
    }

    @Test
    public void testSimpleDistance2() {
        double[] x = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
        double[] y = {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1};
        Instance a = new DoubleArrayDataRow(x);
        Instance b = new DoubleArrayDataRow(y);

        assertEquals(0.6666666666666666, subject.measure(a, b), DELTA);
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
    public void testDistance() {
        Vector x, y;

        x = new DoubleArrayDataRow(new double[]{1, 5, 2, 3, 10});
        y = new DoubleArrayDataRow(new double[]{4, 15, 20, 5, 5});
        double dist = subject.measure(x, y);

        assertEquals(0.40629405295727883, dist, DELTA);
    }

    @Test
    public void testCompare() {
        Vector x, y, z;

        x = new DoubleArrayDataRow(new double[]{1, 2, 3});
        y = new DoubleArrayDataRow(new double[]{3, 5, 7});
        z = new DoubleArrayDataRow(new double[]{13, 55, 7});
        double dist = subject.measure(x, y);
        //@see http://reference.wolfram.com/language/ref/CosineDistance.html
        assertEquals(1 - 17 * Math.sqrt(2.0 / 581.0), dist, DELTA);
        double dist2 = subject.measure(x, z);
        //dist is better (closer) than dist2
        assertEquals(true, subject.compare(dist, dist2));
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
