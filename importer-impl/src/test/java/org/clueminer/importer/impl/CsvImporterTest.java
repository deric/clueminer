package org.clueminer.importer.impl;

import java.io.IOException;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.importer.api.Container;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class CsvImporterTest {

    private CsvImporter subject;
    private static final CommonFixture fixtures = new CommonFixture();

    public CsvImporterTest() {
    }

    @Before
    public void setUp() {
        subject = new CsvImporter();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class CsvImporter.
     */
    @Test
    public void testGetName() {
        assertEquals("CSV", subject.getName());
    }

    /**
     * Test of getSeparator method, of class CsvImporter.
     */
    @Test
    public void testGetSeparator() {
        //default separator should be comma
        assertEquals(',', subject.getSeparator());
    }

    /**
     * Test of setSeparator method, of class CsvImporter.
     */
    @Test
    public void testSetSeparator() {
        subject.setSeparator(';');
        assertEquals(';', subject.getSeparator());
    }

    /**
     * Test of setReader method, of class CsvImporter.
     */
    @Test
    public void testSetReader() {
    }

    /**
     * Test of getFile method, of class CsvImporter.
     */
    @Test
    public void testGetFile() {
    }

    /**
     * Test of setFile method, of class CsvImporter.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testSetFile() throws IOException {
        subject.setFile(fixtures.irisData());
    }

    /**
     * Test of isAccepting method, of class CsvImporter.
     */
    @Test
    public void testIsAccepting() {
    }

    /**
     * Test of execute method, of class CsvImporter.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testExecute() throws IOException {
        Container container = new ImportContainerImpl();
        container.setFile(fixtures.irisData());

        subject.execute(container);
        assertEquals(150, container.getLoader().getNumberOfLines());
    }

    /**
     * Test of getContainer method, of class CsvImporter.
     */
    @Test
    public void testGetContainer() {
    }

    /**
     * Test of getReport method, of class CsvImporter.
     */
    @Test
    public void testGetReport() {
    }

    /**
     * Test of getFileTypes method, of class CsvImporter.
     */
    @Test
    public void testGetFileTypes() {
    }

    /**
     * Test of isMatchingImporter method, of class CsvImporter.
     */
    @Test
    public void testIsMatchingImporter() {
    }

    /**
     * Test of cancel method, of class CsvImporter.
     */
    @Test
    public void testCancel() {
    }

    /**
     * Test of setProgressTicket method, of class CsvImporter.
     */
    @Test
    public void testSetProgressTicket() {
    }

    @Test
    public void testParseLine() throws Exception {
        String nextItem[] = subject.parseLine("This, is, a, test.");
        assertEquals(4, nextItem.length);
        assertEquals("This", nextItem[0]);
        assertEquals(" is", nextItem[1]);
        assertEquals(" a", nextItem[2]);
        assertEquals(" test.", nextItem[3]);
    }

    @Test
    public void parseSimpleString() throws IOException {
        String[] nextLine = subject.parseLine("a,b,c");
        assertEquals(3, nextLine.length);
        assertEquals("a", nextLine[0]);
        assertEquals("b", nextLine[1]);
        assertEquals("c", nextLine[2]);
        assertFalse(subject.isPending());
    }

    @Test
    public void parseSimpleQuotedString() throws IOException {
        String[] nextLine = subject.parseLine("\"a\",\"b\",\"c\"");
        assertEquals(3, nextLine.length);
        assertEquals("a", nextLine[0]);
        assertEquals("b", nextLine[1]);
        assertEquals("c", nextLine[2]);
        assertFalse(subject.isPending());
    }

    @Test
    public void parseSimpleQuotedStringWithSpaces() throws IOException {
        subject.setStrictQuotes(true);
        subject.setIgnoreLeadingWhiteSpace(true);

        String[] nextLine = subject.parseLine(" \"a\" , \"b\" , \"c\" ");
        assertEquals(3, nextLine.length);
        assertEquals("a", nextLine[0]);
        assertEquals("b", nextLine[1]);
        assertEquals("c", nextLine[2]);
        assertFalse(subject.isPending());
    }

    /**
     * Tests quotes in the middle of an element.
     *
     * @throws IOException if bad things happen
     */
    @Test
    public void testParsedLineWithInternalQuota() throws IOException {
        String[] nextLine = subject.parseLine("a,123\"4\"567,c");
        assertEquals(3, nextLine.length);
        assertEquals("123\"4\"567", nextLine[1]);
    }

    @Test
    public void parseQuotedStringWithCommas() throws IOException {
        String[] nextLine = subject.parseLine("a,\"b,b,b\",c");
        assertEquals("a", nextLine[0]);
        assertEquals("b,b,b", nextLine[1]);
        assertEquals("c", nextLine[2]);
        assertEquals(3, nextLine.length);
    }

    @Test
    public void parseQuotedStringWithDefinedSeperator() throws IOException {
        subject.setSeparator(':');

        String[] nextLine = subject.parseLine("a:\"b:b:b\":c");
        assertEquals("a", nextLine[0]);
        assertEquals("b:b:b", nextLine[1]);
        assertEquals("c", nextLine[2]);
        assertEquals(3, nextLine.length);
    }

    @Test
    public void parseQuotedStringWithDefinedSeperatorAndQuote() throws IOException {
        subject.setSeparator(':');
        subject.setQuotechar('\'');

        String[] nextLine = subject.parseLine("a:'b:b:b':c");
        assertEquals("a", nextLine[0]);
        assertEquals("b:b:b", nextLine[1]);
        assertEquals("c", nextLine[2]);
        assertEquals(3, nextLine.length);
    }

    @Test
    public void parseEmptyElements() throws IOException {
        String[] nextLine = subject.parseLine(",,");
        assertEquals(3, nextLine.length);
        assertEquals("", nextLine[0]);
        assertEquals("", nextLine[1]);
        assertEquals("", nextLine[2]);
    }

    @Test
    public void parseMultiLinedQuoted() throws IOException {
        String[] nextLine = subject.parseLine("a,\"PO Box 123,\nKippax,ACT. 2615.\nAustralia\",d.\n");
        assertEquals(3, nextLine.length);
        assertEquals("a", nextLine[0]);
        assertEquals("PO Box 123,\nKippax,ACT. 2615.\nAustralia", nextLine[1]);
        assertEquals("d.\n", nextLine[2]);
    }

    @Test
    public void testADoubleQuoteAsDataElement() throws IOException {
        String[] nextLine = subject.parseLine("a,\"\"\"\",c");// a,"""",c

        assertEquals(3, nextLine.length);
        assertEquals("a", nextLine[0]);
        assertEquals(1, nextLine[1].length());
        assertEquals("\"", nextLine[1]);
        assertEquals("c", nextLine[2]);
    }

    @Test
    public void testEscapedDoubleQuoteAsDataElement() throws IOException {
        subject.setStrictQuotes(true);
        //subject.setIgnoreQuotations(true);
        String[] nextLine = subject.parseLine("\"test\",\"this,test,is,good\",\"\\\"test\\\"\",\"\\\"quote\\\"\""); // "test","this,test,is,good","\"test\",\"quote\""

        assertEquals(4, nextLine.length);

        assertEquals("test", nextLine[0]);
        assertEquals("this,test,is,good", nextLine[1]);
        // assertEquals("\"test\"", nextLine[2]);
        //assertEquals("\"quote\"", nextLine[3]);

    }
}
