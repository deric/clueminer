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
import org.clueminer.neighbor.Neighbor;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class KDTreeTest extends KnnTest {

    private KDTree subject;

    public KDTreeTest() {
    }

    @Test
    public void testNearest() {
        Dataset<? extends Instance> data = insectDataset();
        subject = new KDTree(data);

        assertEquals(data.get(6), subject.nearest(data.get(0)).key);
    }

    @Test
    public void testNn() {
        Dataset<? extends Instance> d = irisDataset();
        subject = new KDTree();
        subject.setDataset(d);
        int k = 5;
        //4.9,3.1,1.5,0.1, Iris-setosa
        Instance ref = d.get(9);
        Neighbor[] nn = subject.knn(ref, k);
        assertEquals(k, nn.length);
        Instance inst;
        //there are 3 same instances iris dataset
        //should find two very same instances (id: 34, 37)
        for (int i = 0; i < 5; i++) {
            inst = (Instance) nn[i].key;
            //TODO: invert order of k-NN results
            System.out.println("inst: " + inst.getIndex());
            /*for (int j = 0; j < d.attributeCount(); j++) {
             assertEquals(ref.get(j), inst.get(j), delta);
             }*/
        }
        assertEquals(k, nn.length);
    }

}
