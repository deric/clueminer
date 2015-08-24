package org.clueminer.dataset.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import javax.swing.JComponent;
import org.clueminer.math.Matrix;

/**
 * Universal interface for a numerical dataset (something like spreadsheet).
 *
 * @author Tomas Barton
 * @param <E> type of data that is stored in the dataset
 */
public interface Dataset<E extends Instance> extends Cloneable, Serializable, Iterable<E>, Collection<E>, Set<E> {

    /**
     * Unique identifier of dataset
     *
     * @return usually it is a number, however to make it more universal, we use
     *         string
     */
    String getId();

    void setId(String id);

    /**
     * Returns the name of Dataset
     *
     * @return should be unique name of the dataset
     */
    String getName();

    /**
     * Set name of this dataset, which doesn't have to be an unique identifier
     *
     * @param name
     */
    void setName(String name);

    /**
     * Returns a set containing all different classes in this data set. If no
     * classes are available, this will return the empty set.
     *
     * @return
     */
    SortedSet<Object> getClasses();

    /**
     * Add an instance to this data set. The compatibility of the new item with
     * the items in the data set should be checked by the implementation.
     * Incompatible items should not be added to the data set.
     *
     * @param i - the instance to be added
     * @return true if the instance was added, otherwise false
     */
    @Override
    boolean add(E i);

    @Override
    boolean addAll(Collection<? extends E> c);

    boolean addAll(Dataset<? extends E> d);

    /**
     * Get the instance with a certain index. In order to keep readability of
     * code we broke standard getter/setter convention.
     *
     * e.g.: dataset.instance(i).value(j)
     *
     * @param index - the index of the instance you want to retrieve.
     * @return
     */
    E instance(int index);

    /**
     * as in array list
     *
     * @param index
     * @return
     */
    E get(int index);

    /**
     * Return true if dataset contains Instance at index idx
     *
     * @param idx
     * @return true if instance exists at given index
     */
    boolean hasIndex(int idx);

    /**
     * @param rand seed
     * @return Random instance from the dataset
     */
    E getRandom(Random rand);

    /**
     * Actual number of instances in dataset
     *
     * @return dataset size
     */
    @Override
    int size();

    /**
     *
     * @return true when dataset does not contain any instance
     */
    @Override
    boolean isEmpty();

    /**
     * During data preprocessing it is quite common to try different ways of
     * preprocessing and therefore generate a few datasets from the original
     * one. This reference is used to track back to the original dataset (which
     * parent is null).
     *
     * @return Dataset original dataset from which was created this one
     */
    Dataset<E> getParent();

    void setParent(Dataset<E> parent);

    /**
     *
     * @return true when parent Dataset exists
     */
    boolean hasParent();

    /**
     * The maximum number of attributes in each instance. Generally instances
     * might have different number of attributes. When the data set contains no
     * instances, this method should return 0.
     *
     * @return
     */
    int attributeCount();

    /**
     * Returns the index of the class value in the supplied data set. This
     * method will return -1 if the class value of this instance is not set.
     *
     * @param clazz class we are looking for (e.g. as a String)
     * @return the index of the class value
     */
    int classIndex(Object clazz);

    /**
     * Returns the class value of the supplied class index.
     *
     * @param index - the index to give the class value for
     * @return the class value of the index
     */
    Object classValue(int index);

    /**
     * Invoked when class membership of an instance +source+ changes
     *
     * @param orig
     * @param current
     * @param source
     */
    void changedClass(Object orig, Object current, Object source);

    /**
     * Get a copy of attributes
     *
     * @return
     */
    Attribute[] copyAttributes();

    /**
     * Array of attributes matching given role
     *
     * @param role input or meta data
     * @return
     */
    Attribute[] attributeByRole(AttributeRole role);

    /**
     * Reference to attributes (when we construct clusters we can use reference
     * to original data, however we can not safely modify data)
     *
     * @return
     */
    Map<Integer, Attribute> getAttributes();

    /**
     * Return attribute at position specified by index
     *
     * @param index
     * @return
     */
    Attribute getAttribute(int index);

    /**
     * Add attributes after last attribute
     *
     * @param attr
     */
    void addAttribute(Attribute attr);

    /**
     * Return attribute at position specified by name of an attribute
     *
     * @param attributeName
     * @return
     */
    Attribute getAttribute(String attributeName);

    /**
     * Method for direct access to instance values directly by attribute name
     * Speed pretty much depends on inner implementation of this method
     *
     * @param attributeName
     * @param instanceIdx
     * @return
     */
    double getAttributeValue(String attributeName, int instanceIdx);

    double getAttributeValue(Attribute attribute, int instanceIdx);

    /**
     * Get the value of an attribute in given instance (accessing data like in
     * matrix)
     *
     * @param instanceIdx    row index
     * @param attributeIndex column index
     * @return
     */
    double get(int instanceIdx, int attributeIndex);

    /**
     * Set attribute value by its name and index in the dataset
     *
     * @param attributeName
     * @param instanceIdx
     * @param value
     */
    void setAttributeValue(String attributeName, int instanceIdx, double value);

    /**
     * Set attribute value by its index and position in dataset
     *
     * @param instanceIdx instance index - starts from 0
     * @param attrIdx     attribute index - starts from 0
     * @param value
     */
    void set(int instanceIdx, int attrIdx, double value);

    /**
     * Places instance at given position
     *
     * @param instanceIdx
     * @param inst
     * @return
     */
    E set(int instanceIdx, E inst);

    /**
     * Set i-th attribute (column)
     *
     * @param index
     * @param attr
     */
    void setAttribute(int index, Attribute attr);

    /**
     * Set attributes
     *
     * @param attributes
     */
    void setAttributes(Map<Integer, Attribute> attributes);

    /**
     * Builder help to create instances of preferred type
     *
     * @return factory for building instances
     */
    InstanceBuilder<E> builder();

    /**
     * Builder create supported attributes types
     *
     * @return factory for building attributes
     */
    AttributeBuilder attributeBuilder();

    /**
     * Create a deep copy of the data set. This method should also create deep
     * copies of the instances in the data set.
     *
     * @return deep copy of this data set.
     */
    Dataset<? extends E> copy();

    /**
     * Copies common structure common to all instances but not instances itself
     *
     * @return Skeleton of dataset
     */
    Dataset<? extends E> duplicate();

    /**
     * Return copy of data as an array of double
     *
     * @return dataset as 2D array
     */
    double[][] arrayCopy();

    void setColorGenerator(ColorGenerator cg);

    @Override
    String toString();

    /**
     * Return component for default data visualization
     *
     * @return
     */
    JComponent getPlotter();

    /**
     * Make sure that dataset will be able to store given number of elements
     *
     * @param size
     */
    void ensureCapacity(int size);

    /**
     * Current capacity of dataset structure
     *
     * @return
     */
    int getCapacity();

    /**
     * Add reference to a dataset which was usually created by transformation of
     * this dataset.
     *
     * @param key     must be unique
     * @param dataset
     */
    void addChild(String key, Dataset<E> dataset);

    /**
     * Return dataset if exists, otherwise null
     *
     * @param key
     * @return
     */
    Dataset<E> getChild(String key);

    /**
     * Should provide matrix-like facade to access values of the dataset
     *
     * @return matrix interface access to the dataset
     */
    Matrix asMatrix();

    /**
     * Minimum value form all attributes
     *
     * @return minimum value in whole dataset
     */
    double min();

    /**
     * Maximum value from all attributes
     *
     * @return maximum value in whole dataset
     */
    double max();

    /**
     * Annulate all precomputed statistics
     */
    void resetStats();

    /**
     * Collection of values by attribute
     *
     * @param index of the attribute
     * @return
     */
    Collection<? extends Number> attrCollection(int index);

}
