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

import java.util.LinkedList;
import java.util.List;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.neighbor.Neighbor;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class LinearSearchTest extends KnnTest {

    private final LinearSearch subject;

    public LinearSearchTest() {
        subject = new LinearSearch();
    }

    @Test
    public void testNearest() {
        Dataset<? extends Instance> data = insectDataset();
        subject.setDataset(data);

        assertEquals(data.get(6), subject.nearest(data.get(0)).key);
    }

    @Test
    public void testKnn() {
        Dataset<? extends Instance> d = insectDataset();
        subject.setDataset(d);
        int idx = 0;
        int k = 3;
        Neighbor[] neighbors = subject.knn(d.get(idx), k);

        int[] expected = new int[]{6, 7, 1};

        for (int i = 0; i < neighbors.length; i++) {
            Neighbor neighbor = neighbors[i];
            assertEquals(expected[i], ((Instance) neighbor.key).getIndex());
        }
    }

    @Test
    public void testNn() {
        Dataset<? extends Instance> d = irisDataset();
        subject.setDataset(d);
        int k = 5;
        //4.9,3.1,1.5,0.1, Iris-setosa
        Instance ref = d.get(9);
        Neighbor[] nn = subject.knn(ref, k);
        assertEquals(k, nn.length);
        Instance inst;
        //there are 3 same instances iris dataset
        //should find two very same instances (id: 34, 37)
        for (int i = 0; i < 2; i++) {
            inst = (Instance) nn[i].key;
            for (int j = 0; j < d.attributeCount(); j++) {
                assertEquals(ref.get(j), inst.get(j), delta);
            }
        }
        assertEquals(k, nn.length);
    }

    @Test
    public void testRange() {
        Dataset<? extends Instance> d = irisDataset();
        subject.setDataset(d);

        List<Neighbor<Instance>> neighbors = new LinkedList<>();
        Instance ref = d.get(9);
        double range = 0.1;
        subject.range(ref, range, neighbors);

        for (Neighbor<Instance> neighbor : neighbors) {
            assertEquals(0.0, neighbor.distance, delta);
        }
        //there are 2 same instances
        assertEquals(2, neighbors.size());

    }

}
