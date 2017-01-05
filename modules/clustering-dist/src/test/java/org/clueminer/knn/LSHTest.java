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
package org.clueminer.knn;

import java.util.HashSet;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.neighbor.Neighbor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 * @param <E>
 */
public class LSHTest<E extends Instance> extends AbstractNNTest {

    private LSH subject;

    @Before
    public void setUp() {
        subject = new LSH();
    }

    @Test
    public void testKnn_3args() {
        Dataset<E> d = (Dataset<E>) irisDataset();
        subject.setDataset(d);

        LinearSearch<E> refSearch = new LinearSearch(d);
        int k = 5;
        //4.9,3.1,1.5,0.1, Iris-setosa
        E ref = d.get(9);
        Neighbor[] nn = subject.knn(ref, k);
        Neighbor[] nn2 = refSearch.knn(ref, k);

        E inst;
        //there are 3 same instances iris dataset

        for (int i = 0; i < k; i++) {
            inst = (E) nn[i].key;
            if (i < 2) {
                //should find two very same instances (id: 34, 37)
                assertEquals(0.0, nn[i].distance, DELTA);
                assertEquals(0.0, nn2[i].distance, DELTA);
            }
            System.out.println(nn[i].distance + ", " + inst.getIndex() + ": " + inst.toString());
            assertNotNull(inst);
        }
        assertEquals(k, nn.length);
    }

    @Test
    public void testNoise() {
        Dataset<? extends Instance> d = irisDataset();
        subject.setDataset(d);
        HashSet<Integer> noise = new HashSet<>();
        noise.add(34);
        subject.setExclude(noise);
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
            //34 marked as noise
            assertNotEquals(34, inst.getIndex());
        }
        assertEquals(k, nn.length);
    }

}
