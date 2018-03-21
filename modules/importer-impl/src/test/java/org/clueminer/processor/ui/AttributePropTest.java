/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.processor.ui;

import org.clueminer.attributes.BasicAttrRole;
import org.clueminer.importer.impl.AttributeDraftImpl;
import org.clueminer.io.importer.api.AttributeDraft;
import static org.junit.Assert.*;
import org.junit.Test;

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
