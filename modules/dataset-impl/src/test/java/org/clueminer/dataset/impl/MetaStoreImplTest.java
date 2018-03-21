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
package org.clueminer.dataset.impl;

import org.clueminer.attributes.BasicAttrRole;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class MetaStoreImplTest {

    private static MetaStoreImpl subject;
    private static Dataset<? extends Instance> dataset;
    private static final double DELTA = 1e-9;

    @Before
    public void setUp() {
        subject = new MetaStoreImpl();
        dataset = new ArrayDataset<>(2, 1);
        dataset.attributeBuilder().create("x", BasicAttrType.NUMERIC, BasicAttrRole.META);
        dataset.attributeBuilder().create("y", BasicAttrType.STRING, BasicAttrRole.META);
    }

    @Test
    public void testGetAndSet() {
        Attribute attr = dataset.getAttribute(0);
        subject.set(attr, 0, 123.0);
        assertEquals(123.0, subject.get(attr, 0));
    }

    @Test
    public void testGetDouble() {
        Attribute attr = dataset.getAttribute(0);
        subject.set(attr, 0, 123.0);
        assertEquals(123.0, subject.getDouble(attr, 0), DELTA);
    }

    @Test
    public void testGetString() {
        Attribute attr = dataset.getAttribute(1);
        subject.set(attr, 0, "foo");
        assertEquals("foo", subject.getString(attr, 0));
    }

}
