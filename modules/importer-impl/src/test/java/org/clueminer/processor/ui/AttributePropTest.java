package org.clueminer.processor.ui;

import org.clueminer.attributes.BasicAttrRole;
import org.clueminer.importer.impl.AttributeDraftImpl;
import org.clueminer.io.importer.api.AttributeDraft;
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
        attr.setJavaType(Double.class);
        attr.setRole(BasicAttrRole.INPUT);
        subject = new AttributeProp(attr, new CsvImporterUI());
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
