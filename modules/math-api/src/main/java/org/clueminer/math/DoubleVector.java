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
    double add(int index, double delta);

    /**
     * Returns the value of this vector at the given index.
     *
     * @param index index to retrieve.
     * @return value at index.
     */
    @Override
    double get(int index);

    /**
     * Sets the length in this vector (optional operation).
     *
     * @param index index to set.
     * @param value value to set in the vector.
     */
    void set(int index, double value);

    /**
     * Returns a double array representing this vector.  The returned array will
     * be "safe" in that no changes to the array will be reflected in the
     * vector (a deep copy), and likewise for changes to to the vector.  The caller is thus
     * free to modify the returned array.
     *
     * @return a {@code double} array of this vector.
     */
    double[] toArray();
}
