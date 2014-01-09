package org.clueminer.dataset.api;

/**
 *
 * @author Tomas Barton
 */
public interface AttributeBuilder {

    public Attribute create(String name, AttributeType type);

    public Attribute create(String name, String type);
}
