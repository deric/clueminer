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
public class LongParserTest {

    private static final AttributeParser subject = new LongParser();

    @TestWith({
        "123, 123",
        "0, 0",
        "1, 1",
        "1234567890, 1234567890",
        "-5, -5"
    })
    public void testParse(String value, long expected) throws Exception {
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

    @Test(expected = ParsingError.class)
    public void testParsingError3() throws ParsingError {
        subject.parse("1.0");
    }

    @Test(expected = ParsingError.class)
    public void testParsingError4() throws ParsingError {
        subject.parse("5.12");
    }

}
