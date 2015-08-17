package org.clueminer.dataset.row;

import java.util.Iterator;
import java.util.Random;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tomas Barton
 */
public class SparseInstanceTest {

    private static SparseInstance instance;
    private static int attrNum = 30;

    public SparseInstanceTest() {
        instance = new SparseInstance(attrNum);
        Random generator = new Random();
        for (int i = 0; i < attrNum; i++) {
            instance.set(i * 5, generator.nextDouble());
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of set method, of class SparseInstance.
     */
    @Test
    public void testAdd() {
        double d = 123456.0;
        instance.set(123, d);
        assertEquals(d, instance.value(123), 0.0001);
    }

    /**
     * Test of set method, of class SparseInstance.
     */
    @Test
    public void testPut() {
        Random rand = new Random();
        int s = instance.size();
        int max = 20;
        for (int i = 0; i < max; i++) {
            instance.put(rand.nextDouble());
        }
        assertEquals(s + max, instance.size());
    }

    /**
     * Test of putAll method, of class SparseInstance.
     */
    @Test
    public void testPutAll() {
    }

    /**
     * Test of remove method, of class SparseInstance.
     */
    @Test
    public void testRemove() {
    }

    /**
     * Test of value method, of class SparseInstance.
     */
    @Test
    public void testGetValue_int() {
    }

    /**
     * Test of set method, of class SparseInstance.
     */
    @Test
    public void testSetValue_int_double() {
    }

    /**
     * Test of size method, of class SparseInstance.
     */
    @Test
    public void testSize() {
        assertEquals(attrNum, instance.size());
    }

    /**
     * Test of isEmpty method, of class SparseInstance.
     */
    @Test
    public void testIsEmpty() {
        assertEquals(false, instance.isEmpty());
        instance.clear();
        assertEquals(0, instance.size());
        assertEquals(true, instance.isEmpty());
    }

    /**
     * Test of setCapacity method, of class SparseInstance.
     */
    @Test
    public void testSetCapacity() {
    }

    /**
     * Test of getCapacity method, of class SparseInstance.
     */
    @Test
    public void testGetCapacity() {
    }

    /**
     * Test of hashCode method, of class SparseInstance.
     */
    @Test
    public void testHashCode() {
    }

    /**
     * Test of equals method, of class SparseInstance.
     */
    @Test
    public void testEquals() {
        SparseInstance a, b;
        a = new SparseInstance();
        b = new SparseInstance();
        for (int i = 0; i < 3; i++) {
            a.set(i, i + 1);
            b.set(i, i + 1);
        }

        //objects which are .equals() MUST have the same .hashCode()
        assertEquals(true, a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());

        //after setting index instances should NOT be considered as the same
        a.setIndex(0);
        a.setIndex(1);
        assertEquals(false, a.equals(b));
        assertEquals(false, a.hashCode() == b.hashCode());
    }

    /**
     * Test of copy method, of class SparseInstance.
     */
    @Test
    public void testCopy() {
    }

    /**
     * Test of value method, of class SparseInstance.
     */
    @Test
    public void testGetValue_int_double() {
    }

    /**
     * Test of set method, of class SparseInstance.
     */
    @Test
    public void testSetValue_3args() {
    }

    /**
     * Test of trim method, of class SparseInstance.
     */
    @Test
    public void testTrim() {
    }

    /**
     * Test of toString method, of class SparseInstance.
     */
    @Test
    public void testToString_0args() {
    }

    /**
     * Test of toString method, of class SparseInstance.
     */
    @Test
    public void testToString_String() {
    }

    /**
     * Test of clear method, of class SparseInstance.
     */
    @Test
    public void testClear() {
        instance.clear();
        assertEquals(0, instance.size());
    }

    /**
     * Test of containsKey method, of class SparseInstance.
     */
    @Test
    public void testContainsKey() {
    }

    /**
     * Test of containsValue method, of class SparseInstance.
     */
    @Test
    public void testContainsValue() {
    }

    /**
     * Test of entrySet method, of class SparseInstance.
     */
    @Test
    public void testEntrySet() {
    }

    /**
     * Test of keySet method, of class SparseInstance.
     */
    @Test
    public void testKeySet() {
    }

    /**
     * Test of values method, of class SparseInstance.
     */
    @Test
    public void testValues() {
    }

    @Test
    public void testIterator() {
        Iterator it = instance.iterator();
        int cnt = 0;
        while (it.hasNext()) {
            it.next();
            cnt++;
        }
        assertEquals(cnt, instance.size());
    }
}
