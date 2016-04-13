package org.clueminer.attributes;

import org.clueminer.dataset.api.AttributeType;

/**
 * Attribute types supported by this module.
 *
 * @author Tomas Barton
 */
public enum BasicAttrType implements AttributeType {

    NUMERICAL,
    NUMERIC,
    REAL,
    NOMINAL,
    STRING,
    DATE,
    TIME,
    DATE_TIME,
    /**
     * multi-dimensional data (as single attribute)
     */
    MD_DATA,
    INTEGER;

}
