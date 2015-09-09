/*
 * Copyright (C) 2011-2015 clueminer.org
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Minimum heap implementation. See [Cormen et al 1999] for formal theory.
 Maintains all elements in a min-heap, such that the minimum element will
 be the peek-most node in the heap at all times. Among many other uses, heaps
 are ideal for
 representing priority queues.
 *
 * @param <T>
 */
public class Heap<T> implements Iterable<T> {

    private int size;
    final private ArrayList<T> heap;
    final private Comparator<T> comparator;

    /**
     * Create a new heap
     *
     * @param comparator A comparator that handles elements of type T
     */
    public Heap(Comparator<T> comparator) {
        size = 0;
        //Allocate space
        heap = new ArrayList<>();

        //Comparator
        this.comparator = comparator;
    }

    /**
     * Insert element into the heap. O(lg n) where n is the number of
     * elements/nodes in the heap
     *
     * @param element new element to be inserted
     */
    public void add(final T element) {
        size++;
        heap.add(element);
        decreaseKey(size - 1);
        //return node;
    }

    public final void clear() {
        heap.clear();
        size = 0;
    }

    /**
     * Return a reference to the peek-most element on the heap. The method does
     * not change the state
     * of the heap in any way. O(k).
     *
     * @return Reference to peek-most element of heap
     */
    public final T peek() {
        return heap.get(0);
    }

    //bound check missing
    /**
     * Pop an element of the heap. O(lg n) where n is the number of elements in
     * heap.
     *
     * @return
     */
    public T pop() {
        T returnNode = peek();
        swap(0, size - 1);
        heap.remove(size - 1);
        size--;

        //if any elements left in heap, do minHeapify
        if (size > 0) {
            minHeapify(0);
        }

        return returnNode;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public T get(int index) {
        return heap.get(index);
    }

    public final int size() {
        return size;
    }

    private boolean decreaseKey(int index) {
        boolean modified = false;

        //    while ( index>0 &&  (heap[parent(index)]).compareTo( heap[index]) >= 0 ) {
        while (index > 0 && comparator.compare(heap.get(parent(index)), heap.get(index)) >= 0) {
            swap(index, parent(index));
            index = parent(index);
            modified = true;
        }

        return modified;
    }

    private void minHeapify(final int index) {
        int smallest;
        int left = left(index);
        int right = right(index);

        //  if (left<size && (heap[left]).compareTo(heap[index]) <= 0 )
        if (left < size && comparator.compare(heap.get(left), heap.get(index)) <= 0) {
            smallest = left;
        } else {
            smallest = index;
        }

        //    if (right<size && (heap[right]).compareTo(heap[smallest]) <=0 )
        if (right < size && comparator.compare(heap.get(right), heap.get(smallest)) <= 0) {
            smallest = right;
        }
        if (smallest != index) {
            swap(index, smallest);
            minHeapify(smallest);
        }
    }

    private void swap(final int index, final int index2) {
        T temp = heap.get(index);

        heap.set(index, heap.get(index2));
        heap.set(index2, temp);
    }

    /**
     * Index divided by 2
     *
     * @param i
     * @return
     */
    private int parent(final int i) {
        return i >>> 1;
    }

    private int left(final int i) {
        return 2 * i;
    }

    private int right(final int i) {
        return 2 * i + 1;
    }

    /**
     * Returns an iterator that iterates over all elements of the heap, in no
     * particular order
     *
     * @return
     */
    @Override
    public final Iterator<T> iterator() {
        return heap.iterator();
    }

    public void printHeap() {
        int step = 1;
        int i = 0;
        for (int n = 0; n < size; n++) {
            i++;
            System.out.print("" + heap.get(n) + "*");
            if (i % step == 0) {
                step *= 2;
                i = 0;
                System.out.println("");
            }
        }

        System.out.println("");
    }
}
