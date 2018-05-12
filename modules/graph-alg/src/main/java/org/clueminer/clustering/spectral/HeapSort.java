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
package org.clueminer.clustering.spectral;

import org.clueminer.neighbor.Neighbor;

/**
 * https://www.geeksforgeeks.org/heap-sort/
 *
 * @author mikusmi1
 */
public class HeapSort {

    public static void sort(Neighbor<Integer> arr[]) {
        int n = arr.length;

        // Build heap (rearrange array)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        // One by one extract an element from heap
        for (int i = n - 1; i >= 0; i--) {
            // Move current root to end
            double tempDist = arr[0].distance;
            int tempIndex = arr[0].index;
            int tempKey = arr[0].key;

            // Swap
            arr[0] = new Neighbor<>(arr[i].key, arr[i].index, arr[i].distance);
            arr[i].distance = tempDist;
            arr[i].index = tempIndex;
            arr[i].key = tempKey;

            // call max heapify on the reduced heap
            heapify(arr, i, 0);
        }
    }

    // To heapify a subtree rooted with node i which is
    // an index in arr[]. n is size of heap
    private static void heapify(Neighbor<Integer> arr[], int n, int i) {
        int largest = i;  // Initialize largest as root
        int l = 2 * i + 1;  // left = 2*i + 1
        int r = 2 * i + 2;  // right = 2*i + 2

        // If left child is larger than root
        if (l < n && arr[l].distance > arr[largest].distance) {
            largest = l;
        }

        // If right child is larger than largest so far
        if (r < n && arr[r].distance > arr[largest].distance) {
            largest = r;
        }

        // If largest is not root
        if (largest != i) {
            double tempDist = arr[i].distance;
            int tempIndex = arr[i].index;
            int tempKey = arr[i].key;

            // Swap
            arr[i] = new Neighbor<>(arr[largest].key, arr[largest].index, arr[largest].distance);
            arr[largest].distance = tempDist;
            arr[largest].index = tempIndex;
            arr[largest].key = tempKey;

            // Recursively heapify the affected sub-tree
            heapify(arr, n, largest);
        }
    }
}
