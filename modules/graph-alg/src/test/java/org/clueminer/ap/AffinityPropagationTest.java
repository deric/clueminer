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

    public AffinityPropagationTest() {
        AP = new AffinityPropagation();
    }

    @Test
    public void testCluster() {
        Dataset data = FakeDatasets.schoolData();
        Props props = new Props();
        Clustering clust = AP.cluster(data, props);
        assertNotNull(clust);
        assertEquals(2, clust.size());
    }

    @Test
    public void testIris() {
        Dataset data = FakeDatasets.irisDataset();
        Props props = new Props();
        Clustering clust = AP.cluster(data, props);
        assertNotNull(clust);
        assertEquals(24, clust.size());
    }

    //@Test
    public void testSimilarity() {
        Dataset data = FakeDatasets.schoolData();
        Matrix s = AP.similarity(data);
        //s.printLower(2, 2);
        assertTrue("should contain negative values", s.get(0, 4) < 0);
    }

}
