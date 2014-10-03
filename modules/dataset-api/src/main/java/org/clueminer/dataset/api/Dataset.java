package org.clueminer.dataset.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import javax.swing.JComponent;

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
    public String getId();

    public void setId(String id);

    /**
     * Returns the name of Dataset
     *
     * @return should be unique name of the dataset
     */
    public String getName();

    /**
     * Set name of this dataset, which doesn't have to be an unique identifier
     *
     * @param name
     */
    public void setName(String name);

    /**
     * Returns a set containing all different classes in this data set. If no
     * classes are available, this will return the empty set.
     *
     * @return
     */
    public SortedSet<Object> getClasses();

    /**
     * Add an instance to this data set. The compatibility of the new item with
     * the items in the data set should be checked by the implementation.
     * Incompatible items should not be added to the data set.
     *
     * @param i - the instance to be added
     * @return true if the instance was added, otherwise false
     */
    @Override
    public boolean add(E i);

    @Override
    public boolean addAll(Collection<? extends E> c);

    public boolean addAll(Dataset<E> d);

    /**
     * Get the instance with a certain index. In order to keep readability of
     * code we broke standard getter/setter convention.
     *
     * e.g.: dataset.instance(i).value(j)
     *
     * @param index - the index of the instance you want to retrieve.
     * @return
     */
    public E instance(int index);

    /**
     * as in array list
     *
     * @param index
     * @return
     */
    public E get(int index);

    /**
     * Return true if dataset contains Instance at index idx
     *
     * @param idx
     * @return true if instance exists at given index
     */
    public boolean hasIndex(int idx);

    /**
     * @param rand seed
     * @return Random instance from the dataset
     */
    public E getRandom(Random rand);

    /**
     * Actual number of instances in dataset
     *
     * @return dataset size
     */
    @Override
    public int size();

    /**
     *
     * @return true when dataset does not contain any instance
     */
    @Override
    public boolean isEmpty();

    /**
     * During data preprocessing it is quite common to try different ways of
     * preprocessing and therefore generate a few datasets from the original
     * one. This reference is used to track back to the original dataset (which
     * parent is null).
     *
     * @return Dataset original dataset from which was created this one
     */
    public Dataset<? extends Instance> getParent();

    public void setParent(Dataset<? extends Instance> parent);

    /**
     *
     * @return true when parent Dataset exists
     */
    public boolean hasParent();

    /**
     * The maximum number of attributes in each instance. Generally instances
     * might have different number of attributes. When the data set contains no
     * instances, this method should return 0.
     *
     * @return
     */
    public int attributeCount();

    /**
     * Returns the index of the class value in the supplied data set. This
     * method will return -1 if the class value of this instance is not set.
     *
     * @param clazz class we are looking for (e.g. as a String)
     * @return the index of the class value
     */
    public int classIndex(Object clazz);

    /**
     * Returns the class value of the supplied class index.
     *
     * @param index - the index to give the class value for
     * @return the class value of the index
     */
    public Object classValue(int index);

    /**
     * Get a copy of attributes
     *
     * @return
     */
    public Attribute[] copyAttributes();

    /**
     * Array of attributes matching given role
     *
     * @param role input or meta data
     * @return
     */
    public Attribute[] attributeByRole(AttributeRole role);

    /**
     * Reference to attributes (when we construct clusters we can use reference
     * to original data, however we can not safely modify data)
     *
     * @return
     */
    public Map<Integer, Attribute> getAttributes();

    /**
     * Return attribute at position specified by index
     *
     * @param index
     * @return
     */
    public Attribute getAttribute(int index);

    /**
     * Add attributes after last attribute
     *
     * @param attr
     */
    public void addAttribute(Attribute attr);

    /**
     * Return attribute at position specified by name of an attribute
     *
     * @param attributeName
     * @return
     */
    public Attribute getAttribute(String attributeName);

    /**
     * Method for direct access to instance values directly by attribute name
     * Speed pretty much depends on inner implementation of this method
     *
     * @param attributeName
     * @param instanceIdx
     * @return
     */
    public double getAttributeValue(String attributeName, int instanceIdx);

    public double getAttributeValue(Attribute attribute, int instanceIdx);

    /**
     * Get the value of an attribute in given instance (accessing data like in
     * matrix)
     *
     * @param instanceIdx    row index
     * @param attributeIndex column index
     * @return
     */
    public double get(int instanceIdx, int attributeIndex);

    /**
     * Set attribute value by its name and index in the dataset
     *
     * @param attributeName
     * @param instanceIdx
     * @param value
     */
    public void setAttributeValue(String attributeName, int instanceIdx, double value);

    /**
     * Set attribute value by its index and position in dataset
     *
     * @param instanceIdx instance index - starts from 0
     * @param attrIdx     attribute index - starts from 0
     * @param value
     */
    public void set(int instanceIdx, int attrIdx, double value);

    /**
     * Set i-th attribute (column)
     *
     * @param index
     * @param attr
     */
    public void setAttribute(int index, Attribute attr);

    /**
     * Set attributes
     *
     * @param attributes
     */
    public void setAttributes(Map<Integer, Attribute> attributes);

    /**
     * Builder help to create instances of preferred type
     *
     * @return factory for building instances
     */
    public InstanceBuilder builder();

    /**
     * Builder create supported attributes types
     *
     * @return factory for building attributes
     */
    public AttributeBuilder attributeBuilder();

    /**
     * Create a deep copy of the data set. This method should also create deep
     * copies of the instances in the data set.
     *
     * @return deep copy of this data set.
     */
    public Dataset<? extends E> copy();

    /**
     * Copies common structure common to all instances but not instances itself
     *
     * @return Skeleton of dataset
     */
    public Dataset<? extends E> duplicate();

    /**
     * Return copy of data as an array of double
     *
     * @return dataset as 2D array
     */
    public double[][] arrayCopy();

    public void setColorGenerator(ColorGenerator cg);

    @Override
    public String toString();

    /**
     * Return component for default data visualization
     *
     * @return
     */
    public JComponent getPlotter();

    /**
     * Make sure that dataset will be able to store given number of elements
     *
     * @param size
     */
    public void ensureCapacity(int size);

    /**
     * Current capacity of dataset structure
     *
     * @return
     */
    public int getCapacity();

    /**
     * Add reference to a dataset which was usually created by transformation of
     * this dataset.
     *
     * @param key     must be unique
     * @param dataset
     */
    public void addChild(String key, Dataset<Instance> dataset);

    /**
     * Return dataset if exists, otherwise null
     *
     * @param key
     * @return
     */
    public Dataset<Instance> getChild(String key);
}
