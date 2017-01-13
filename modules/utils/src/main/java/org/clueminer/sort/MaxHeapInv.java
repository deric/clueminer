/*
 * Copyright (C) 2011-2017 clueminer.org
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
import org.openide.util.Exceptions;

/**
 * A heap build on top of passed array, it keeps limited number of items (k).
 * After sorting data in passed array will be in incremental (ascending) order.
 *
 * @TODO: implementation is NOT correct
 * @author deric
 * @param <T> type being sorted
 */
public class MaxHeapInv<T extends Comparable<? super T>> extends BaseHeap<T> {

    /**
     * The heap size.
     */
    private final int k;
    /**
     * The number of objects that have been added into heap.
     */
    private int n;
    /**
     * True if the heap is fully sorted.
     */
    private boolean sorted;

    /**
     * Constructor.
     *
     * @param heap the array to store smallest values to track.
     */
    public MaxHeapInv(T[] heap) {
        this.heap = heap;
        k = heap.length;
        n = 0;
        sorted = false;
    }

    /**
     * Assimilate a new value from the stream.
     *
     * @param datum
     */
    public void add(T datum) {
        sorted = false;
        if (n < k) {
            heap[n++] = datum;
            if (n == k) {
                //when heap is full, ensure that heap condition holds
                heapify(heap);
            }
        } else {
            n++;
            //positon k-1 contains largest element
            if (datum.compareTo(heap[k - 1]) < 0) {
                heap[k - 1] = datum;
                siftDown(heap, k - 1, 0);
            }
        }
    }

    /**
     * To restore the max-heap condition when a node's priority is decreased. We
     * move down the heap, exchanging the node at position k with the larger of
     * that node's two children if necessary and stopping when the node at k is
     * not smaller than either child or the bottom is reached. Note that if n is
     * even and k is n/2, then the node at k has only one child -- this case
     * must be treated properly.
     *
     * @param <T>
     * @param arr
     * @param i
     * @param n
     * @return true when heap was modified
     */
    public <T extends Comparable<? super T>> boolean siftDown(T[] arr, int i, int n) {
        int l, r, j;
        j = i;
        boolean changes = false;
        while (i >= n) {
            l = left(i);
            r = right(i);

            //select larger childern
            if (l >= n) {
                j = l;
                if (r >= n && arr[l].compareTo(arr[r]) < 0) {
                    j = r;
                }
            } else if (r >= n) {
                j = r;
            }

            //avoid endless loop
            if (i == j) {
                return false;
            }
            //check heap condition is broken (parent is smaller)
            if (arr[i].compareTo(arr[j]) < 0) {
                SortUtils.swap(arr, i, j);
                changes = true;
            }
            i = j;
        }
        return changes;
    }

    /**
     * Place the array in max-heap order. Note that the array is not fully
     * sorted.
     */
    private <T extends Comparable<? super T>> void heapify(T[] arr) {
        for (int i = k / 2; i < k; i++) {
            siftDown(arr, i, 0);
        }
    }

    /**
     * In case of avoiding creating new objects frequently, one may check and
     * update the peek object directly and call this method to sort the internal
     * array in heap order.
     */
    public void heapify() {
        if (n < k) {
            throw new IllegalStateException();
        }

        heapify(heap);
        siftDown(heap, k - 1, 0);
    }

    /**
     * Returns the k-<i>th</i> smallest value seen so far.
     *
     * @return
     */
    public T peekLast() {
        return heap[0];
    }

    /**
     * Return largest value in the heap
     *
     * @return
     */
    public T peek() {
        return heap[k - 1];
    }

    /**
     * Returns the i-<i>th</i> smallest value seen so far. i = 0 returns the
     * smallest value seen, i = 1 the second largest, ..., i = k-1 the last
     * position tracked. Also, i must be less than the number of previous
     * assimilated.
     *
     * @param i
     * @return
     */
    @Override
    public T get(int i) {
        if (i > Math.min(k, n) - 1 || i < 0) {
            throw new IllegalArgumentException("HeapSelect +" + i + "+ is greater than the number of data received so far.");
        }

        if (!sorted) {
            sort(heap, Math.min(k, n));
            sorted = true;
        }

        return heap[i];
    }

    public void set(int i, T value) {
        heap[i] = value;
    }

    /**
     * Sort the smallest values.
     */
    public void sort() {
        if (!sorted) {
            sort(heap, Math.min(k, n));
            sorted = true;
        }
    }

    @Override
    public int size() {
        return k;
    }

    /**
     * Sorts the specified array into ascending order. It is based on Shell
     * sort, which is very efficient because the array is almost sorted by
     * heapifying.
     */
    private static <T extends Comparable<? super T>> void sort(T[] a, int n) {
        int inc = 1;
        do {
            inc *= 3;
            inc++;
        } while (inc <= n);

        do {
            inc /= 3;
            for (int i = inc; i < n; i++) {
                T v = a[i];
                int j = i;
                while (a[j - inc].compareTo(v) > 0) {
                    a[j] = a[j - inc];
                    j -= inc;
                    if (j < inc) {
                        break;
                    }
                }
                a[j] = v;
            }
        } while (inc > 1);
    }

    /**
     * Avoid calling getter, cause it causes sorting
     *
     * @param out
     * @param index
     * @throws IOException
     */
    @Override
    protected void printNodeValue(OutputStreamWriter out, int index) throws IOException {
        out.write("#" + index + " (" + heap[index] + ")");
        out.write('\n');
    }

    public void print() {
        try {
            OutputStreamWriter out = new OutputStreamWriter(System.out);
            printTree(out, k - 1);
            out.flush();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public int left(final int i) {
        int j = k - i;
        return k - (2 * j);
    }

    @Override
    public int right(final int i) {
        int j = k - i;
        return k - (2 * j + 1);
    }

}
