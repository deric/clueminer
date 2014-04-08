package org.clueminer.importer.parser;

import org.clueminer.io.importer.api.AttributeParser;
import org.clueminer.io.importer.api.ParsingError;

/**
 *
 * @author Tomas Barton
 */
public class IntParser implements AttributeParser {

    private static final String nullValue = "n/a";
    private static final String typeName = "int";

    private static IntParser instance;

    public static IntParser getInstance() {
        if (instance == null) {
            instance = new IntParser();
        }
        return instance;
    }

    @Override
    public Object parse(String value) throws ParsingError {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ParsingError("unable to parse value " + value + " as " + getTypeName() + ": " + e.getMessage());
        }
    }

    @Override
    public String getNullValue() {
        return nullValue;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

}
