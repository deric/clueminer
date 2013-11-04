package org.clueminer.dataset.row;

import java.util.Iterator;
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
public class TimeRowTest {

    private static TimeRow subject;
    private static double delta = 1e-9;

    public TimeRowTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        subject = new TimeRow(15);
        subject.put(1.0);
        subject.put(5.0);
        subject.put(3.14);
        subject.put(2.81);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of item method, of class TimeRow.
     */
    @Test
    public void testItem() {
        assertEquals(1.0, subject.item(0).doubleValue(), delta);
        assertEquals(5.0, subject.item(1).doubleValue(), delta);
    }

    /**
     * Test of getFullName method, of class TimeRow.
     */
    @Test
    public void testGetFullName() {
        subject.setName("foo");
        assertEquals("foo", subject.getName());
    }

    /**
     * Test of put method, of class TimeRow.
     */
    @Test
    public void testPut() {
        subject.put(123.0);
        assertEquals(123.0, subject.get(subject.size() - 1), delta);
    }

    /**
     * Test of remove method, of class TimeRow.
     */
    @Test
    public void testRemove() {
    }

    /**
     * Test of value method, of class TimeRow.
     */
    @Test
    public void testValue() {
    }

    /**
     * Test of set method, of class TimeRow.
     */
    @Test
    public void testSet_int_double() {
    }

    /**
     * Test of setCapacity method, of class TimeRow.
     */
    @Test
    public void testSetCapacity() {
    }

    /**
     * Test of getCapacity method, of class TimeRow.
     */
    @Test
    public void testGetCapacity() {
    }

    /**
     * Test of toString method, of class TimeRow.
     */
    @Test
    public void testToString() {
    }

    /**
     * Test of toStringArray method, of class TimeRow.
     */
    @Test
    public void testToStringArray() {
    }

    /**
     * Test of getPlotter method, of class TimeRow.
     */
    @Test
    public void testGetPlotter() {
    }

    /**
     * Test of getValue method, of class TimeRow.
     */
    @Test
    public void testGetValue() {
    }

    /**
     * Test of get method, of class TimeRow.
     */
    @Test
    public void testGet() {
        assertEquals(1.0, subject.get(0), delta);
        assertEquals(5.0, subject.get(1), delta);
    }

    /**
     * Test of magnitude method, of class TimeRow.
     */
    @Test
    public void testMagnitude() {
    }

    /**
     * Test of set method, of class TimeRow.
     */
    @Test
    public void testSet_int_Number() {
    }

    /**
     * Test of add method, of class TimeRow.
     */
    @Test
    public void testAdd() {
    }

    /**
     * Test of valueAt method, of class TimeRow.
     */
    @Test
    public void testValueAt_double() {
    }

    /**
     * Test of valueAt method, of class TimeRow.
     */
    @Test
    public void testValueAt_double_Interpolator() {
    }

    /**
     * Test of crop method, of class TimeRow.
     */
    @Test
    public void testCrop() {
    }

    /**
     * Test of normalize method, of class TimeRow.
     */
    @Test
    public void testNormalize() {
    }

    /**
     * Test of copy method, of class TimeRow.
     */
    @Test
    public void testCopy() {
        double[] expected = new double[]{1.0, 5.0, 3.14, 2.81};
        double[] copy = subject.arrayCopy();
        int i = 0;
        for (double d : copy) {
            assertEquals(d, expected[i], delta);
            i++;
        }
    }

    /**
     * Test of iterator method, of class TimeRow.
     */
    @Test
    public void testIterator() {
        double[] expected = new double[]{1.0, 5.0, 3.14, 2.81};
        int i = 0;
        Iterator<Double> it = subject.iterator();
        while (it.hasNext()) {
            assertEquals(it.next(), expected[i], delta);
            i++;
        }
    }

    /**
     * Test of size method, of class TimeRow.
     */
    @Test
    public void testSize() {
        assertEquals(4, subject.size());
    }
}