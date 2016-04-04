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

/**
 * This class tracks the smallest values seen thus far in a stream of values.
 * This implements a single-pass selection for large data sets. That is, we have
 * a stream of input values, each of which we get to see only once. We want to
 * be able to report at any time, say after n values, the i-<i>th</i> smallest
 * value see so far.
 *
 * @author Haifeng Li
 */
public class DoubleHeapSelect {

    /**
     * The heap size.
     */
    private int k;
    /**
     * The number of objects that have been added into heap.
     */
    private int n;
    /**
     * True if the heap is fully sorted.
     */
    private boolean sorted;
    /**
     * The heap array.
     */
    private double[] heap;

    /**
     * Constructor.
     *
     * @param k the heap size.
     */
    public DoubleHeapSelect(int k) {
        this(new double[k]);
    }

    /**
     * Constructor.
     *
     * @param heap the array to store smallest values to track.
     */
    public DoubleHeapSelect(double[] heap) {
        this.heap = heap;
        k = heap.length;
        n = 0;
        sorted = false;
    }

    /**
     * Assimilate a new value from the stream.
     * @param datum
     */
    public void add(double datum) {
        sorted = false;
        if (n < k) {
            heap[n++] = datum;
            if (n == k) {
                heapify(heap);
            }
        } else {
            n++;
            if (datum < heap[0]) {
                heap[0] = datum;
                SortUtils.siftDown(heap, 0, k - 1);
            }
        }
    }

    /**
     * Returns the k-<i>th</i> smallest value seen so far.
     * @return
     */
    public double peek() {
        return heap[0];
    }

    /**
     * Returns the i-<i>th</i> smallest value seen so far. i = 0 returns the
     * smallest value seen, i = 1 the second largest, ..., i = k-1 the last
     * position tracked. Also, i must be less than the number of previous
     * assimilated.
     * @param i
     * @return
     */
    public double get(int i) {
        if (i > Math.min(k, n) - 1) {
            throw new IllegalArgumentException("HeapSelect i is greater than the number of data received so far.");
        }

        if (i == k - 1) {
            return heap[0];
        }

        if (!sorted) {
            sort(heap, Math.min(k, n));
            sorted = true;
        }

        return heap[k - 1 - i];
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

    /**
     * Place the array in max-heap order. Note that the array is not fully
     * sorted.
     */
    private static void heapify(double[] arr) {
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--) {
            SortUtils.siftDown(arr, i, n - 1);
        }
    }

    /**
     * Sorts the specified array into descending order. It is based on Shell
     * sort, which is very efficient because the array is almost sorted by
     * heapifying.
     */
    private static void sort(double[] a, int n) {
        int inc = 1;
        do {
            inc *= 3;
            inc++;
        } while (inc <= n);

        do {
            inc /= 3;
            for (int i = inc; i < n; i++) {
                double v = a[i];
                int j = i;
                while (a[j - inc] < v) {
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
}
