package org.clueminer.importer.parser;

import org.clueminer.io.importer.api.AttributeParser;
import org.clueminer.io.importer.api.ParsingError;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = AttributeParser.class)
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
            throw new ParsingError("unable to parse value " + value + " as " + getName() + ": " + e.getMessage());
        }
    }

    @Override
    public String getNullValue() {
        return nullValue;
    }

    @Override
    public String getName() {
        return typeName;
    }

}
