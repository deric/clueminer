/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.clustering.aggl;

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
public class ElementTest {

    private Element subject;
    private static double delta = 1e-9;
    public ElementTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        subject = new Element(2.0, 1, 1);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of compareTo method, of class Element.
     */
    @Test
    public void testCompareTo() {
        Element e1 = new Element(1.0, 0, 0);
        Element e2 = new Element(1.0, 0, 1);
        //should be identical
        assertEquals(0, e1.compareTo(e2));

        e2 = new Element(3.0, 0, 1);
        //e2 is bigger
        assertEquals(-1, e1.compareTo(e2));
    }

    /**
     * Test of getValue method, of class Element.
     */
    @Test
    public void testGetValue() {
        assertEquals(2.0, subject.getValue(), delta);
    }

    /**
     * Test of setValue method, of class Element.
     */
    @Test
    public void testSetValue() {
        subject.setValue(5.0);
        assertEquals(5.0, subject.getValue(), delta);
    }

    /**
     * Test of getRow method, of class Element.
     */
    @Test
    public void testGetRow() {
        assertEquals(1, subject.getRow());
    }

    /**
     * Test of setRow method, of class Element.
     */
    @Test
    public void testSetRow() {
        subject.setRow(5);
        assertEquals(5, subject.getRow());
    }

    /**
     * Test of getColumn method, of class Element.
     */
    @Test
    public void testGetColumn() {
        assertEquals(1, subject.getColumn());
    }

    /**
     * Test of setColumn method, of class Element.
     */
    @Test
    public void testSetColumn() {
        subject.setColumn(8);
        assertEquals(8, subject.getColumn());
    }

}
