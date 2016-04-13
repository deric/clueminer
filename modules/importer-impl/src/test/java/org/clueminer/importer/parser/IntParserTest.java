package org.clueminer.importer.parser;

import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import org.clueminer.io.importer.api.AttributeParser;
import org.clueminer.exception.ParsingError;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author deric
 */
@RunWith(ZohhakRunner.class)
public class IntParserTest {

    private static final AttributeParser subject = new IntParser();

    @TestWith({
        "123, 123",
        "0, 0",
        "-0, 0",
        "12, 12"
    })
    public void testParse(String value, int expected) throws Exception {
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
        subject.parse("1.12");
    }

}
