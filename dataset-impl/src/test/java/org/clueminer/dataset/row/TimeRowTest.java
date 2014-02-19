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
        subject = new TimeRow(Double.class, 15);
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
        int size = subject.size();
        subject.put(123.0);
        assertEquals(123.0, subject.get(subject.size() - 1), delta);
        assertEquals(size + 1, subject.size());
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
        //first item
        int index = 0;
        double prev = subject.get(index);
        subject.set(index, prev + 1);
        assertEquals(prev + 1, subject.get(index), delta);

        //setting value on a null position
        index = 5;
        prev = 123;
        subject.set(index, prev);
        assertEquals(prev, subject.get(index), delta);
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

    /**
     * Test of multiply method, of class TimeRow.
     */
    @Test
    public void testMultiply() {
        double[] expected = new double[]{2.0, 10.0, 6.28, 5.62};
        TimeRow res = subject.multiply(2.0);
        double[] copy = res.arrayCopy();
        int i = 0;
        for (double d : copy) {
            assertEquals(d, expected[i], delta);
            i++;
        }
    }

    /**
     * Test of hasIndex method, of class TimeRow.
     */
    @Test
    public void testHasIndex() {
        assertEquals(true, subject.hasIndex(0));
        assertEquals(true, subject.hasIndex(1));
        assertEquals(true, subject.hasIndex(2));
        assertEquals(true, subject.hasIndex(3));
        assertEquals(false, subject.hasIndex(4));
        assertEquals(false, subject.hasIndex(-1));
        assertEquals(false, subject.hasIndex(subject.size()));
    }

    /**
     * Test of setDefaultValue method, of class TimeRow.
     */
    @Test
    public void testSetDefaultValue() {
        subject.setDefaultValue(-1.0);
        assertEquals(-1.0, subject.get(-1), delta);
    }

    @Test
    public void testMetaData() {
        double[] meta = new double[]{123.0, 42.0};
        subject.setMetaNum(meta);

        double[] foo = subject.getMetaNum();
        assertEquals(meta[0], foo[0], delta);
        assertEquals(meta[1], foo[1], delta);
    }

    @Test
    public void testMin() {
        assertEquals(1.0, subject.getMin(), delta);
    }

    @Test
    public void testMax() {
        assertEquals(5.0, subject.getMax(), delta);
    }

    @Test
    public void testStdDev() {
        subject = new TimeRow(Double.class, 15);
        subject.put(1.0);
        subject.put(2.0);
        subject.put(3.0);
        subject.put(4.0);
        subject.put(5.0);

        assertEquals(1.5811388300841898, subject.getStdDev(), delta);
    }

}
