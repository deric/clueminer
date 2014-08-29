package org.clueminer.algorithm;

import org.clueminer.math.Numeric;

/**
 * Does not look for exact match but only closes value to toFind
 *
 * @author Tomas Barton
 */
public class InterpolationSearch {

    public static int search(Numeric[] sortedArray, double toFind) {
        return search(sortedArray, 0, sortedArray.length - 1, toFind);
    }

    /**
     * Search for number closes to toFind
     *
     * @param a
     * @param l lower index in array to start from
     * @param h upper index limit in array
     * @param x
     * @return index of closest number in sortedArray
     */
    public static int search(Numeric[] a, int l, int h, double x) {
        int low = l;
        int high = h;
        if (low < 0) {
            low = 0;
        }
        if (high >= a.length) {
            high = a.length - 1;
        }
        int mid;
        while (a[low].compareTo(x) < 0 && a[high].compareTo(x) >= 0) {
            mid = (int) (low + Math.ceil(((x - a[low].getValue()) * (high - low)) / (a[high].getValue() - a[low].getValue())));
            if (a[mid].getValue() < x) {
                low = mid + 1;
            } else if (a[mid].getValue() > x) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        if (Math.abs(x - a[low].getValue()) < Math.abs(a[high].getValue() - x)) {
            return low;
        }
        return high;
    }
}
