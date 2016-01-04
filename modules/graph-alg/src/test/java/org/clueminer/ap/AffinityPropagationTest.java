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
package org.clueminer.ap;

import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class AffinityPropagationTest {

    private static AffinityPropagation AP;
    private static final double DELTA = 1e-5;

    public AffinityPropagationTest() {
        AP = new AffinityPropagation();
    }

    @Test
    public void testCluster() {
        Dataset data = FakeDatasets.schoolData();
        Props props = new Props();
        props.put(Algorithm.DISTANCE, "Negative Euclidean");
        Clustering clust = AP.cluster(data, props);
        assertNotNull(clust);
        assertEquals(-923.6858499999972, props.getDouble(AffinityPropagation.PREFERENCE), DELTA);
        assertEquals(3, clust.size());
    }

    @Test
    public void testGaussians1() {
        Dataset data = FakeDatasets.gaussians1();
        Props props = new Props();
        props.put(Algorithm.DISTANCE, "Negative Euclidean");
        props.putInt(AffinityPropagation.MAX_ITERATIONS, 100);
        props.putDouble(AffinityPropagation.DAMPING, 0.9);
        Clustering clust = AP.cluster(data, props);
        assertNotNull(clust);
        assertEquals(2, clust.size());
        assertEquals(-0.2826022, props.getDouble(AffinityPropagation.PREFERENCE), DELTA);
    }

    @Test
    public void testBlobs() {
        Dataset data = FakeDatasets.blobs();
        Props props = new Props();
        props.put(Algorithm.DISTANCE, "Negative Euclidean");
        props.putInt(AffinityPropagation.MAX_ITERATIONS, 100);
        props.putDouble(AffinityPropagation.DAMPING, 0.7);
        Clustering clust = AP.cluster(data, props);
        assertNotNull(clust);
        assertEquals(14, clust.size());
        assertEquals(-3.831947304102332, props.getDouble(AffinityPropagation.PREFERENCE), DELTA);
    }

    @Test
    public void testIris() {
        Dataset data = FakeDatasets.irisDataset();
        Props props = new Props();
        Clustering clust = AP.cluster(data, props);
        assertNotNull(clust);
        assertEquals(2, clust.size());
    }

    //@Test
    public void testSimilarity() {
        Dataset data = FakeDatasets.schoolData();
        Matrix s = AP.similarity(data);
        //s.printLower(2, 2);
        assertTrue("should contain negative values", s.get(0, 4) < 0);
    }

}
