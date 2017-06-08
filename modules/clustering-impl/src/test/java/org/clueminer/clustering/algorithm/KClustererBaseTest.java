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
package org.clueminer.clustering.algorithm;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.HardAssignment;
import org.clueminer.clustering.api.Assignment;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class KClustererBaseTest {

    @Test
    public void testFromAssignment() {
        Dataset<? extends Instance> dataset = FakeClustering.irisDataset();

        int[] memebership = new int[dataset.size()];
        for (int i = 0; i < 50; i++) {
            memebership[i] = 0;
            memebership[i + 50] = 1;
            memebership[i + 100] = 2;
        }
        Assignment assign = new HardAssignment(memebership);
        assertEquals(3, assign.distinct());

        Clustering<Instance, Cluster<Instance>> clustering = KClustererBase.fromAssignment(assign, dataset);
        //we should have all instances
        assertEquals(dataset.size(), clustering.instancesCount());
        assertEquals(3, clustering.size());
    }

}
