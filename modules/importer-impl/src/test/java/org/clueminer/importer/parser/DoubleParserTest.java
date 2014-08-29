package org.clueminer.importer.parser;

import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import org.clueminer.io.importer.api.AttributeParser;
import org.clueminer.io.importer.api.ParsingError;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author deric
 */
@RunWith(ZohhakRunner.class)
public class DoubleParserTest {

    private static final AttributeParser subject = new DoubleParser();

    @TestWith({
        "1.23, 1.23",
        "0, 0.0",
        "-1, -1.0",
        "12e-2, 12e-2"
    })
    public void testParse(String value, double expected) throws Exception {
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
