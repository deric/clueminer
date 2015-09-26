package org.clueminer.dataset.api;

import java.io.Serializable;
import java.util.Iterator;

/**
 *
 * @author Tomas Barton
 */
public interface Attribute extends Cloneable, Serializable, DataVector {

    /**
     * Indicates a missing value for nominal values. For the internal values and
     * numerical values, Double.NaN is used which can be checked via
     * {@link Double#isNaN(double)}.
     */
    public static final String MISSING_NOMINAL_VALUE = "?";

    /**
     * Returns the name of the attribute.
     *
     * @return
     */
    String getName();

    /**
     * Sets the name of the attribute.
     *
     * @param name
     */
    void setName(String name);

    /**
     * @return index of column in dataset
     */
    int getIndex();

    /**
     * Set index of an attribute
     *
     * @param index
     */
    void setIndex(int index);

    /**
     * In order to obtain all values that belong to specific attribute it's
     * necessary to change a row based approach to a column based. Dataset
     * contain references to all "rows" (instances)
     *
     * @param dataset
     */
    void setDataset(Dataset<? extends Instance> dataset);

    /**
     * Returns an iterator over all statistics objects available for this type
     * of attribute. Additional statistics can be registered via
     * {@link #registerStatistics(Statistics)}.
     *
     * @return
     */
    Iterator<Statistics> getAllStatistics();

    /**
     * Registers the attribute statistics.
     *
     * @param statistics
     */
    void registerStatistics(Statistics statistics);

    /**
     * Return value of precomputed statistics, which should be on changes in
     * dataset updated
     *
     * @param name
     * @return
     */
    double statistics(IStats name);

    /**
     * Invoke reset on all registered statistics
     */
    void resetStats();

    /**
     * Triggered when a new value is added to a dataset The type of a value
     * should be determined by the Attribute itself
     *
     *
     * @param value
     */
    void updateStatistics(Object value);

    /**
     * Returns the nominal mapping between nominal values and internal double
     * representations. Please note that invoking this method might result in an
     * {@link UnsupportedOperationException}
     * for non-nominal attributes.
     *
     * @return
     */
    NominalMapping getMapping();

    /**
     * Returns the nominal mapping between nominal values and internal double
     * representations. Please note that invoking this method might result in an
     * exception for non-nominal attributes.
     *
     * @param nominalMapping
     */
    void setMapping(NominalMapping nominalMapping);

    /**
     * Sets the default value for this attribute.
     *
     * @param value
     */
    void setDefault(double value);

    /**
     * Returns the default value for this attribute.
     *
     * @return
     */
    double getDefault();

    /**
     * Returns true if the attribute is nominal.
     *
     * @return
     */
    boolean isNominal();

    /**
     * Returns true if the attribute is nominal.
     *
     * @return
     */
    boolean isNumerical();

    /**
     * If true attribute would not be included in input data of an algorithm
     *
     * @return
     */
    boolean isMeta();

    /**
     * Role could be input data (used for computation) or meta data
     *
     * @return
     */
    AttributeRole getRole();

    /**
     *
     * @return
     */
    AttributeType getType();

    /**
     * Set role of the attribute
     *
     * @param role
     */
    void setRole(AttributeRole role);

    /**
     * We should be able to go through all values of an attribute. In
     * spreadsheet we would have Instances in rows and attributes in columns
     *
     * @return String | Double | Boolean | etc.
     */
    Iterator<? extends Object> values();

    /**
     * If attribute is numerical, it should support this conversion
     *
     * @return attribute values as double array
     */
    double[] asDoubleArray();

    /**
     * Number of items (e.g. number of rows in dataset)
     *
     * @return
     */
    int size();

    /**
     * Returns a formatted string of the given value according to the attribute
     * type.
     *
     * @param value
     * @param digits
     * @param quoteNominal
     * @return
     */
    String asString(double value, int digits, boolean quoteNominal);

    /**
     * Returns true if the given object is an attribute with the same name and
     * table index.
     *
     * @param o
     */
    @Override
    boolean equals(Object o);

    /**
     * Returns the hash code. Please note that equal attributes must return the
     * same hash code.
     */
    @Override
    int hashCode();

    /**
     * Clones this attribute.
     *
     * @return
     */
    Object clone();

    /**
     * Returns a human readable string that describes this attribute.
     */
    @Override
    String toString();
}
