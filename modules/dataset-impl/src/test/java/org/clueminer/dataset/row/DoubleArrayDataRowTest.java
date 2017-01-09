/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.dataset.row;

import org.clueminer.math.Vector;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
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
    private static final double DELTA = 1e-9;
    private static final double[] t1array = new double[]{1, 2, 3, 4, 5};

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

        assertEquals(21, res.getValue(0), DELTA);
        assertEquals(49, res.getValue(1), DELTA);
        assertEquals(61, res.getValue(2), DELTA);
        assertEquals(20, res.getValue(3), DELTA);
        assertEquals(100.8, res.getValue(4), DELTA);
    }

    /**
     * Test of item method, of class DoubleArrayDataRow.
     */
    @Test
    public void testGet_int() {
        //get first item in test instance
        assertEquals(10.5, t2.value(0), DELTA);
    }

    /**
     * Test of item method, of class DoubleArrayDataRow.
     */
    @Test
    public void testGet_int_double() {
        //if index is not in t1array, return default value
        assertEquals(100, t2.getValue(-1, 100), DELTA);
        assertEquals(100, t2.getValue(t2.size(), 100), DELTA);
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
        assertEquals(value, t1.get(idx), DELTA);
    }

    @Test
    public void testSetWithoutLoosingPrecision() {
        double value = 789.123456789123123456456789;
        int idx = 3;
        t1.set(idx, value);
        assertEquals(value, t1.get(idx), DELTA);
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
        assertEquals(value, t1.get(idx), DELTA);
    }

    /**
     * Test of set method, of class DoubleArrayDataRow.
     */
    @Test
    public void testSet_3args() {
        DoubleArrayDataRow test = new DoubleArrayDataRow(3);
        for (int i = 0; i < 3; i++) {
            test.set(i, i);
            assertEquals(i, test.get(i), DELTA);
        }
    }

    @Test
    public void testEnsureCapacity() {
    }

    @Test
    public void testTrim() {
    }

    @Test
    public void testToString() {
    }

    @Test
    public void testGetType() {
    }

    @Test
    public void testSize() {
        assertEquals(5, t1.size());
        assertEquals(5, t2.size());
    }

    @Test
    public void testAsArray() {
        DoubleArrayDataRow data = (DoubleArrayDataRow) t1.copy();
        double[] ary = data.asArray();
        assertEquals(1, ary[0], DELTA);
        //we should get reference to data
        ary[0] = 0;
        assertEquals(0, data.get(0), DELTA);
    }

    @Test
    public void testArrayCopy() {
        DoubleArrayDataRow data = (DoubleArrayDataRow) t1.copy();
        double[] ary = data.arrayCopy();
        assertEquals(1, ary[0], DELTA);
        ary[0] = 0;
        //we're getting a copy of data
        assertEquals(1, data.get(0), DELTA);
    }

    @Test
    public void testCopy() {
        DoubleArrayDataRow copy = (DoubleArrayDataRow) t1.copy();
        assertTrue(t1.equals(copy));
        copy.set(0, 10);
        assertFalse(t1.equals(copy));
    }

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
        double[] data = new double[]{1, 2, 3};
        DoubleArrayDataRow a, b;
        a = new DoubleArrayDataRow(data);
        b = new DoubleArrayDataRow(data);

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
        //TODO: new vector should be filled with zeros (expected behaviour in java)
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
        assertEquals("DoubleArrayData(5)[1.0,2.0,3.0,4.0,5.0]", t1.toString());
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

    @Test
    public void testAdd_double() {
        Vector<Double> orig = new DoubleArrayDataRow(new double[]{0, 0, 0});
        Vector<Double> upd = orig.add(1);

        for (int i = 0; i < orig.size(); i++) {
            //original shoud not change
            assertEquals(0.0, orig.get(i), DELTA);
            //new one should be updated
            assertEquals(1.0, upd.get(i), DELTA);
        }
    }

    @Test
    public void testSubtract_double() {
        Vector<Double> orig = new DoubleArrayDataRow(new double[]{5, 5, 5});
        Vector<Double> upd = orig.minus(1);

        for (int i = 0; i < orig.size(); i++) {
            //original shoud not change
            assertEquals(5.0, orig.get(i), DELTA);
            //new one should be updated
            assertEquals(4.0, upd.get(i), DELTA);
        }
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

    @Test
    public void testDuplicate() {
        Vector<Double> orig = new DoubleArrayDataRow(new double[]{1, 3, 8});
        Vector<Double> dup = orig.duplicate();

        for (int i = 0; i < orig.size(); i++) {
            //duplicated vector is empty
            assertNotSame(orig.get(i), dup.get(i));
        }
    }

    @Test
    public void testGetUnknown() {
    }

    @Test
    public void testSetUnknown() {
    }

    @Test
    public void testMinus() {
        Vector<Double> orig = new DoubleArrayDataRow(new double[]{5, 5, 5});
        Vector<Double> other = new DoubleArrayDataRow(new double[]{1, 1, 1});
        Vector<Double> upd = orig.minus(other);

        for (int i = 0; i < orig.size(); i++) {
            //original shoud not change
            assertEquals(5.0, orig.get(i), DELTA);
            //new one should be updated
            assertEquals(4.0, upd.get(i), DELTA);
        }
    }

    @Test
    public void testTimes() {
        Vector<Double> orig = new DoubleArrayDataRow(new double[]{2, 2, 2});
        Vector<Double> upd = orig.times(2.0);

        for (int i = 0; i < orig.size(); i++) {
            //original shoud not change
            assertEquals(2.0, orig.get(i), DELTA);
            //new one should be updated
            assertEquals(4.0, upd.get(i), DELTA);
        }
    }
}
