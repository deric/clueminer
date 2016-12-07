package org.clueminer.dataset.api;

import java.text.DecimalFormat;
import java.util.HashSet;
import org.clueminer.exception.ParserError;

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
     * Build instance and immediately adds to the dataset
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
     * Create an instance from string values
     *
     * @param values
     * @return newly created instance
     */
    E create(String[] values) throws ParserError;

    /**
     * Store value as <code>row</code>'s <code>attr</code> value.
     *
     * @param value
     * @param row
     * @param attr
     * @throws java.text.ParseException in case that value can't be converted to
     *                                  the attribute's type
     */
    void set(Object value, Attribute attr, E row) throws ParserError;

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

    E create(String[] strings, Attribute[] attributes) throws ParserError;

    /**
     * Convert values from strings
     *
     * @param values
     * @param classValue
     * @return
     */
    E create(String[] values, Object classValue) throws ParserError;

    /**
     * List of strings which are considered as missing values
     *
     * @return
     */
    HashSet<String> getMissing();

    void setMissing(HashSet<String> missing);

    /**
     * Number formatter
     *
     * @return
     */
    DecimalFormat getDecimalFormat();

    /**
     * Set formatter for parsing Strings into numbers
     *
     * @param decimalFormat
     */
    void setDecimalFormat(DecimalFormat decimalFormat);
}
