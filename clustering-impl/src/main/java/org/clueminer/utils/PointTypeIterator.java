package org.clueminer.utils;

import java.util.Iterator;

/**
 * Used for plotting gnuplot charts
 *
 * @author Tomas Barton
 */
public class PointTypeIterator implements Iterator {

    private int current = 0;
    private static int[] types = {7, 5, 13, 22, 9, 11, 3, 16, 18, 24, 1, 2, 4, 6};

    @Override
    public boolean hasNext() {
        if (current < types.length) {
            return true;
        }
        return false;
    }

    @Override
    public Object next() {
        if (current == types.length) {
            current = 0;
        }
        return types[current++];
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
