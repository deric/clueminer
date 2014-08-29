package org.clueminer.attributes;

import org.clueminer.dataset.api.AttributeType;

/**
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
    INTEGER;

}
