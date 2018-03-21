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
package org.clueminer.eval.utils;

import java.util.Collection;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class MatchingTest {

    private Matching subject;

    public MatchingTest() {
    }

    @Before
    public void setUp() {
        subject = new Matching();
        subject.put("foo", "bar");
    }

    @Test
    public void testSize() {
        assertEquals(1, subject.size());
    }

    @Test
    public void testGet() {
        assertEquals("bar", subject.get("foo"));
    }

    @Test
    public void testValues() {
        Collection res = subject.values();
        assertEquals(1, res.size());
    }

    @Test
    public void testContainsKey() {
        assertEquals(true, subject.containsKey("foo"));
    }

    @Test
    public void testPut() {
        subject.put("xxx", "cluster 1");
        assertEquals(2, subject.size());
    }

    @Test
    public void testGetByCluster() {
        subject.put("class x", "bar");
        assertEquals("[class x, foo]", subject.getByCluster("bar"));
    }

}
