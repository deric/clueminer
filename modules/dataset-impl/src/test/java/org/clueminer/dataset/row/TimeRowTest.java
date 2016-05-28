package org.clueminer.dataset.row;

import java.util.Iterator;
import java.util.Random;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.StatsNum;
import org.clueminer.dataset.impl.TimeseriesDataset;
import org.clueminer.types.TimePoint;
import org.clueminer.utils.Dump;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class TimeRowTest {

    private static TimeRow subject;
    private static final double delta = 1e-9;

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

    @Test
    public void testCapacityOverflow() {
        int size = 5;
        Random rand = new Random();
        subject = new TimeRow(Double.class, size);
        for (int i = 0; i < 2 * size; i++) {
            subject.put(rand.nextDouble());
        }
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
        assertEquals(1.0, subject.value(0), delta);
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

    @Test
    public void testSetCapacity() {
        subject.setCapacity(20);
        assertEquals(20, subject.getCapacity());
    }

    @Test
    public void testGetCapacity() {
        assertEquals(15, subject.getCapacity());
    }

    @Test
    public void testToString() {
        assertNotNull(subject.toString());
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
     * Test of valueAt method, of class TimeRow.
     */
    @Test
    public void testValueAt_double_Interpolator() {
        //linear data
        int size = 7;

        TimeseriesDataset<ContinuousInstance> dataset = new TimeseriesDataset<>(5);
        ContinuousInstance inst = new TimeRow(Double.class, 7);
        TimePoint tp[] = new TimePointAttribute[size];
        for (int i = 0; i < tp.length; i++) {
            tp[i] = new TimePointAttribute(i, i + 100, Math.pow(i, 2));
            inst.put(i);
        }
        System.out.print("val: ");
        dataset.setTimePoints(tp);
        for (int i = 0; i < tp.length; i++) {
            if (i > 0) {
                System.out.print(" " + tp[i].getPosition());
            } else {
                System.out.print(tp[i].getPosition());
            }
        }
        System.out.println("");

        Dump.array(inst.arrayCopy(), "ts");

        dataset.add(inst);
        for (int i = 0; i < 20; i++) {
            assertNotNull(inst.valueAt(i));
            System.out.println(i + " = " + inst.valueAt(i));
        }
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
        //biased (without n-1 correction)
        assertEquals(1.4142135623730951, subject.statistics(StatsNum.STD_BIA), delta);
    }

}
