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

import java.util.Random;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class HeapSelectTest {

    private static final double delta = 1e-9;

    public HeapSelectTest() {
    }

    @Test
    public void testSort() {
        HeapSelect<Integer> heap = new HeapSelect<>(new Integer[10]);
        int size = 10;
        for (int i = 0; i < size; i++) {
            heap.add(10 - i);
        }
        heap.sort();

        //sorted values from 1 to 10
        for (int i = 0; i < size; i++) {
            assertEquals(true, (i + 1) == heap.get(i));
        }
        assertEquals(10.0, heap.peekLast(), delta);
    }

    @Test
    public void testSelect() {
        HeapSelect<Integer> instance = new HeapSelect<>(new Integer[10]);
        for (int i = 0; i < 1000; i++) {
            instance.add(i);
            if (i > 10) {
                for (int j = 0; j < 10; j++) {
                    assertEquals(instance.get(j), Integer.valueOf(j));
                }
            }
        }

        instance = new HeapSelect<>(new Integer[10]);
        for (int i = 0; i < 1000; i++) {
            instance.add(1000 - i);
            if (i >= 9) {
                for (int j = 0; j < 10; j++) {
                    assertEquals(instance.get(j), Integer.valueOf(1000 - i + j));
                }
            }
        }
    }

    /**
     * Test of get method, of class HeapSelect.
     */
    @Test
    public void testSelectBig() {
        System.out.println("HeapSelect Big");
        HeapSelect<Double> instance = new HeapSelect<>(new Double[10]);
        Random rand = new Random();
        for (int i = 0; i < 1000000; i++) {
            instance.add(rand.nextDouble());
        }

        for (int j = 0; j < 10; j++) {
            System.out.println(instance.get(j));
        }
    }

}
