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
package org.clueminer.knn;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.neighbor.Neighbor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class KDTreeTest<E extends Instance> extends KnnTest {

    private KDTree subject;

    Dataset<E> data;
    KDTree<E> kdtree = null;
    LinearSearch<E> naive = null;
    private static double DELTA = 1e-9;

    public KDTreeTest() {
        int n = 5;
        int d = 2;
        data = new ArrayDataset(n, d);
        for (int i = 0; i < d; i++) {
            data.attributeBuilder().create("d" + i, "NUMERIC");
        }
        InstanceBuilder<E> b = data.builder();
        E inst;
        for (int i = 0; i < n; i++) {
            inst = b.build(d);
            for (int j = 0; j < d; j++) {
                inst.set(j, Math.random() * 10);//Math.random() * 10
            }
            data.add(inst);
        }

        kdtree = new KDTree<>(data);
        EuclideanDistance eucl = new EuclideanDistance();
        naive = new LinearSearch<>(data, eucl);
    }

    @Test
    public void testNear() {
        System.out.println("data: " + data.toString());
        for (int i = 0; i < data.size(); i++) {
            System.out.println("testing " + i);
            Neighbor<E> n1 = kdtree.nearest(data.get(i));
            Neighbor<E> n2 = naive.nearest(data.get(i));
            System.out.println("item: " + data.get(i).toString() + ", ID = " + data.get(i).getIndex());
            System.out.println("naive = " + n2.index + ", kd = " + n1.index);
            System.out.println("naive = " + n2.distance + ", kd = " + n1.distance);
            assertEquals(n1.index, n2.index);
            assertEquals(n1.key, n2.key);
            assertEquals(n1.distance, n2.distance, DELTA);
        }
    }

    @Test
    public void testNearest() {
        Dataset<? extends Instance> d = insectDataset();
        subject = new KDTree(d);

        assertEquals(d.get(6), subject.nearest(d.get(0)).key);
    }

    @Test
    public void testNn() {
        Dataset<E> d = (Dataset<E>) irisDataset();
        subject = new KDTree();
        subject.setDataset(d);

        LinearSearch<E> refSearch = new LinearSearch(d);
        int k = 5;
        //4.9,3.1,1.5,0.1, Iris-setosa
        E ref = d.get(9);
        Neighbor[] nn = subject.knn(ref, k);
        Neighbor[] nn2 = refSearch.knn(ref, k);
        assertEquals(k, nn.length);
        E inst;
        //there are 3 same instances iris dataset

        for (int i = 0; i < 5; i++) {
            inst = (E) nn[i].key;
            if (i < 2) {
                //should find two very same instances (id: 34, 37)
                assertEquals(0.0, nn[i].distance, DELTA);
                assertEquals(0.0, nn2[i].distance, DELTA);
            }
            assertNotNull(inst);
        }
        assertEquals(k, nn.length);
    }

}
