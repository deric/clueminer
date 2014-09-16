package org.clueminer.dataset.row;

import org.clueminer.math.Vector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class FloatArrayDataRowTest {

    private static final double delta = 1e-9;

    public FloatArrayDataRowTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetFullName() {
    }

    @Test
    public void testValue() {
    }

    @Test
    public void testGetValue_int_double() {
    }

    @Test
    public void testGetValue_int() {
    }

    @Test
    public void testGet() {
    }

    @Test
    public void testPut() {
    }

    @Test
    public void testSet_int_double() {
    }

    @Test
    public void testSet_floatArr() {
    }

    @Test
    public void testRemove() {
    }

    @Test
    public void testSetValue() {
    }

    @Test
    public void testSize() {
    }

    @Test
    public void testIsEmpty() {
    }

    @Test
    public void testSetCapacity() {
    }

    @Test
    public void testGetCapacity() {
    }

    @Test
    public void testTrim() {
    }

    @Test
    public void testArrayCopy() {
    }

    @Test
    public void testGetPlotter() {
    }

    @Test
    public void testMagnitude() {
    }

    @Test
    public void testSet_int_Number() {
    }

    @Test
    public void testAdd() {
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
        Vector<Float> upd = orig.subtract(1);

        for (int i = 0; i < orig.size(); i++) {
            //original shoud not change
            assertEquals(5.0, orig.get(i), delta);
            //new one should be updated
            assertEquals(4.0, upd.get(i), delta);
        }
    }

    ;

    @Test
    public void testDuplicate() {
    }

    @Test
    public void testIterator() {
    }

    @Test
    public void testCopy() {
    }

    @Test
    public void testHashCode() {
    }

    @Test
    public void testEquals() {
    }

    @Test
    public void testToString_0args() {
    }

    @Test
    public void testToString_String() {
    }

}
