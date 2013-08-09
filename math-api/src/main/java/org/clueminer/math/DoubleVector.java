package org.clueminer.math;

/**
 * An generalized interface for vectors.  This interface allows implementations
 * to implement the vector with any kind of underlying data type, but the input
 * and output data types must be doubles.
 *
 * <p>Methods which modify the state of a {@code Vector} are optional.
 * Implementations that are not modifiable should throw an {@code
 * UnsupportedOperationException} if such methods are called.  These methods are
 * marked as "optional" in the specification for the interface.
 *
 * @author Keith Stevens
 */
public interface DoubleVector extends Vector<Double> {

    /**
     * Changes the value in this vector by a specified amount (optional
     * operation).  If there is not a value set at index, delta should be set to
     * the actual value.
     *
     * @param index index to change.
     * @param delta the amount to change by.
     * @return the resulting value at the index
     */
    public double add(int index, double delta);

    /**
     * Returns the value of this vector at the given index.
     *
     * @param index index to retrieve.
     * @return value at index.
     */
    public double get(int index);

    /**
     * Returns the value of the vector at the given index as a {@code Double}.
     *
     * @param {@inheritDoc}
     * @return {@inheritDoc}
     */
    public Double getDouble(int index);

    /**
     * Sets the length in this vector (optional operation).
     *
     * @param index index to set.
     * @param value value to set in the vector.
     */
    public void set(int index, double value);

    /**
     * Returns a double array representing this vector.  The returned array will
     * be "safe" in that no changes to the array will be reflected in the
     * vector (a deep copy), and likewise for changes to to the vector.  The caller is thus
     * free to modify the returned array.
     *
     * @return a {@code double} array of this vector.
     */
    public double[] toArray();
}
