package org.clueminer.dataset;

import org.clueminer.exception.UnsupportedAttributeType;

/**
 *
 * @author Tomas Barton
 */
public interface AttributeBuilder {
    
    public Attribute create(String name, IAttributeType type) throws UnsupportedAttributeType;
    
    public Attribute create(String name, String type) throws UnsupportedAttributeType;
}
