package org.clueminer.math;

/**
 * A wrapper for array of whatever type of numbers. We want to support both
 * primitive and non-primitive types
 *
 * @author Tomas Barton
 */
public interface NumericBox {

    /**
     * Get ith number
     *
     * @param index
     * @return value on index position
     */
    public double get(int index);

    /**
     *
     * @return size of an array
     */
    public int size();

}
