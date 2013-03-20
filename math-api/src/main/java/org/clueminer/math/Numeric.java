package org.clueminer.math;

/**
 * Simple interface for data type representing a value that can be compared.
 * Implementing classes should implement a problem specific logic (storing data
 * etc.)
 *
 * @author Tomas Barton
 */
public interface Numeric extends Comparable<Numeric> {

    public double getValue();

    /**
     * +1 when this is bigger than d 0 when numbers are equal -1 when this is
     * smaller than d
     *
     * @param d
     * @return -1, 0 or +1
     */
    public int compareTo(double d);
}