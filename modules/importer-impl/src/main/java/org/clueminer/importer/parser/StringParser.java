package org.clueminer.importer.parser;

import org.clueminer.io.importer.api.AttributeParser;
import org.clueminer.io.importer.api.ParsingError;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = AttributeParser.class)
public class StringParser implements AttributeParser {

    private static final String nullValue = "n/a";
    private static final String typeName = "string";

    @Override
    public String getName() {
        return typeName;
    }

    @Override
    public Object parse(String value) throws ParsingError {
        //nothing to do
        return value;
    }

    @Override
    public String getNullValue() {
        return nullValue;
    }

}
