package org.clueminer.clustering.params;

/**
 * Defines type which is used for storing its value.
 *
 * @author Tomas Barton
 */
public enum ParamType {

    DOUBLE,
    STRING,
    BOOLEAN,
    INTEGER,
    NULL, //unspecified type (will be detected according to java types)
}
