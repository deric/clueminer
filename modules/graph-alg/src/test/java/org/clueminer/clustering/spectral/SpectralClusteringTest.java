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

import com.google.common.graph.Graph;
import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;

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
    public void testEpsCluster() {
        Dataset<E> dataset = (Dataset<E>) FakeClustering.jainDataset();
        Props params = new Props();
        params.putInt(SpectralClustering.K, 2);
        params.put(SpectralClustering.SP_ALG, "Normalized SP Shi&Malik");
        params.putDouble(SpectralClustering.SIGMA, 2);
        params.put(SpectralClustering.MATRIX_CONV, "epsilon-neighborhood matrix");
        params.put(SpectralClustering.EPS, 0.3);

        Clustering clustering = subject.cluster(dataset, params);
        assertEquals(2, clustering.size());
//        assertEquals(276, clustering.get(0).size());
//        assertEquals(97, clustering.get(1).size());
        assertEquals(dataset.size(), clustering.instancesCount());
    }

    @Test
    public void testJainEpsGraphCluster() {
        Dataset<E> dataset = (Dataset<E>) FakeClustering.jainDataset();
        Props params = new Props();
        params.putInt(SpectralClustering.K, 2);
        params.putDouble(SpectralClustering.SIGMA, 2);
        //params.put(SpectralClustering.EPS, 0.3);

        Clustering clustering = subject.cluster(dataset, params);
        assertEquals("[1,372]", clustering.fingerprint());

        assertEquals(dataset.size(), clustering.instancesCount());
    }

    @Test
    public void testJainDirectedKnnGraphCluster() {
        Dataset<E> dataset = (Dataset<E>) FakeClustering.jainDataset();
        Props params = new Props();
        params.putInt(SpectralClustering.K, 2);
        params.putDouble(SpectralClustering.SIGMA, 2.5);
        params.put(SpectralClustering.MATRIX_CONV, "directed k-neighborhood matrix");
        params.put(SpectralClustering.EPS, 0.3);

        Clustering clustering = subject.cluster(dataset, params);
        //assertEquals("[27,346]", clustering.fingerprint());

        assertEquals(dataset.size(), clustering.instancesCount());
    }
//    @Test
//    public void testSpiralDirectedKnnCluster() {
//        Dataset<E> dataset = (Dataset<E>) FakeClustering.spiralDataset();
//        Props params = new Props();
//        params.putInt(SpectralClustering.K, 2);
//        params.putDouble(SpectralClustering.Sigma, 0.4);
//        params.putInt(SpectralClustering.KnnK, 3);
//
//        Clustering clustering = subject.cluster(dataset, params);
//        assertEquals(2, clustering.size());
//        assertEquals(500, clustering.get(0).size());
//        assertEquals(500, clustering.get(1).size());
//        assertEquals(dataset.size(), clustering.instancesCount());
//    }
//    @Test
//    public void testJainUndirectedKnnGraphCluster() {
//        Dataset<E> dataset = (Dataset<E>) FakeClustering.jainDataset();
//        Props params = new Props();
//        params.putInt(SpectralClustering.K, 2);
//        params.putDouble(SpectralClustering.SIGMA, 3);
//        params.putInt(SpectralClustering.KNNK, 4);
//
//        Clustering clustering = subject.cluster(dataset, params);
//        assertEquals(2, clustering.size());
//        assertEquals(276, clustering.get(0).size());
//        assertEquals(97, clustering.get(1).size());
//        assertEquals(dataset.size(), clustering.instancesCount());
//    }
}
