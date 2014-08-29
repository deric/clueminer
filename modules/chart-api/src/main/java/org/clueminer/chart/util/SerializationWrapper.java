package org.clueminer.chart.util;

import java.io.Serializable;

/**
 * Interface for classes used for wrapping non-serializable classes.
 * The wrapper can be unwrapped for deserialization.
 *
 * @param <T> Class that the wrapper can handle.
 */
public interface SerializationWrapper<T> extends Serializable {

    /**
     * Creates a new instance of the wrapped class using the data from the
     * wrapper. This can be used for deserialization.
     *
     * @return A new instance of the wrapped class {@code T}.
     */
    T unwrap();
}
