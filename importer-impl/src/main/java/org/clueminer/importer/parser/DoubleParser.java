package org.clueminer.importer.parser;

import org.clueminer.io.importer.api.AttributeParser;
import org.clueminer.io.importer.api.ParsingError;

/**
 *
 * @author Tomas Barton
 */
public class DoubleParser implements AttributeParser {

    private static final String nullValue = "n/a";
    private static final String typeName = "double";

    private static DoubleParser instance;

    public static DoubleParser getInstance() {
        if (instance == null) {
            instance = new DoubleParser();
        }
        return instance;
    }

    @Override
    public Object parse(String value) throws ParsingError {
        try {
            return Double.parseDouble(value);
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
