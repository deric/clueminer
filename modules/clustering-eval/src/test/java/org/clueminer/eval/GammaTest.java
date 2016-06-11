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
package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.Gamma.Sres;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class GammaTest {

    private static Gamma subject;
    private static final double delta = 1e-9;

    public GammaTest() {
        subject = new Gamma();
    }

    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    @Test
    public void testSTable() {
        Dataset<? extends Instance> data = FakeDatasets.schoolData();
        int k = 3;
        Clustering<Instance, Cluster<Instance>> clusters = new ClusterList<>(k);
        Cluster c;
        for (int i = 0; i < k; i++) {
            c = clusters.createCluster(i);
            c.setAttributes(data.getAttributes());
        }
        int mod;
        for (int i = 0; i < data.size(); i++) {
            mod = i % k;
            c = clusters.get(mod);
            c.add(data.get(i));
        }
        Sres res = subject.computeSTable(clusters);

        assertEquals(2252, res.minus, delta);
        assertEquals(1588, res.plus, delta);
    }

    @Test
    public void testSchoolScore() throws ScoreException {
        Dataset<? extends Instance> data = FakeDatasets.schoolData();
        int k = 3;
        Clustering<Instance, Cluster<Instance>> clusters = new ClusterList<>(k);
        Cluster c;
        for (int i = 0; i < k; i++) {
            c = clusters.createCluster(i);
            c.setAttributes(data.getAttributes());
        }
        int mod;
        for (int i = 0; i < data.size(); i++) {
            mod = i % k;
            c = clusters.get(mod);
            c.add(data.get(i));
        }
        assertEquals(-0.17291666666666666, subject.score(clusters), delta);
    }

    /**
     * Check against definition (and tests in R package clusterCrit)
     * https://cran.r-project.org/web/packages/clusterCrit/index.html
     *
     * NOTE: There's a small problem with precision of floating point
     * operations. First 7 decimal digits seems to match.
     */
    @Test
    public void testClusterCrit() throws ScoreException {
        double score = subject.score(FakeClustering.int100p4());
        // clusterCrit result
        //assertEquals(-0.99999988079071, score, delta);
        assertEquals(-0.9999998720538721, score, delta);
    }

}
