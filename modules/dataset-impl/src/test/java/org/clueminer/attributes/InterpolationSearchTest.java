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
package org.clueminer.attributes;

import org.clueminer.algorithm.InterpolationSearch;
import org.clueminer.math.Numeric;
import org.junit.*;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Tomas Barton
 */
public class InterpolationSearchTest {

    public InterpolationSearchTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of search method, of class InterpolationSearch.
     */
    @Test
    public void testSearch() {
        Numeric[] sortedArray = (Numeric[]) new TimePointAttribute[5];
        sortedArray[0] = (Numeric) new TimePointAttribute(0, 1, 1.5F);
        sortedArray[1] = (Numeric) new TimePointAttribute(1, 2, 2F);
        sortedArray[2] = (Numeric) new TimePointAttribute(2, 3, 5F);
        sortedArray[3] = (Numeric) new TimePointAttribute(3, 4, 10F);
        sortedArray[4] = (Numeric) new TimePointAttribute(4, 5, 15F);

        double toFind = 8.0F;
        int expResult = 3; //10
        int result = InterpolationSearch.search(sortedArray, toFind);
        assertEquals(expResult, result);

        result = InterpolationSearch.search(sortedArray, 1F);
        assertEquals(0, result);

        result = InterpolationSearch.search(sortedArray, 16F);
        assertEquals(4, result);

        result = InterpolationSearch.search(sortedArray, 1.5d);
        assertEquals(0, result);
    }

}