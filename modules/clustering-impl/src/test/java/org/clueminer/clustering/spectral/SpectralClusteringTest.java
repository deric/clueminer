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
package org.clueminer.clustering.spectral;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class SpectralClusteringTest<E extends Instance> {

    private final SpectralClustering subject;

    public SpectralClusteringTest() {
        subject = new SpectralClustering();
    }

    @Test
    public void testCluster() {
        Dataset<E> dataset = (Dataset<E>) FakeClustering.irisDataset();
        Props params = new Props();
        params.putInt(KMeans.K, 3);
        Clustering clustering = subject.cluster(dataset, params);
        assertEquals(1, clustering.size()); //TODO: fix this
        //assertEquals(dataset.size(), clustering.instancesCount());
    }

}
