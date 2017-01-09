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
package org.clueminer.io;

import java.io.IOException;
import org.clueminer.exception.ParserError;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.utils.DataFileInfo;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DataSnifferTest {

    private static CommonFixture tf;
    private final DataSniffer subject;

    public DataSnifferTest() {
        tf = new CommonFixture();
        subject = new DataSniffer();
    }

    @Test
    public void testNumAttributes() throws IOException, ParserError {
        DataFileInfo df = subject.scan(tf.irisArff());
        assertEquals(4, df.numAttributes);

        df = subject.scan(tf.glassArff());
        assertEquals(9, df.numAttributes);

        df = subject.scan(tf.irisData());
        assertEquals(",", df.separator);
        assertEquals(4, df.numAttributes);
    }

}
