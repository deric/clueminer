package org.clueminer.importer.impl;

import java.io.IOException;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.types.ContainerLoader;
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

    private final CsvImporter subject = new CsvImporter();
    private static final CommonFixture fixtures = new CommonFixture();

    public CsvImporterTest() {
    }

    @Before
    public void setUp() {
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
        subject.setFile(fixtures.irisData());
        ContainerLoader container = new ImportContainerImpl();
        /**
         * *
         * @TODO check parsed files
         */
        //subject.execute(container);
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

}
