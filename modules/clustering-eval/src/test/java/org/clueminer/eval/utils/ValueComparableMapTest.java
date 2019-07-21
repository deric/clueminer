/*
 * Copyright (C) 2011-2019 clueminer.org
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

import com.google.common.collect.Ordering;
import java.util.TreeMap;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author tombart
 */
public class ValueComparableMapTest {

    /**
     * Test of put method, of class ValueComparableMap.
     */
    @Test
    public void testPut() {
        TreeMap<String, Integer> map = new ValueComparableMap<>(Ordering.natural());
        map.put("a", 5);
        map.put("b", 1);
        map.put("c", 3);
        assertEquals("b", map.firstKey());
        assertEquals("a", map.lastKey());
        map.put("d", 0);
        assertEquals("d", map.firstKey());
        //ensure it's still a map (by overwriting a key, but with a new value)
        map.put("d", 2);
        assertEquals("b", map.firstKey());
        //Ensure multiple values do not clobber keys
        map.put("e", 2);
        assertEquals(5, map.size());
        assertEquals(2, (int) map.get("e"));
        assertEquals(2, (int) map.get("d"));
        System.out.println("map :" + map);

    }
}
