/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.dataset.api;

import java.awt.Color;
import java.io.Serializable;
import org.clueminer.math.Vector;

/**
 * An instance is a usually a row of data which contains a number of attributes
 * (that is its dimension). An attribute should be possible to cast to a number.
 * This definition should be as general as possible, however computing distances
 * on nominal attributes is tricky with strongly typed language.
 *
 * @param <T>
 * @TODO In future it would be good to introduce a special interface for string
 * attributes.
 *
 * @author Tomas Barton
 */
public interface Instance<T extends Number> extends Cloneable, Serializable, Vector<T>, DataVector {

    /**
     * ID might contain any characters
     *
     * @return unique identification at least in the dataset
     */
    String getId();

    void setId(String id);

    String getName();

    void setName(String name);

    /**
     *
     * @return numeric position in dataset, start from 0
     */
    int getIndex();

    void setIndex(int i);

    /**
     * Full name is a combination of name and ID or dataset name.
     *
     * @return
     */
    String getFullName();

    /**
     * Adds value at the end and return its index
     *
     * @param value
     * @return index
     */
    int put(double value);

    /**
     * Remove i-th attribute
     *
     * @todo introduce version compatible with ArrayList etc.
     *
     * @param i
     */
    void remove(int i);

    /**
     * Retrieve value from Instance at given index
     *
     * @param index
     * @return value at position specified by index
     */
    double value(int index);

    /**
     * Set value at given position (behavior depends on underlying structure -
     * in array value will be replaced, in a list it might shift values)
     *
     * @param index starting from 0
     * @param value
     */
    void set(int index, double value);

    /**
     * Parses converts object into underlying instance representation.
     *
     * @param index
     * @param value
     */
    void setObject(int index, Object value);

    /**
     * Current number of attributes (dimension of the instance)
     *
     * @return
     */
    @Override
    int size();

    /**
     *
     * @return true when instance doesn't contain any value
     */
    boolean isEmpty();

    /**
     * Set maximum number of attributes (usually it's the same as size) It
     * becomes handy in case of sparse instances
     *
     * @param capacity
     */
    void setCapacity(int capacity);

    /**
     * Return maximum number of attributes that can be stored in the instance
     *
     * @return
     */
    int getCapacity();

    /**
     * Assignment to a class (category), it it's available otherwise returns
     * null
     *
     * @return
     */
    Object classValue();

    void setClassValue(Object obj);

    /**
     * Color is used for plotting
     *
     * @return color of instance
     */
    Color getColor();

    void setColor(Color c);

    /**
     * A deep copy of this instance
     *
     * @return
     */
    Instance copy();

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);

    double[] arrayCopy();

    /**
     * Shallow copy if possible
     *
     * @return reference to double data
     */
    double[] asArray();

    @Override
    String toString();

    /**
     * Converts instance to string representation
     *
     * @param separator of values
     * @return serialized values
     */
    String toString(String separator);

    /**
     * Used for exporting dataset
     *
     * @return array of Strings
     */
    String[] toStringArray();

    /**
     *
     * @return numeric meta data
     */
    double[] getMetaNum();

    /**
     * Numeric meta data
     *
     * @param meta
     */
    void setMetaNum(double[] meta);

    /**
     * When preprocessing data sometimes we need to display reference to
     * original data
     *
     * @return Instance from which was this one derived
     */
    Instance getAncestor();

    /**
     * Set reference to original data row
     *
     * @param instance
     */
    void setAncestor(Instance instance);

    /**
     * Return component for default data visualization which displays an
     * Instance (e.g. a scatterplot)
     *
     * @return
     */
    Plotter getPlotter();

    /**
     *
     * @param dataset
     */
    void setParent(Dataset<? extends Instance> dataset);

    /**
     * The dataset where this instance belong
     *
     * @return original dataset
     */
    Dataset<? extends Instance> getParent();
}
