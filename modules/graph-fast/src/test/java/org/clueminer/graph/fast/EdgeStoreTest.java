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
package org.clueminer.graph.fast;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class EdgeStoreTest {

    private EdgeStore subject;

    public EdgeStoreTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testDefaultSize() {
        subject = new EdgeStore();
        int size = subject.size();
        boolean isEmpty = subject.isEmpty();

        assertEquals(isEmpty, true);
        assertEquals(size, 0);
    }

    @Test
    public void testEnsureCapacity() {
    }

    @Test
    public void testContains() {
    }

    @Test
    public void testIterator() {
    }

    @Test
    public void testToArray_GenericType() {
    }

    @Test
    public void testAdd() {
    }

    @Test
    public void testRemove() {
    }

    @Test
    public void testContainsAll() {
    }

    @Test
    public void testAddAll() {
    }

    @Test
    public void testRemoveAll() {
    }

    @Test
    public void testRetainAll() {
    }

    @Test
    public void testClear() {
    }

    @Test
    public void testToArray_0args() {
    }

    @Test
    public void testToCollection() {
    }

    @Test
    public void testDoBreak() {
    }

    @Test
    public void testCheckValidId() {
    }

    @Test
    public void testHashCode() {
    }

    @Test
    public void testEquals() {
    }

}
