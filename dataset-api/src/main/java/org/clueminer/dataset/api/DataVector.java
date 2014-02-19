package org.clueminer.dataset.api;

import java.util.Iterator;

/**
 *
 * @author Tomas Barton
 */
public interface DataVector {

    /**
     *
     * @return length of the vector
     */
    public int size();

    /**
     *
     * @return iterator over all elements
     */
    public Iterator<? extends Object> values();
}
