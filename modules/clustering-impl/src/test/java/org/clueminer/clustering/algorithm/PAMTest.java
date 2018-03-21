/*
 * Copyright (C) 2011-2018 clueminer.org
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
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class PAMTest {

    private final PAM subject;

    public PAMTest() {
        subject = new PAM();
    }

    @Test
    public void testCluster() {
        Dataset<? extends Instance> dataset = FakeClustering.irisDataset();
        Props params = new Props();
        params.putInt(KMeans.K, 3);
        Clustering clustering = subject.cluster(dataset, params);
        assertEquals(3, clustering.size());
        assertEquals(dataset.size(), clustering.instancesCount());
    }

}
