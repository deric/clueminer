/*
 * Copyright (C) 2015 clueminer.org
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
package org.clueminer.distance.api;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author deric
 */
public class DmComparatorTest {

    private DmComparator subject;

    public DmComparatorTest() {
    }

    @Before
    public void setUp() {
        Distance eucl = mock(Distance.class);
        when(eucl.compare(1.0, 5.0)).thenReturn(true);
        subject = new DmComparator(eucl);
    }

    @Test
    public void testCompare() {
        assertEquals(-1, subject.compare(1.0, 5.0));
    }

    @Test
    public void testDelta() {
        //should be considered as the same
        assertEquals(0, subject.compare(1e-10, 5e-10));
    }

    @Test
    public void testSetDelta() {
        //change delta
        subject.setDelta(1e-4);
        //should be considered as the same
        assertEquals(0, subject.compare(1e-5, 5e-5));
    }

}
