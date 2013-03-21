package org.clueminer.attributes;

import org.clueminer.dataset.api.IAttributeType;

/**
 *
 * @author Tomas Barton
 */
public enum AttributeType implements IAttributeType {
    NUMERICAL,
    NUMERIC,
    REAL,
    NOMINAL,
    STRING,
    DATE,
    TIME,
    DATE_TIME;
    
}
