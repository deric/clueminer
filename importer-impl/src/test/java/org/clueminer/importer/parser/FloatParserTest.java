package org.clueminer.importer.parser;

import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import org.clueminer.io.importer.api.AttributeParser;
import org.clueminer.io.importer.api.ParsingError;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author deric
 */
@RunWith(ZohhakRunner.class)
public class FloatParserTest {

    private static final AttributeParser subject = new FloatParser();

    @TestWith({
        "1.23, 1.23",
        "0, 0.0",
        "-1, -1.0",
        "12e-2, 12e-2"
    })
    public void testParse(String value, float expected) throws Exception {
        assertEquals(expected, subject.parse(value));
    }

    @Test(expected = ParsingError.class)
    public void testParsingError() throws ParsingError {
        subject.parse("n/a");
    }

    @Test(expected = ParsingError.class)
    public void testParsingError2() throws ParsingError {
        subject.parse("wtf");
    }
}
