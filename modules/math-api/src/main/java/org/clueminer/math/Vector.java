package org.clueminer.math;

/**
 * An generalized interface for vectors. This interface allows implementations
 * to implement the vector with any kind of underlying numerical data type.
 *
 * @author Tomas Barton
 * @param <T>
 */
public interface Vector<T extends Number> {

    /**
     * Returns {@code true} if the object is a {@link Vector} of the same size
     * and whose corresponding indices have equivalent values.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o);

    /**
     * Return the value of the vector at the given index as a generic
     * type{@code T}.
     *
     * @param index index to retrieve.
     * @return value at index.
     */
    public T getValue(int index);

    /**
     * Return double representation of the value. Most of computations are done
     * with double precision, so this is act a default getter method. Use method
     * {@code getValue} for getting type used for storing numbers.
     *
     * @param index in vector starting from 0
     * @return value at given position
     */
    public double get(int index);

    /**
     * Returns the hash code as the sum of the vectors elements, normalized to
     * an {@code int}.
     *
     * @return
     */
    @Override
    public int hashCode();

    /**
     * Return the size of the {@code Vector}.
     *
     * @return size of the vector.
     */
    public int size();

    /**
     * Returns the magnitude of this vector
     *
     * @return
     */
    public double magnitude();

    /**
     * Computes dot product of this and another vector
     *
     * @param v the other vector
     * @return the dot product of this vector and another
     */
    public double dot(Vector v);

    /**
     * Return p-Norm
     *
     * @param p
     * @return
     */
    public double pNorm(double p);

    /**
     * Set the value in the vector (optional operation).
     *
     * @param index index to set.
     * @param value value to set in the vector.
     */
    public void set(int index, Number value);

    /**
     * Adds other to this and return a new instance of Vector with the result
     *
     * @param other
     * @return
     */
    public Vector<T> add(Vector<T> other);

    /**
     * Adds scalar num to each member of the Vector and returns new Vector
     * instance
     *
     * @param num
     * @return
     */
    public Vector<T> add(double num);

    /**
     * Subtract scalar num form each member of this Vector and return a new
     * instance of Vector
     *
     * @param num
     * @return
     */
    public Vector<T> subtract(double num);

    /**
     * Will return the same underlying Vector structure without any values.
     *
     * @return empty Vector structure with the same length.
     */
    public Vector<T> duplicate();

    /**
     * Cloned Vector must have the same hashcode and equals.
     *
     * @return identical copy of Vector
     */
    public Vector<T> clone();
}
