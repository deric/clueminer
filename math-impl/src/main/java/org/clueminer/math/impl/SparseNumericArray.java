package org.clueminer.math.impl;

/**
 * A interface for array-like classes that use a sparse internal representation
 * for numeric values.  This interface exposes additional methods for
 * manipulating the array values, which may offer performance improvements when
 * compound operations are done in the internal reprentation.  The exact details
 * of performance are specified by the implementation themselves.<p>
 *
 * In addition, it is suggested that instances that wrap Java primitive-type
 * arrays provide additional methods that operate directly on the primitive
 * types rather than their object equivalents.  This allows users to save any
 * performance costs from auto-boxing the primitives.
 */
public interface SparseNumericArray<T extends Number> extends SparseArray<T> {

    /**
     * Adds the specified value to the value at the index and stores the result
     * (just as {@code array[index] += delta}).  Note that this can be used with
     * negative {@code delta} values to achieve equivalent {@code -=}
     * functionality.
     *
     * @param index the position in the array
     * @param delta the change in value at the index
     *
     * @return the new value stored at the index
     */
    T add(int index, T delta);

    /**
     * Multiplies the value to the index by the provided value and saves the
     * result at the index (just as {@code array[index] *= value})
     *
     * @param index the position in the array
     * @param delta the change in value at the index
     *
     * @return the new value stored at the index
     */
    T multiply(int index, T value);

    /**
     * Divides the specified value to the index by the provided value and stores
     * the result at the index (just as {@code array[index] /= value})
     *
     * @param index the position in the array
     * @param delta the change in value at the index
     *
     * @return the new value stored at the index
     */
    T divide(int index, T value);

}