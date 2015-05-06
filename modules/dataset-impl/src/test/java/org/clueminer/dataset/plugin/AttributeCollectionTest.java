/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.dataset.plugin;

import java.util.Iterator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class AttributeCollectionTest {

    private AttributeCollection subject;
    private final double[][] data2x5 = new double[][]{{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}};
    private Dataset<? extends Instance> dataset;

    public AttributeCollectionTest() {
        dataset = new ArrayDataset<>(data2x5);
    }

    @Test
    public void testSize() {
        subject = new AttributeCollection(dataset, 0);

        assertEquals(2, subject.size());
    }

    @Test
    public void testIsEmpty() {
        subject = new AttributeCollection(dataset, 3);
        assertEquals(false, subject.isEmpty());
    }

    @Test
    public void testContains() {
        subject = new AttributeCollection(dataset, 0);
        assertEquals(true, subject.contains(1.0));
        assertEquals(true, subject.contains(6.0));
    }

    @Test
    public void testIterator() {
        subject = new AttributeCollection(dataset, 0);
        int i = 0;
        Iterator it = subject.iterator();
        while (it.hasNext()) {
            assertNotNull(it.next());
            i++;
        }
        assertEquals(1, i);
    }


}
