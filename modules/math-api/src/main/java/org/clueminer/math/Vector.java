/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    boolean equals(Object o);

    /**
     * Return the value of the vector at the given index as a generic
     * type{@code T}.
     *
     * @param index index to retrieve.
     * @return value at index.
     */
    T getValue(int index);

    /**
     * Return double representation of the value. Most of computations are done
     * with double precision, so this is act a default getter method. Use method
     * {@code getValue} for getting type used for storing numbers.
     *
     * @param index in vector starting from 0
     * @return value at given position
     */
    double get(int index);

    /**
     * Returns the hash code as the sum of the vectors elements, normalized to
     * an {@code int}.
     *
     * @return
     */
    @Override
    int hashCode();

    /**
     * Return the size of the {@code Vector}.
     *
     * @return size of the vector.
     */
    int size();

    /**
     * Returns the magnitude of this vector
     *
     * @return
     */
    double magnitude();

    /**
     * Computes dot product of this and another vector
     *
     * @param v the other vector
     * @return the dot product of this vector and another
     */
    double dot(Vector v);

    /**
     * Return p-Norm
     *
     * @param p
     * @return
     */
    double pNorm(double p);

    /**
     * Set the value in the vector (optional operation).
     *
     * @param index index to set.
     * @param value value to set in the vector.
     */
    void set(int index, Number value);

    /**
     * Adds other to this and return a new instance of Vector with the result
     *
     * @param other
     * @return
     */
    Vector<T> add(Vector<T> other);

    /**
     * Adds scalar num to each member of the Vector and returns new Vector
     * instance
     *
     * @param num
     * @return
     */
    Vector<T> add(double num);

    /**
     * Subtract scalar num form each member of this Vector and return a new
     * instance of Vector
     *
     * @param num
     * @return
     */
    Vector<T> minus(double num);

    /**
     * Subtract other from this vector
     *
     * @param other
     * @return
     */
    Vector<T> minus(Vector<T> other);

    /**
     * Multiply vector by a scalar number
     *
     * @param scalar
     * @return
     */
    Vector<T> times(double scalar);

    /**
     * Will return the same underlying Vector structure without any values.
     *
     * @return empty Vector structure with the same length.
     */
    Vector<T> duplicate();

}
