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
package org.clueminer.sort;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import org.clueminer.utils.Duple;
import org.openide.util.Exceptions;

/**
 * Minimum heap implementation. See [Cormen et al 1999] for formal theory.
 * Maintains all elements in a min-heap, such that the minimum element will be
 * the peek-most node in the heap at all times. Among many other uses, heaps are
 * ideal for representing priority queues.
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
     * Create heap with given capacity
     *
     * @param comparator
     * @param capacity   expected capacity (might be eventually exceeded)
     */
    public Heap(Comparator<T> comparator, int capacity) {
        size = 0;
        //Allocate space
        heap = new ArrayList<>(capacity);

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
     * not change the state of the heap in any way. O(k).
     *
     * @return Reference to peek-most element of heap
     */
    public final T peek() {
        return heap.get(0);
    }

    public final T peekLast() {
        if (size() > 0) {
            return heap.get(size() - 1);
        }
        return null;
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

    /**
     * Value at given index. Root is at position 0
     *
     * @param index
     * @return value stored in heap at given index
     */
    public T get(int index) {
        return heap.get(index);
    }

    /**
     * Actual number of item on the heap
     *
     * @return
     */
    public final int size() {
        return size;
    }

    private boolean decreaseKey(int index) {
        boolean modified = false;

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

        if (left < size && comparator.compare(heap.get(left), heap.get(index)) <= 0) {
            smallest = left;
        } else {
            smallest = index;
        }

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
     * Remove item at given internal heap index. Removed value is replaced by
     * last value in the heap, which is then sifted down.
     *
     * Complexity of removal is O(log n)
     *
     * @param index
     * @return item on given index, null if item doesn't exist
     */
    public T remove(int index) {
        if (index >= size) {
            return null;
        }
        T item = heap.get(index);
        swap(index, size - 1);
        heap.remove(size - 1);
        size--;

        //if any elements left in heap, do minHeapify
        if (size > 0) {
            minHeapify(index);
        }
        return item;
    }

    /**
     * Index divided by 2
     *
     * @param i
     * @return
     */
    int parent(final int i) {
        return i >>> 1;
    }

    int left(final int i) {
        return 2 * i + 1;
    }

    int right(final int i) {
        return 2 * i + 2;
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

    public final Iterator<Duple<Integer, T>> indexValue() {
        return new HeapIndexValueIterator<>();

    }

    private class HeapIndexValueIterator<T> implements Iterator<Duple<Integer, T>> {

        private int index;

        public HeapIndexValueIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public Duple<Integer, T> next() {
            int idx = index++;
            return new Duple<>(idx, (T) heap.get(idx));
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove using iterator.");
        }

    }

    public void print() {
        try {
            OutputStreamWriter out = new OutputStreamWriter(System.out);
            printTree(out, 0);
            out.flush();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void printTree(OutputStreamWriter out, int index) throws IOException {
        if (index >= size) {
            return;
        }
        printTree(out, left(index), false, "");
        printNodeValue(out, index);
        printTree(out, right(index), true, "");
    }

    protected void printNodeValue(OutputStreamWriter out, int index) throws IOException {
        out.write("#" + index + " (" + heap.get(index) + ")");
        out.write('\n');
    }

    public void printTree(OutputStreamWriter out, int index, boolean isRight, String indent) throws IOException {
        if (index >= size) {
            return;
        }
        printTree(out, left(index), false, indent + (isRight ? " |      " : "        "));

        out.write(indent);
        if (isRight) {
            out.write(" \\");
        } else {
            out.write(" /");
        }
        out.write("----- ");
        printNodeValue(out, index);
        printTree(out, right(index), true, indent + (isRight ? "        " : " |      "));
    }

}
