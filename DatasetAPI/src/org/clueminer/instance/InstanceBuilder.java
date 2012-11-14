package org.clueminer.instance;

import org.clueminer.dataset.Attribute;

/**
 *
 * @author Tomas Barton
 */
public interface InstanceBuilder {
    
        /**
     * Create an empty instance
     *
     * @return
     */
    public Instance create();
    
    /**
     * 
     * @param capacity maximum number of items in set (array)
     * @return object for storing values (numerical, nominal, etc.)
     */
    public Instance create(int capacity);

    /**
     * Create an instance from double values
     *
     * @param values
     * @return
     */
    public Instance create(double[] values);

    /**
     * Create an instance from double values
     *
     * @param values
     * @param classValue
     * @return
     */
    public Instance create(double[] values, Object classValue);
    
    public Instance create(String[] strings, Attribute[] attributes);
}
