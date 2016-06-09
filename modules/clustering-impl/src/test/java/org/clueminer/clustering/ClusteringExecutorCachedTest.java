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
package org.clueminer.clustering;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ClusteringExecutorCachedTest {

    private final ClusteringExecutorCached subject = new ClusteringExecutorCached();

    @Ignore
    public void testHclustRows() {
        Props pref = new Props();
        Clustering<Instance, Cluster<Instance>> clust = subject.clusterRows(FakeClustering.irisDataset(), pref);
        assertNotNull(clust);
        //cutoff implementation is needed
    }

    @Test
    public void testHclustColumns() {
        Distance dm = new EuclideanDistance();
        Props pref = new Props();
        HierarchicalResult hres = subject.hclustRows(FakeClustering.irisDataset(), pref);
        assertNotNull(hres);
        assertEquals(150, hres.size());
    }

    @Test
    public void testClusterRows() {
    }

    //TODO: move some cutoff strategy to this package
    @Ignore
    public void testClusterAll() {
        Distance dm = new EuclideanDistance();
        Props pref = new Props();
        DendrogramMapping mapping = subject.clusterAll(FakeClustering.irisDataset(), pref);
        assertNotNull(mapping);
        HierarchicalResult rows = mapping.getRowsResult();
        Matrix mr = rows.getProximityMatrix();
        assertEquals(150, mr.rowsCount());
        assertEquals(150, mr.columnsCount());
        HierarchicalResult cols = mapping.getColsResult();
        assertEquals(150, rows.size());
        assertEquals(4, cols.size());
        Matrix mc = cols.getProximityMatrix();
        assertEquals(4, mc.rowsCount());
        assertEquals(4, mc.columnsCount());
    }

}
