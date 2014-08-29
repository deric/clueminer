package org.clueminer.io.importer.api;

/**
 *
 * @author Tomas Barton
 */
public interface AttributeParser {

    /**
     * Return common type name
     *
     * @return
     */
    String getName();

    /**
     * Try to parse given Object (possibly String)
     *
     * @param value
     * @return
     * @throws org.clueminer.io.importer.api.ParsingError
     */
    Object parse(String value) throws ParsingError;

    /**
     * Default value is used when parsing fails
     *
     * @return String representation of default "NULL" value (e.g. n/a)
     */
    String getNullValue();
}
