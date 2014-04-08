package org.clueminer.importer.parser;

import org.clueminer.io.importer.api.AttributeParser;
import org.clueminer.io.importer.api.ParsingError;

/**
 *
 * @author Tomas Barton
 */
public class FloatParser implements AttributeParser {

    private static final String nullValue = "n/a";
    private static final String typeName = "float";

    private static FloatParser instance;

    public static FloatParser getInstance() {
        if (instance == null) {
            instance = new FloatParser();
        }
        return instance;
    }

    @Override
    public Object parse(String value) throws ParsingError {
        try {
            return Float.parseFloat(value);
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
