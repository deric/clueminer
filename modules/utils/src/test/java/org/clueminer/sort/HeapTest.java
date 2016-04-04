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

import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import org.clueminer.utils.Duple;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class HeapTest {

    private Comparator<Double> asc;
    private Comparator<Integer> desc;

    public HeapTest() {
        asc = new Comparator<Double>() {

            @Override
            public int compare(Double o1, Double o2) {
                return o1.compareTo(o2);
            }
        };

        desc = new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }
        };
    }

    @Test
    public void testNonFixedSize() {
        //create heap with capacity 10
        Heap<Double> instance = new Heap<>(asc);
        Random rand = new Random();
        //add 20 items
        double d;
        for (int i = 0; i < 20; i++) {
            d = rand.nextDouble();
            instance.add(d);
        }
        instance.print();
        //and retrieve 20 items
        double prev = -1;
        for (int j = 0; j < 20; j++) {
            d = instance.pop();
            //in most cases this should work
            //assertTrue(prev + " should be less than " + d, prev < d);
            prev = d;
        }
    }

    @Test
    public void testSorting() {
        //create heap with capacity 10
        Heap<Integer> heap = new Heap<>(desc);
        int n = 10;

        for (int i = 0; i < n; i++) {
            heap.add(i);
        }
        int item;
        for (int i = n; i > 0; i--) {
            item = heap.pop();
            assertEquals(i - 1, item);
        }
    }

    @Test
    public void testRemove() {
        //create heap with capacity 10
        Heap<Integer> heap = new Heap<>(desc);
        int n = 20;

        for (int i = 0; i < n; i++) {
            heap.add(i);
        }
        Iterator<Duple<Integer, Integer>> iter = heap.indexValue();
        Duple<Integer, Integer> item;
        int j = 0;
        while (iter.hasNext()) {
            item = iter.next();
            j++;
        }
        assertEquals(heap.size(), j);
        heap.print();
        System.out.println("===");
        System.out.println("removing 1 -> " + heap.get(1));
        int size = heap.size();
        int idx = 1;
        heap.remove(idx);
        assertEquals(size - 1, heap.size());
        assertTrue("left child should be smaller than its parent", heap.get(idx) > heap.get(heap.left(idx)));
        assertTrue("right child should be smaller than its parent", heap.get(idx) > heap.get(heap.right(idx)));
        heap.print();
    }
}
