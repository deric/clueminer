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
import org.clueminer.neighbor.Neighbor;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class KnnTest extends AbstractNNTest {

    @Test
    public void testBenchmark() {
        CachingKNN t1 = new CachingKNN();
        LinearSearch t2 = new LinearSearch();
        Dataset<? extends Instance> d = irisDataset();
        t1.setDataset(d);
        t2.setDataset(d);
        int k = 5;
        for (int i = 0; i < d.size(); i++) {
            Instance ref = d.get(i);
            Neighbor[] nn1 = t1.knn(ref, k);
            Neighbor[] nn2 = t2.knn(ref, k);

            Instance inst1, inst2;
            for (int j = 0; j < k; j++) {
                inst1 = (Instance) nn1[j].key;
                inst2 = (Instance) nn2[j].key;
                assertEquals(inst1, inst2);
            }
        }
    }
}
