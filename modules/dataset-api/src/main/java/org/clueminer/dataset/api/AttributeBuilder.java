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

    /**
     * In order to be independent on specific implementation a lookup by type
     * and role could be used. Befare of possible runtime exceptions
     *
     * @param name
     * @param type
     * @param role
     * @return
     */
    public Attribute create(String name, String type, String role);

    /**
     * Creates new instance of an attribute, but does not add it to the dataset;
     *
     * @param name
     * @param type
     * @param role
     * @return
     */
    public Attribute build(String name, String type, String role);

    public Attribute build(String name, AttributeType type, AttributeRole role);

    public Attribute build(String name, String type);
}
