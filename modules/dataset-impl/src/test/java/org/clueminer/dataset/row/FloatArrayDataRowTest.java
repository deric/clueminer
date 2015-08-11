package org.clueminer.dataset.row;

import org.clueminer.math.Vector;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class FloatArrayDataRowTest {

    private static final double delta = 1e-9;

    public FloatArrayDataRowTest() {
    }

    @Test
    public void testAdd() {
        Vector<Float> orig = new FloatArrayDataRow(new float[]{2, 3, 5});
        Vector<Float> othr = new FloatArrayDataRow(new float[]{1, 2, 3});
        Vector<Float> expt = new FloatArrayDataRow(new float[]{3, 5, 8});
        Vector<Float> upd = orig.add(othr);

        for (int i = 0; i < orig.size(); i++) {
            //original shoud not change
            assertEquals(upd.get(i), expt.get(i), delta);
        }
    }

    @Test
    public void testAdd_double() {
        Vector<Float> orig = new FloatArrayDataRow(new float[]{0, 0, 0});
        Vector<Float> upd = orig.add(1);

        for (int i = 0; i < orig.size(); i++) {
            //original shoud not change
            assertEquals(0.0, orig.get(i), delta);
            //new one should be updated
            assertEquals(1.0, upd.get(i), delta);
        }
    }

    @Test
    public void testSubtract_double() {
        Vector<Float> orig = new FloatArrayDataRow(new float[]{5, 5, 5});
        Vector<Float> upd = orig.minus(1);

        for (int i = 0; i < orig.size(); i++) {
            //original shoud not change
            assertEquals(5.0, orig.get(i), delta);
            //new one should be updated
            assertEquals(4.0, upd.get(i), delta);
        }
    }

    @Test
    public void testMinus() {
        Vector<Float> orig = new FloatArrayDataRow(new float[]{2, 3, 5});
        Vector<Float> othr = new FloatArrayDataRow(new float[]{1, 2, 3});
        Vector<Float> expt = new FloatArrayDataRow(new float[]{1, 1, 2});
        Vector<Float> upd = orig.minus(othr);

        for (int i = 0; i < orig.size(); i++) {
            //original shoud not change
            assertEquals(upd.get(i), expt.get(i), delta);
        }
    }

    @Test
    public void testTimes() {
        Vector<Float> orig = new FloatArrayDataRow(new float[]{2, 3, 5});
        Vector<Float> expt = new FloatArrayDataRow(new float[]{4, 6, 10});
        Vector<Float> upd = orig.times(2);

        for (int i = 0; i < orig.size(); i++) {
            //original shoud not change
            assertEquals(upd.get(i), expt.get(i), delta);
        }
    }

}
