package org.clueminer.dataset.api;

/**
 *
 * @author Tomas Barton
 */
public interface InstanceBuilder<E extends Instance> {

    /**
     * Create an empty instance
     *
     * @return
     */
    public E create();

    /**
     * Depending on implementation copies relevant (meta)data from the original
     * instance. Method is used for data preprocessing/transformations.
     *
     * @param orig
     * @return new Instance
     */
    public E createCopyOf(E orig);

    /**
     *
     * @param orig original instance
     * @param parent dataset which will be considered as new instance parent
     * @return
     */
    public E createCopyOf(E orig, Dataset<E> parent);

    /**
     *
     * @param capacity maximum number of items in set (array)
     * @return object for storing values (numerical, nominal, etc.)
     */
    public E create(int capacity);

    /**
     * Create an instance from double values
     *
     * @param values
     * @return
     */
    public E create(double[] values);

    /**
     * Create an instance from double values
     *
     * @param values
     * @param classValue
     * @return
     */
    public E create(double[] values, Object classValue);

    public E create(String[] strings, Attribute[] attributes);
}
