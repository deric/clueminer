package org.clueminer.algorithm;

import org.clueminer.math.Numeric;


/**
 * Binary search is probably the fastest algorithm for searching in ordered
 * array. When searched value is out of interval in given array, a value
 * from start or end of array is returned.
 * 
 * @author Tomas Barton
 */
public class BinarySearch {

    public static int search(Numeric[] sortedArray, double x) {
        return search(sortedArray, 0, sortedArray.length - 1, x);
    }

    public static int search(Numeric[] a, int low, int high, double x) {
        // x < a[low] - trivial case searched value is out of array's lower bound
        if (a[low].compareTo(x) > 0) {
            return low;
        }
        
        // x > a[high] - another trivial case, searched value is bigger than
        // highest number in array
        if (a[high].compareTo(x) <= 0) {
            return high;
        }

        int tmp;
        if (low > high) {
            tmp = low;
            low = high;
            high = tmp;
        }
        if (low < 0) {
            low = 0;
        }
        if (high >= a.length) {
            high = a.length - 1;
        }
        int mid;
        while (low <= high) {
            /**
             * this is quite an important step, which speeds up this
             * implementation. division by 2 is done very quickly
             * even though is repeated many times, the overall
             * performance is still very good
             */
            mid = (low + high) >>> 1; //equal to division by 2
            if (a[mid].compareTo(x) < 0) {
                low = mid + 1;
            } else if (a[mid].compareTo(x) > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }

        if (Math.abs(x - a[low].getValue()) <= Math.abs(a[high].getValue() - x)) {
            return low;
        }
        return high;
    }
}
