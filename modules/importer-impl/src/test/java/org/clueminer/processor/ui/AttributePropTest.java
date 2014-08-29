package org.clueminer.processor.ui;

import org.clueminer.attributes.BasicAttrRole;
import org.clueminer.importer.impl.AttributeDraftImpl;
import org.clueminer.io.importer.api.AttributeDraft;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class AttributePropTest {

    private static AttributeProp subject;

    public AttributePropTest() {
        AttributeDraft attr = new AttributeDraftImpl("test");
        attr.setType(Double.class);
        attr.setRole(BasicAttrRole.INPUT);
        subject = new AttributeProp(attr, new CsvImporterUI());
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSetAttrName() {
    }

    @Test
    public void testSetType() {
    }

    @Test
    public void testSetRole() {
    }

    /**
     * TODO: rewrite to parametric test
     */
    @Test
    public void testStringToClass() {
        Class<?> res = subject.stringToClass("double");
        assertEquals(Double.class, res);
        res = subject.stringToClass("float");
        assertEquals(Float.class, res);
    }

    @Test
    public void testClassToString() {
        String res = subject.classToString(Double.class);
        assertEquals("double", res);
        res = subject.classToString(Float.class);
        assertEquals("float", res);
    }

}
