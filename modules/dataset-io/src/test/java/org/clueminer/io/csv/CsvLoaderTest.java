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
package org.clueminer.io.csv;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.fixtures.CommonFixture;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 * @param <E>
 */
public class CsvLoaderTest<E extends Instance> {

    private static CommonFixture tf = new CommonFixture();
    private Dataset<E> dataset;
    private CsvLoader subject;

    public CsvLoaderTest() {
        subject = new CsvLoader();
    }

    @Test
    public void testLoad_File_Dataset() throws Exception {
        dataset = new ArrayDataset(102, 10);
        subject.load(tf.zooCsv(), dataset);
        assertEquals(102, dataset.size());
        assertEquals(18, dataset.attributeCount());
    }
}
