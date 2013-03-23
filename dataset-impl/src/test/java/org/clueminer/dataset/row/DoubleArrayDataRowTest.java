package org.clueminer.dataset.row;

import org.clueminer.math.Vector;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tomas Barton
 */
public class DoubleArrayDataRowTest {

    static DoubleArrayDataRow t1;
    static DoubleArrayDataRow t2;
    //precision for comparing
    static double delta = 0.0001;

    public DoubleArrayDataRowTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        t1 = new DoubleArrayDataRow(5);
        double[] array = new double[]{1, 2, 3, 4, 5};
        t1.set(array);
        t2 = new DoubleArrayDataRow(new double[]{10.5, 24.5, 30.5, 10.0, 50.4});
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of put method, of class DoubleArrayDataRow.
     */
    @Test
    public void testAdd() {
        int expected = t1.getLast() + 1;
        t1.put(5);
        //we put one more than is capacity of an array, it should be
        //extended to bigger size
        assertEquals(expected, t1.getLast());
    }

    @Test
    public void testVectorAdd() {
        Vector<Double> vec = new DoubleArrayDataRow(new double[]{10.5, 24.5, 30.5, 10.0, 50.4});
        Vector<Double> res = t2.add(vec);
        
        assertEquals(21, res.getValue(0).doubleValue(), delta);
        assertEquals(49, res.getValue(1).doubleValue(), delta);
        assertEquals(61, res.getValue(2).doubleValue(), delta);
        assertEquals(20, res.getValue(3).doubleValue(), delta);
        assertEquals(100.8, res.getValue(4).doubleValue(), delta);
    }

    /**
     * Test of get method, of class DoubleArrayDataRow.
     */
    @Test
    public void testGet_int() {
        //get first item in test instance
        assertEquals(10.5, t2.value(0), delta);
    }

    /**
     * Test of get method, of class DoubleArrayDataRow.
     */
    @Test
    public void testGet_int_double() {
        //if index is not in array, return default value
        assertEquals(100, t2.getValue(-1, 100), delta);
        assertEquals(100, t2.getValue(t2.size(), 100), delta);
    }

    /**
     * Test of set method, of class DoubleArrayDataRow.
     */
    @Test
    public void testSet_int_double() {
    }

    /**
     * Test of set method, of class DoubleArrayDataRow.
     */
    @Test
    public void testSet_3args() {
    }

    /**
     * Test of ensureCapacity method, of class DoubleArrayDataRow.
     */
    @Test
    public void testEnsureCapacity() {
    }

    /**
     * Test of trim method, of class DoubleArrayDataRow.
     */
    @Test
    public void testTrim() {
    }

    /**
     * Test of toString method, of class DoubleArrayDataRow.
     */
    @Test
    public void testToString() {
    }

    /**
     * Test of getType method, of class DoubleArrayDataRow.
     */
    @Test
    public void testGetType() {
    }

    /**
     * Test of size method, of class DoubleArrayDataRow.
     */
    @Test
    public void testSize() {
    }

    /**
     * Test of copy method, of class DoubleArrayDataRow.
     */
    @Test
    public void testCopy() {
        DoubleArrayDataRow copy = (DoubleArrayDataRow) t1.copy();
        assertTrue(t1.equals(copy));
        copy.put(0, 10);
        assertFalse(t1.equals(copy));
    }

    /**
     * Test of iterator method, of class DoubleArrayDataRow.
     */
    @Test
    public void testIterator() {
        int i = 0;
        while (t1.iterator().hasNext()) {
            t1.iterator().next();
            i++;
        }
        assertEquals(t1.size(), i);
    }

    /**
     * Test of hashCode method, of class DoubleArrayDataRow.
     */
    @Test
    public void testHashCode() {
    }

    /**
     * Test of equals method, of class DoubleArrayDataRow.
     */
    @Test
    public void testEquals() {
        assertFalse(t1.equals(t2));
        assertTrue(t1.equals(t1));
    }
}