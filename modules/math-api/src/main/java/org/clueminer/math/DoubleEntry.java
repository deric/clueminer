package org.clueminer.math;

/**
 * An object that represents an index that has an associated {@code double}
 * value. This class is intended to support other classes that provide iterator
 * access over their indexable values without needing to incur auto-boxing
 * overhead.
 */
public interface DoubleEntry {

    /**
     * Returns the index position of this entry.
     */
    public int index();

    /**
     * Returns the value at this entry's index.
     */
    public double value();
}
