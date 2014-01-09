package org.clueminer.dataset.api;

/**
 *
 * @author Tomas Barton
 */
public interface AttributeBuilder {

    /**
     * By default should create a numeric continuous attribute (role: input)
     *
     * @param name
     * @param type
     * @return
     */
    public Attribute create(String name, AttributeType type);

    /**
     *
     * @param name
     * @param type
     * @param role role is either input data (processed by algorithms) or meta
     *             data
     * @return
     */
    public Attribute create(String name, AttributeType type, AttributeRole role);

    /**
     * By default should create a numeric attribute with input data role
     *
     * @param name
     * @param type
     * @return
     */
    public Attribute create(String name, String type);
}
