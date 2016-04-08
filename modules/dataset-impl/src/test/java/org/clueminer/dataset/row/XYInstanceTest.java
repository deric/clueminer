/*
 * Copyright (C) 2011-2016 clueminer.org
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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class XYInstanceTest {

    XYInstance a, b;
    private static final double DELTA = 1e-9;

    public XYInstanceTest() {
    }

    @Test
    public void testResetMinMax() {
    }

    @Test
    public void testGetAllStatistics() {
    }

    @Test
    public void testRegisterStatistics() {
    }

    @Test
    public void testStatistics() {
    }

    @Test
    public void testUpdateStatistics() {
    }

    @Test
    public void testResetStatistics() {
    }

    @Test
    public void testSize() {
        a = new XYInstance(5);
        assertEquals(0, a.size());
    }

    @Test
    public void testIsEmpty() {
        a = new XYInstance(5);
        assertEquals(true, a.isEmpty());
        a.put(0);
        assertEquals(false, a.isEmpty());
    }

    @Test
    public void testPutXy() {
        a = new XYInstance(5);
        a.put(1, 5);
        assertEquals(false, a.isEmpty());
        assertEquals(1, a.size());
    }

    @Test
    public void testPut() {
        a = new XYInstance(5);
        for (int i = 0; i < 7; i++) {
            a.put(i, i + 5);
        }
        assertEquals(7, a.size());
        assertEquals(11, a.get(6), DELTA);
    }

    @Test
    public void testArrayCopy() {
    }

    @Test
    public void testGetMetaNum() {
    }

    @Test
    public void testSetMetaNum() {
    }

    @Test
    public void testMagnitude() {
    }

    @Test
    public void testDot() {
    }

    @Test
    public void testPNorm() {
    }

    @Test
    public void testGetStartTime() {
    }

    @Test
    public void testRemove() {
    }

    @Test
    public void testValue() {
    }

    @Test
    public void testSet_int_double() {
    }

    @Test
    public void testSetCapacity() {
    }

    @Test
    public void testGetCapacity() {
    }

    @Test
    public void testToString() {
    }

    @Test
    public void testToStringArray() {
    }

    @Test
    public void testGetPlotter() {
    }

    @Test
    public void testGetValue() {
    }

    @Test
    public void testHasIndex() {
        a = new XYInstance(5);
        assertEquals(false, a.hasIndex(0));
        a.put(5, 4);
        assertEquals(true, a.hasIndex(0));
    }

    @Test
    public void testSet_int_Number() {
    }

    @Test
    public void testAdd_Vector() {
    }

    @Test
    public void testMinus_Vector() {
    }

    @Test
    public void testTimes() {
    }

    @Test
    public void testDuplicate() {
    }

    @Test
    public void testValues() {
    }

    @Test
    public void testValueAt_double() {
    }

    @Test
    public void testValueAt_double_Interpolator() {
    }

    @Test
    public void testGetMin() {
    }

    @Test
    public void testGetMax() {
    }

    @Test
    public void testGetStdDev() {
    }

    @Test
    public void testCrop() {
    }

    @Test
    public void testNormalize() {
    }

    @Test
    public void testCopy() {
    }

    @Test
    public void testIterator() {
    }

    @Test
    public void testAdd_double() {
    }

    @Test
    public void testMinus_double() {
    }

}
