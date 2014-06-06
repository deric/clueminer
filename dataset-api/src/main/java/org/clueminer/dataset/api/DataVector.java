package org.clueminer.dataset.api;

import java.util.Iterator;

/**
 *
 * @author Tomas Barton
 */
public interface DataVector {

    /**
     * Human readable name, if any
     *
     * @return string identification
     */
    String getName();

    /**
     *
     * @return length of the vector
     */
    int size();

    /**
     *
     * @return iterator over all elements
     */
    Iterator<? extends Object> values();

    /**
     * Id of row/column
     *
     * @return non-negative index starting from 0
     */
    int getIndex();
}
