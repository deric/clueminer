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
package org.clueminer.sort;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class HeapSelectInvTest {

    private static final double DELTA = 1e-9;

    @Test
    public void testSort() {
        Integer[] data = new Integer[10];
        HeapSelectInv<Integer> heap = new HeapSelectInv<>(data);
        int size = 10;
        for (int i = 0; i < size; i++) {
            heap.add(10 - i);
        }

        heap.sort();

        //sorted values from 1 to 10
        for (int i = 0; i < size; i++) {
            assertEquals((i + 1), data[i], DELTA);
        }
        assertEquals(10.0, heap.peekLast(), DELTA);
    }

}
