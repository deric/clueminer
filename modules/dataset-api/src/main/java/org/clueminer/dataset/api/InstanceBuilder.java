package org.clueminer.dataset.api;

/**
 * Builder allows creating new instances without knowing which underlying
 * structure is used for storing data (we delegate this responsibility to class
 * which is implementing {@link org.clueminer.dataset.api.Dataset} interface).
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface InstanceBuilder<E extends Instance> {

    /**
     * Create an empty instance and add it to the dataset
     *
     * @return
     */
     E create();

    /**
     * Build will use same data structure for storing data, as other instances
     * in dataset are using.
     *
     * @return new instance without adding to the dataset
     */
     E build();

    /**
     * Depending on implementation copies relevant (meta)data from the original
     * instance. Method is used for data preprocessing/transformations.
     *
     * @param orig
     * @return new Instance
     */
     E createCopyOf(E orig);

    /**
     *
     * @param orig   original instance
     * @param parent dataset which will be considered as new instance parent
     * @return
     */
     E createCopyOf(E orig, Dataset<E> parent);

    /**
     *
     * @param capacity maximum number of items in set (array)
     * @return object for storing values (numerical, nominal, etc.)
     */
    E create(int capacity);

    /**
     * Builds new instance with given capacity and does not add it to the
     * dataset.
     *
     * @param capacity
     * @return
     */
    E build(int capacity);
    /**
     * Create an instance from double values
     *
     * @param values
     * @return
     */
     E create(double[] values);

    /**
     * Build an instance from given values
     *
     * @param values
     * @return
     */
    E build(double[] values);
    /**
     * Create an instance from double values
     *
     * @param values
     * @param classValue
     * @return
     */
    E create(double[] values, Object classValue);

    /**
     * Create an instance with a label (class value)
     *
     * @param values
     * @param classValue
     * @return
     */
    E create(double[] values, String classValue);

    /**
     * Build an instance and return it without adding to the dataset
     *
     * @param values
     * @param classValue
     * @return
     */
    E build(double[] values, String classValue);

    E create(String[] strings, Attribute[] attributes);
}
