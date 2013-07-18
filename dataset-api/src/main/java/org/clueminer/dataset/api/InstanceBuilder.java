package org.clueminer.dataset.api;

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
     * Depending on implementation copies relevant (meta)data from the original
     * instance. Method is used for data preprocessing/transformations.
     *
     * @param orig
     * @return new Instance
     */
    public Instance createCopyOf(Instance orig);
    
    /**
     *      
     * @param orig original instance
     * @param parent dataset which will be considered as new instance parent
     * @return 
     */
    public Instance createCopyOf(Instance orig, Dataset<Instance> parent);

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
