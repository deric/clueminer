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
package org.clueminer.knn;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author deric
 */
public class KDTreeTest extends KnnTest {

    private KDTree subject;

    public KDTreeTest() {
    }

    //@Test
    public void testNearest() {
        Dataset<? extends Instance> data = insectDataset();
        subject = new KDTree(data);
        //subject.setDataset(data);


        assertEquals(data.get(6), subject.nearest(data.get(0)).key);
    }

}
