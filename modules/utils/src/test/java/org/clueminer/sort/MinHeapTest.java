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
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class MinHeapTest {

    private static final double DELTA = 1e-9;

    @Test
    public void testSort() {
        Integer[] data = new Integer[10];
        MinHeap<Integer> heap = new MinHeap<>(data);
        int size = 10;
        for (int i = 0; i < size; i++) {
            heap.add(size - i);
        }
        heap.print();
        //TODO: heapify should guarantee that smallest element is first
        heap.heapify();
        //heap.sort();
        assertEquals(1, data[0].intValue());
        assertEquals(10, data[9].intValue());

        assertEquals(1, heap.peek().intValue());
        assertEquals(10, heap.peekLast().intValue());

        heap.print();
        arrPrint(data);
    }

    //@Test
    public void testUnSorted() {
        int size = 5;
        Integer[] data = new Integer[size];
        MinHeap<Integer> heap = new MinHeap<>(data);
        for (int i = 0; i < size; i++) {
            heap.add(999999);
        }

        heap.add(1);
        heap.add(5);
        //heap.print();
        heap.add(3);
        heap.add(8);
        heap.add(4);
        System.out.println("==== 8 should be last");
        heap.heapify();
        assertEquals(8, heap.peekLast().intValue());
        heap.print();

        heap.add(2);

        heap.sort();
        arrPrint(data);
        //smallest value should be at position 0
        assertEquals(1, data[0].intValue());
    }

    private void print(MinHeap<Integer> heap, int n) {
        for (int i = 0; i < n; i++) {
            System.out.println(i + ": " + heap.get(i));
        }
    }

    /**
     * we use heap for sorting an array
     *
     * @param heap
     */
    private void arrPrint(Integer[] heap) {
        for (int i = 0; i < heap.length; i++) {
            System.out.println("[" + i + "] = " + heap[i]);
        }
    }

    //@Test
    public void testDoubleSorted() {
        int size = 5;
        Double[] data = new Double[size];
        MinHeap<Double> heap = new MinHeap<>(data);
        for (int i = 0; i < size; i++) {
            heap.add(Double.MAX_VALUE);
        }
        heap.add(0.35);
        heap.add(1.35);
        heap.add(0.5);
        heap.add(0.1);
        heap.add(0.01);
        assertEquals(0.01, heap.peek(), DELTA);

        heap.print();
    }

    //@Test
    public void testRandom() {
        int size = 20;
        Double[] data = new Double[size];
        Random rand = new Random();
        MinHeap<Double> heap = new MinHeap<>(data);
        for (int i = 0; i < size; i++) {
            heap.add(rand.nextDouble() * 10);
        }
        heap.heapify();
        for (int i = 0; i < size; i++) {
            heap.add(rand.nextDouble() * 10);
            heap.heapify();
        }
        double prev = 0.0;
        for (double d : heap) {
            assertTrue("expect " + prev + " to be smaller than " + d, prev < d);
        }
    }

}
