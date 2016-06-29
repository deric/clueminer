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

import java.util.Arrays;
import java.util.Random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class MaxHeapTest {

    private static final double DELTA = 1e-9;

    @Test
    public void testSort() {
        Integer[] data = new Integer[10];
        MaxHeap<Integer> heap = new MaxHeap<>(data);
        int size = 10;
        for (int i = 0; i < size; i++) {
            heap.add(i + 1);
        }
        System.out.println("before: " + Arrays.toString(data));
        //after adding last item heapify is called anyway
        heap.print();
        heap.heapify();
        //assertEquals(1, heap.peek().intValue());
        System.out.println("heapify: " + Arrays.toString(data));
        heap.sort();

        assertEquals(1, heap.peek().intValue());
        //heap is not fully sorted
        assertEquals(10, heap.peekLast().intValue());
        //printing causes heap sorting!
        heap.print();
        System.out.println("sorted: " + Arrays.toString(data));
        arrPrint(data);

        //sorted values from 1 to 10
        for (int i = 0; i < size; i++) {
            assertEquals(i + 1, heap.get(i).intValue());
        }
    }

    private void arrPrint(Integer[] heap) {
        for (int i = 0; i < heap.length; i++) {
            System.out.println("[" + i + "] = " + heap[i]);
        }
    }

    @Test
    public void testSelect() {
        MaxHeap<Integer> instance = new MaxHeap<>(new Integer[10]);
        for (int i = 0; i < 1000; i++) {
            instance.add(i);
            if (i > 10) {
                for (int j = 0; j < 10; j++) {
                    assertEquals(instance.get(j), Integer.valueOf(j));
                }
            }
        }

        instance = new MaxHeap<>(new Integer[10]);
        for (int i = 0; i < 1000; i++) {
            instance.add(1000 - i);
            if (i >= 9) {
                for (int j = 0; j < 10; j++) {
                    assertEquals(instance.get(j), Integer.valueOf(1000 - i + j));
                }
            }
        }
    }

    @Test
    public void testSelectBig() {
        System.out.println("HeapSelect Big");
        MaxHeap<Double> instance = new MaxHeap<>(new Double[10]);
        Random rand = new Random();
        for (int i = 0; i < 1000000; i++) {
            instance.add(rand.nextDouble());
        }

        for (int j = 0; j < 9; j++) {
            assertTrue(instance.get(j) < instance.get(j + 1));
        }
    }

}
