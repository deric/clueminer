package org.clueminer.math.impl;

import org.clueminer.math.DoubleEntry;

/**
 * A mutable {@code DoubleEntry} implementation.
 */
public class DoubleEntryImpl implements DoubleEntry {

    public int index;
    public double val;

    public DoubleEntryImpl(int index, int val) {
        this.index = index;
        this.val = val;
    }

    /**
     * {@inheritDoc}
     */
    public int index() {
        return index;
    }

    /**
     * {@inheritDoc}
     */
    public double value() {
        return val;
    }

    public String toString() {
        return "[" + index + "->" + val + "]";
    }
}