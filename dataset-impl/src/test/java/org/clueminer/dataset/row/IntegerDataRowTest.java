package org.clueminer.dataset.row;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class IntegerDataRowTest {

    private static int[] d1;
    private static int[] d2;
    static IntegerDataRow t1;
    static IntegerDataRow t2;
    static double delta = 1e-9;

    public IntegerDataRowTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        d1 = new int[]{424, 2155, 3014, 2982, 3502, 2891, 3948, 3778, 4091, 4068, 3736, 3915, 4125, 4090, 4087};
        d2 = new int[]{527, 2308, 2871, 3384, 3505, 2802, 4020, 4344, 4016, 4540, 4736, 4171, 4759, 4562, 4922};


        t1 = new IntegerDataRow(15);
        t2 = new IntegerDataRow(d2);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        t1.set(d1.clone());
        t2.set(d2.clone());
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of set method, of class IntegerDataRow.
     */
    @Test
    public void testSet_intArr() {
        t1 = new IntegerDataRow(15);
        t1.set(d1);

        t2 = new IntegerDataRow(d2);
    }

    /**
     * Test of getFullName method, of class IntegerDataRow.
     */
    @Test
    public void testGetFullName() {
    }

    /**
     * Test of set method, of class IntegerDataRow.
     */
    @Test
    public void testPut_int() {
        t1.put(42);
        assertEquals(42, t1.item(t1.size() - 1), delta);
    }

    /**
     * Test of value method, of class IntegerDataRow.
     */
    @Test
    public void testValue() {
        assertEquals(424, t1.value(0), delta);
    }

    /**
     * Test of intValue method, of class IntegerDataRow.
     */
    @Test
    public void testIntValue() {
        assertEquals(424, t1.intValue(0));
    }

    /**
     * Test of getDouble method, of class IntegerDataRow.
     */
    @Test
    public void testGetValue_int_double() {
    }

    /**
     * Test of getDouble method, of class IntegerDataRow.
     */
    @Test
    public void testGetValue_int() {
    }

    /**
     * Test of item method, of class IntegerDataRow.
     */
    @Test
    public void testGet() {
        assertEquals(424, t1.item(0), delta);
    }

    /**
     * Test of set method, of class IntegerDataRow.
     */
    @Test
    public void testPut_int_double() {
        t1.set(0, 500.0);
        assertEquals(500, t1.item(0), delta);
    }

    /**
     * Test of set method, of class IntegerDataRow.
     */
    @Test
    public void testSet_int_Number() {
    }

    /**
     * Test of setValue method, of class IntegerDataRow.
     */
    @Test
    public void testSetValue() {
    }

    /**
     * Test of size method, of class IntegerDataRow.
     */
    @Test
    public void testSize() {
        assertEquals(t1.size(), 15);
        t1.put(1234);
        assertEquals(t1.size(), 16);
    }

    /**
     * Test of isEmpty method, of class IntegerDataRow.
     */
    @Test
    public void testIsEmpty() {
        assertFalse(t1.isEmpty());
    }

    /**
     * Test of setCapacity method, of class IntegerDataRow.
     */
    @Test
    public void testSetCapacity() {
    }

    /**
     * Test of getCapacity method, of class IntegerDataRow.
     */
    @Test
    public void testGetCapacity() {
    }

    /**
     * Test of trim method, of class IntegerDataRow.
     */
    @Test
    public void testTrim() {
    }

    /**
     * Test of remove method, of class IntegerDataRow.
     */
    @Test
    public void testRemove() {
    }

    /**
     * Test of getLast method, of class IntegerDataRow.
     */
    @Test
    public void testGetLast() {
    }

    /**
     * Test of copy method, of class IntegerDataRow.
     */
    @Test
    public void testCopy() {
    }

    /**
     * Test of arrayCopy method, of class IntegerDataRow.
     */
    @Test
    public void testArrayCopy() {
        double[] arry = t1.arrayCopy();
        for (int i = 0; i < arry.length; i++) {
            assertEquals(d1[i], arry[i], delta);
        }
    }

    /**
     * Test of arrayCopyInt method, of class IntegerDataRow.
     */
    @Test
    public void testArrayCopyInt() {
        int[] arry = t1.arrayCopyInt();
        for (int i = 0; i < arry.length; i++) {
            assertEquals(d1[i], arry[i]);
        }
    }

    /**
     * Test of getPlotter method, of class IntegerDataRow.
     */
    @Test
    public void testGetPlotter() {
    }

    /**
     * Test of magnitude method, of class IntegerDataRow.
     */
    @Test
    public void testMagnitude() {
    }

    /**
     * Test of add method, of class IntegerDataRow.
     */
    @Test
    public void testAdd() {
    }

    /**
     * Test of set method, of class IntegerDataRow.
     */
    @Test
    public void testPut_double() {
    }

    /**
     * Test of iterator method, of class IntegerDataRow.
     */
    @Test
    public void testIterator() {
    }

    /**
     * Test of hashCode method, of class IntegerDataRow.
     */
    @Test
    public void testHashCode() {
    }

    /**
     * Test of equals method, of class IntegerDataRow.
     */
    @Test
    public void testEquals() {
    }

    /**
     * Test of toString method, of class IntegerDataRow.
     */
    @Test
    public void testToString_0args() {
    }

    /**
     * Test of toString method, of class IntegerDataRow.
     */
    @Test
    public void testToString_String() {
    }
}