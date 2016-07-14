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
package org.clueminer.utils;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ListUtilsTest {

    private List<Integer> simpleList(int size) {
        List<Integer> list = new ArrayList();
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
        return list;
    }

    @Test
    public void testSplitList() {
        List<Integer> source = simpleList(10);
        //split list into two lists
        List<List<Integer>> res = ListUtils.splitList(source, 2);
        assertEquals(2, res.size());
        List<Integer> first = res.get(0);
        //first list starts with zero
        assertEquals(0, (int) first.get(0));
        assertEquals(4, (int) first.get(4));
        //second list starts with 5
        assertEquals(5, (int) res.get(1).get(0));
        assertEquals(9, (int) res.get(1).get(4));
    }

    @Test
    public void testSwap() {
        List<Integer> source = simpleList(2);
        ListUtils.swap(source, 0, 1);
        //swap first two items
        assertEquals(1, (int) source.get(0));
        assertEquals(0, (int) source.get(1));
    }

    @Test
    public void testCollectFutures() throws Exception {
    }

    @Test
    public void testAddRange() {
    }

    @Test
    public void testRandomSample_4args() {
    }

    @Test
    public void testRandomSample_3args() {
    }

}
