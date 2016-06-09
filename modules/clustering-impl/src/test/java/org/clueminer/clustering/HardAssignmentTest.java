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
package org.clueminer.clustering;

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
public class HardAssignmentTest {

    private HardAssignment subject;

    public HardAssignmentTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        subject = new HardAssignment();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testMembership() {
        assertEquals(0, subject.membership().length);
    }

    @Test
    public void testAssign() {
        subject.assign(1, 0);
        subject.assign(2, 1);
        subject.assign(3, 2);
        subject.assign(5, 1);

        assertEquals(8, subject.size());
        assertEquals(3, subject.distinct());
    }

    /**
     * Test of length method, of class HardAssignment.
     */
    @Test
    public void testLength() {
        assertEquals(0, subject.size());

        subject = new HardAssignment(5);
        assertEquals(5, subject.size());
    }

    /**
     * Test of distinct method, of class HardAssignment.
     */
    @Test
    public void testDistinct() {
        assertEquals(0, subject.distinct());

        subject = new HardAssignment(new int[]{1, 3, 5, 8});
        assertEquals(4, subject.size());
        assertEquals(4, subject.distinct());

    }

    @Test
    public void testAssigned() {
        subject.assign(0, 5);

        assertEquals(5, subject.assigned(0));
    }

}
