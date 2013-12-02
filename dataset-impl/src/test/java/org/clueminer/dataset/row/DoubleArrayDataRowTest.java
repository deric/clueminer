package org.clueminer.dataset.row;

import org.clueminer.math.Vector;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tomas Barton
 */
public class DoubleArrayDataRowTest {

    private DoubleArrayDataRow t1;
    private DoubleArrayDataRow t2;
    //precision for comparing
    static double delta = 1e-9;
    static double[] t1array = new double[]{1, 2, 3, 4, 5};

    public DoubleArrayDataRowTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        t1 = new DoubleArrayDataRow(5);
        //we have to ensure test independency
        t1.set(t1array.clone());
        t2 = new DoubleArrayDataRow(new double[]{10.5, 24.5, 30.5, 10.0, 50.4});
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test of set method, of class DoubleArrayDataRow.
     */
    @Test
    public void testAdd() {
        int expected = t1.getLast() + 1;
        t1.put(5);
        //we set one more than is capacity of an t1array, it should be
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
     * Test of item method, of class DoubleArrayDataRow.
     */
    @Test
    public void testGet_int() {
        //get first item in test instance
        assertEquals(10.5, t2.value(0), delta);
    }

    /**
     * Test of item method, of class DoubleArrayDataRow.
     */
    @Test
    public void testGet_int_double() {
        //if index is not in t1array, return default value
        assertEquals(100, t2.getValue(-1, 100), delta);
        assertEquals(100, t2.getValue(t2.size(), 100), delta);
    }

    /**
     * Test of set method, of class DoubleArrayDataRow.
     */
    @Test
    public void testSet_int_double() {
        double value = 789.654;
        int idx = 1;
        assertNotSame(value, t1.get(idx));
        t1.set(idx, value);
        assertEquals(value, t1.get(idx), delta);
    }

    @Test
    public void testSetWithoutLoosingPrecision() {
        double value = 789.123456789123123456456789;
        int idx = 3;
        t1.set(idx, value);
        assertEquals(value, t1.get(idx), delta);
    }

    /**
     * Try setting value over capacity of an array
     */
    @Test
    public void testSetOutOfCapacity_int_double() {
        double value = 7;
        int idx = 6;
        assertNotSame(value, t1.get(idx));
        t1.set(idx, value);
        assertEquals(value, t1.get(idx), delta);
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
        assertEquals(5, t1.size());
        assertEquals(5, t2.size());
    }

    /**
     * Test of copy method, of class DoubleArrayDataRow.
     */
    @Test
    public void testCopy() {
        DoubleArrayDataRow copy = (DoubleArrayDataRow) t1.copy();
        System.out.println("copy: " + copy.toString());
        assertTrue(t1.equals(copy));
        copy.set(0, 10);
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

    /**
     * Test of getFullName method, of class DoubleArrayDataRow.
     */
    @Test
    public void testGetFullName() {
    }

    /**
     * Test of set method, of class DoubleArrayDataRow.
     */
    @Test
    public void testPut_double() {
        int size = t1.size();
        t1.put(123.0);
        assertEquals(size + 1, t1.size());
    }

    /**
     * Test of value method, of class DoubleArrayDataRow.
     */
    @Test
    public void testValue() {
    }

    /**
     * Test of getDouble method, of class DoubleArrayDataRow.
     */
    @Test
    public void testGetValue_int_double() {
    }

    /**
     * Test of getDouble method, of class DoubleArrayDataRow.
     */
    @Test
    public void testGetValue_int() {
    }

    /**
     * Test of item method, of class DoubleArrayDataRow.
     */
    @Test
    public void testGet() {
    }

    /**
     * Test of set method, of class DoubleArrayDataRow.
     */
    @Test
    public void testPut_int_double() {
    }

    /**
     * Test of set method, of class DoubleArrayDataRow.
     */
    @Test
    public void testSet_doubleArr() {
    }

    /**
     * Test of set method, of class DoubleArrayDataRow.
     */
    @Test
    public void testSet_int_Number() {
    }

    /**
     * Test of setValue method, of class DoubleArrayDataRow.
     */
    @Test
    public void testSetValue() {
    }

    /**
     * Test of isEmpty method, of class DoubleArrayDataRow.
     */
    @Test
    public void testIsEmpty() {
        assertEquals(false, t1.isEmpty());
        assertEquals(true, new DoubleArrayDataRow(0).isEmpty());
        assertEquals(true, new DoubleArrayDataRow(5).isEmpty());
    }

    /**
     * Test of setCapacity method, of class DoubleArrayDataRow.
     */
    @Test
    public void testSetCapacity() {
    }

    /**
     * Test of getCapacity method, of class DoubleArrayDataRow.
     */
    @Test
    public void testGetCapacity() {
    }

    /**
     * Test of remove method, of class DoubleArrayDataRow.
     */
    @Test
    public void testRemove() {
    }

    /**
     * Test of getLast method, of class DoubleArrayDataRow.
     */
    @Test
    public void testGetLast() {
    }

    /**
     * Test of arrayCopy method, of class DoubleArrayDataRow.
     */
    @Test
    public void testArrayCopy() {
    }

    /**
     * Test of getPlotter method, of class DoubleArrayDataRow.
     */
    @Test
    public void testGetPlotter() {
    }

    /**
     * Test of magnitude method, of class DoubleArrayDataRow.
     */
    @Test
    public void testMagnitude() {
    }

    /**
     * Test of toString method, of class DoubleArrayDataRow.
     */
    @Test
    public void testToString_0args() {
    }

    /**
     * Test of toString method, of class DoubleArrayDataRow.
     */
    @Test
    public void testToString_String() {
        System.out.println(t1.toString());
        assertEquals("1.0,2.0,3.0,4.0,5.0", t1.toString());
    }

    @Test
    public void testToStringArray() {
        //     System.out.println(t1.toStringArray());
        //assertEquals("1.0,2.0,3.0,4.0,5.0", t1.toString());
    }

    /**
     * Test of put method, of class DoubleArrayDataRow.
     */
    @Test
    public void testPut() {
        int size = t1.size();
        t1.put(123.);
        assertEquals(size + 1, t1.size());
    }

    /**
     * Test of item method, of class DoubleArrayDataRow.
     */
    @Test
    public void testItem() {
    }

    /**
     * Test of getDouble method, of class DoubleArrayDataRow.
     */
    @Test
    public void testGetDouble() {
    }

    /**
     * Test of add method, of class DoubleArrayDataRow.
     */
    @Test
    public void testAdd_Vector() {
    }

    /**
     * Test of add method, of class DoubleArrayDataRow.
     */
    @Test
    public void testAdd_int_double() {
    }

    /**
     * Test of toArray method, of class DoubleArrayDataRow.
     */
    @Test
    public void testToArray() {
    }
}
