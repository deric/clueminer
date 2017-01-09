/*
 * Copyright (C) 2011-2017 clueminer.org
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

import com.googlecode.zohhak.api.TestWith;
import java.text.ParseException;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.exception.ParserError;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class LongRowFactoryTest {

    private LongRowFactory subject;
    private Dataset<? extends Instance> dataset;
    private static final double DELTA = 1e-9;

    @Before
    public void setUp() {
        dataset = new ArrayDataset<>(2, 1);
        dataset.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        subject = new LongRowFactory(dataset);
    }

    @TestWith({
        "123, 123",
        "0, 0",
        "1, 1",
        "1234567890, 1234567890",
        "-5, -5"
    })
    public void testParse(String value, float expected) throws Exception {
        Instance row = subject.create();
        subject.set(value, dataset.getAttribute(0), row);
        assertEquals(expected, row.get(0), DELTA);
    }

    @Test(expected = ParserError.class)
    public void testParsingError() throws ParserError, ParseException {
        Instance row = subject.create();
        subject.set("n/a", dataset.getAttribute(0), row);
    }

    @Test(expected = ParserError.class)
    public void testParsingError2() throws ParserError, ParseException {
        Instance row = subject.create();
        subject.set("n/a", dataset.getAttribute(0), row);
    }

    @Test(expected = ParserError.class)
    public void testParsingError3() throws ParserError {
        Instance row = subject.create();
        subject.set("1.12", dataset.getAttribute(0), row);
    }

}
