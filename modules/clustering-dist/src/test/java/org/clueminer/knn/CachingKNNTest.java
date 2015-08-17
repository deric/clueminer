/*
 * Copyright (C) 2015 clueminer.org
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
import org.clueminer.utils.Props;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class CachingKNNTest extends KnnTest {

    private final CachingKNN subject;

    public CachingKNNTest() {
        subject = new CachingKNN();
    }

    @Test
    public void testNnIds() {
        Dataset<? extends Instance> d = insectDataset();

        int idx = 0;
        int k = 3;
        int[] neighbors = subject.nnIds(idx, k, d, new Props());

        int[] expected = new int[]{6, 7, 1};
        Assert.assertArrayEquals(expected, neighbors);
    }

    @Test
    public void testNn() {
        Dataset<? extends Instance> d = irisDataset();
        int k = 5;
        Instance[] nn = subject.nn(0, k, d, new Props());
        assertEquals(k, nn.length);

        nn = subject.nn(9, k, d, new Props());
        //4.9,3.1,1.5,0.1, Iris-setosa
        Instance ref = d.get(9);
        //there are 3 same instances iris dataset

        //should find two very same instances (id: 34, 37)
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < d.attributeCount(); j++) {
                assertEquals(ref.get(j), nn[i].get(j), delta);
            }
        }
        assertEquals(k, nn.length);
    }

}
