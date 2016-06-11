/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.importer.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import org.clueminer.attributes.BasicAttrRole;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.fixtures.MLearnFixture;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.io.importer.api.Report;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class CsvImporterTest {

    private CsvImporter subject;
    private static final CommonFixture FIX = new CommonFixture();
    private static final MLearnFixture ML = new MLearnFixture();

    @Before
    public void setUp() {
        subject = new CsvImporter();
        //subject.setLoader(new DraftContainer());
    }

    @Test
    public void testGetName() {
        assertEquals("CSV", subject.getName());
    }

    @Test
    public void testGetSeparator() {
        //default separator should be comma
        assertEquals(',', subject.getSeparator());
    }

    @Test
    public void testSetSeparator() {
        subject.setSeparator(';');
        assertEquals(';', subject.getSeparator());
    }

    @Test
    public void testExecute() throws IOException {
        Container container = new DraftContainer();

        subject.execute(container, FIX.irisData());
        assertEquals(151, container.getNumberOfLines());

    }

    @Test
    public void testDataImport() throws IOException {
        Container container = new DraftContainer();

        subject.execute(container, ML.correlations());
        assertEquals(28, container.getNumberOfLines());
        assertEquals(27, container.getInstanceCount());
        assertEquals(40, container.getAttributeCount());

        Attribute attr = container.getAttribute(0);
        assertEquals(BasicAttrRole.META, attr.getRole());

        attr = container.getAttribute(1);
        assertEquals(BasicAttrRole.INPUT, attr.getRole());

        Report r = container.getReport();
        System.out.println("issues: " + r.getText());
        InstanceDraft draft = container.getInstance(0);
        assertEquals("3-spiral", draft.getObject(0));
        System.out.println("[0, 0]: " + draft.getObject(0));
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

    @Test
    public void testImportData() throws Exception {
    }

    @Test
    public void testLineRead() throws Exception {
    }

    @Test
    public void testParseType() {
        Container cont = new DraftContainer();
        Attribute attr = cont.createAttribute(0, "test");
        assertNotNull(attr);
        subject.setContainer(cont);
        subject.parseType("Double", 0);
    }

    @Test
    public void testIsPending() {
    }

    @Test
    public void testIsNextCharacterEscapable() {
    }

    @Test
    public void testIsAllWhiteSpace() {
    }

    @Test
    public void testIsHasHeader() {
    }

    @Test
    public void testSetHasHeader() {
    }

    @Test
    public void testIsSkipHeader() {
    }

    @Test
    public void testSetSkipHeader() {
    }

    @Test
    public void testGetQuotechar() {
    }

    @Test
    public void testSetQuotechar() {
    }

    @Test
    public void testIsCancel() {
    }

    @Test
    public void testSetCancel() {
    }

    @Test
    public void testIsIgnoreQuotations() {
    }

    @Test
    public void testSetIgnoreQuotations() {
    }

    @Test
    public void testIsStrictQuotes() {
    }

    @Test
    public void testSetStrictQuotes() {
    }

    @Test
    public void testGetEscape() {
    }

    @Test
    public void testSetEscape() {
    }

    @Test
    public void testIsIgnoreLeadingWhiteSpace() {
    }

    @Test
    public void testSetIgnoreLeadingWhiteSpace() {
    }

//TODO: fix import
    //@Test
    public void testMissingValues() {
        CsvImporter importer = new CsvImporter();
        Container container = new DraftContainer();
        importer.setSeparator(';');
        importer.setHasHeader(false);
        HashSet<String> missing = new HashSet<String>();
        missing.add("NA");
        //importer.setMissing(missing);
        String line = "id-123;1;NA;NA;1;1;1;1;1;1;1;NA;1;1;1;1;1";
        Reader reader = new StringReader(line);
        try {
            importer.execute(container, reader);

            container.setMissing(missing);
            assertEquals(1, container.getNumberOfLines());
            assertEquals(BasicAttrRole.INPUT, container.getAttribute(0).getRole());
            assertEquals(17, container.getAttributeCount());
            assertEquals(1, container.getInstanceCount());
            InstanceDraft inst = container.getInstance(0);
            System.out.println("inst " + inst.toString());
            assertEquals(1.0, inst.getObject(1));
            assertEquals(Double.NaN, inst.getObject(2));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    @Test
    public void testParsingHeader() {
        CsvImporter importer = new CsvImporter();
        Container container = new DraftContainer();
        importer.setSeparator(',');
        importer.setHasHeader(true);
        String line = "id,meta_1,input_1,foo,bar";
        Reader reader = new StringReader(line);
        try {
            importer.execute(container, reader);

            assertEquals(1, container.getNumberOfLines());
            assertEquals(BasicAttrRole.ID, container.getAttribute(0).getRole());
            assertEquals(BasicAttrRole.META, container.getAttribute(1).getRole());
            assertEquals(BasicAttrRole.INPUT, container.getAttribute(2).getRole());
            assertEquals(5, container.getAttributeCount());
            assertEquals(0, container.getInstanceCount());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Test
    public void testParsingTypes() {
        CsvImporter importer = new CsvImporter();
        Container container = new DraftContainer();
        importer.setSeparator(',');
        importer.setHasHeader(true);
        String line = "attr1,attr2,attr3\ndouble,double,string";
        Reader reader = new StringReader(line);
        try {
            importer.execute(container, reader);

            assertEquals(2, container.getNumberOfLines());
            assertEquals(Double.class, container.getAttribute(0).getJavaType());
            assertEquals(Double.class, container.getAttribute(1).getJavaType());
            assertEquals(String.class, container.getAttribute(2).getJavaType());
            assertEquals(3, container.getAttributeCount());
            assertEquals(0, container.getInstanceCount());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
