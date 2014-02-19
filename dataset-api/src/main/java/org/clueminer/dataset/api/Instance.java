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

    public String getId();

    public void setId(String id);

    public String getName();

    public void setName(String name);

    /**
     * Full name is a combination of name and ID or dataset name.
     *
     * @return
     */
    public String getFullName();

    /**
     * Adds value at the end and return its index
     *
     * @param value
     * @return index
     */
    public int put(double value);

    /**
     * Remove i-th attribute
     *
     * @todo introduce version compatible with ArrayList etc.
     *
     * @param i
     */
    public void remove(int i);

    /**
     * Retrieve value from Instance at given index
     *
     * @param index
     * @return value at position specified by index
     */
    public double value(int index);

    /**
     * Set value at given position (behavior depends on underlying structure -
     * in array value will be replaced, in a list it might shift values)
     *
     * @param index starting from 0
     * @param value
     */
    public void set(int index, double value);

    /**
     * Current number of attributes (dimension of the instance)
     *
     * @return
     */
    @Override
    public int size();

    /**
     *
     * @return true when instance doesn't contain any value
     */
    public boolean isEmpty();

    /**
     * Set maximum number of attributes (usually it's the same as size) It
     * becomes handy in case of sparse instances
     *
     * @param capacity
     */
    public void setCapacity(int capacity);

    /**
     * Return maximum number of attributes that can be stored in the instance
     *
     * @return
     */
    public int getCapacity();

    /**
     * Assignment to a class (category), it it's available otherwise returns
     * null
     *
     * @return
     */
    public Object classValue();

    public void setClassValue(Object obj);

    /**
     * Color is used for plotting
     *
     * @return color of instance
     */
    public Color getColor();

    public void setColor(Color c);

    /**
     * A deep copy of this instance
     *
     * @return
     */
    public Instance copy();

    @Override
    public int hashCode();

    @Override
    public boolean equals(Object obj);

    public double[] arrayCopy();

    @Override
    public String toString();

    /**
     * Converts instance to string representation
     *
     * @param separator of values
     * @return serialized values
     */
    public String toString(String separator);

    /**
     * Used for exporting dataset
     *
     * @return array of Strings
     */
    public String[] toStringArray();

    /**
     *
     * @return numeric meta data
     */
    public double[] getMetaNum();

    /**
     * Numeric meta data
     *
     * @param meta
     */
    public void setMetaNum(double[] meta);

    /**
     * When preprocessing data sometimes we need to display reference to
     * original data
     *
     * @return Instance from which was this one derived
     */
    public Instance getAncestor();

    /**
     * Set reference to original data row
     *
     * @param instance
     */
    public void setAncestor(Instance instance);

    /**
     * Return component for default data visualization which displays an
     * Instance (e.g. a scatterplot)
     *
     * @return
     */
    public Plotter getPlotter();
}
