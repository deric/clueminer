package org.clueminer.dataset.api;

/**
 * Used for defining attributes in a dataset. Supported attributes types are
 * loaded in runtime, which makes it easy to create a new type.
 *
 * @author Tomas Barton
 * @param <A> type of attribute being created
 */
public interface AttributeBuilder<A extends Attribute> {

    /**
     * By default should create a numeric continuous attribute (role: input)
     *
     * @param name
     * @param type
     * @return
     */
    A create(String name, AttributeType type);

    /**
     *
     * @param name
     * @param type
     * @param role role is either input data (processed by algorithms) or meta
     *             data
     * @return
     */
    A create(String name, AttributeType type, AttributeRole role);

    /**
     * By default should create a numeric attribute with input data role
     *
     * @param name
     * @param type
     * @return
     */
    A create(String name, String type);

    /**
     * In order to be independent on specific implementation a lookup by type
     * and role could be used. Befare of possible runtime exceptions
     *
     * @param name
     * @param type
     * @param role
     * @return
     */
    A create(String name, String type, String role);

    /**
     * Creates new instance of an attribute, but does not add it to the dataset;
     *
     * @param name
     * @param type
     * @param role
     * @return
     */
    A build(String name, String type, String role);

    A build(String name, AttributeType type, AttributeRole role);

    A build(String name, String type);

    A build(String name, AttributeType type);
}
