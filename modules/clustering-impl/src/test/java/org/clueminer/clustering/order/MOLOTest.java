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
package org.clueminer.clustering.order;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.aggl.HCLW;
import org.clueminer.clustering.aggl.linkage.SingleLinkage;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Dump;
import org.clueminer.utils.PropType;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class MOLOTest {

    private static AgglomerativeClustering algorithm;
    private static Dataset<? extends Instance> dataset;
    private static HierarchicalResult rowsResult;
    private static final double DELTA = 1e-9;
    private static MOLO subject;

    @BeforeClass
    public static void setUpClass() {
        subject = new MOLO();
        dataset = FakeClustering.irisDataset();
        algorithm = new HCLW();

        //prepare clustering
        //@TODO: this is too complex, there must be a one-line method for doing this
        Props pref = new Props();
        pref.put(AlgParams.LINKAGE, SingleLinkage.name);
        pref.put(PropType.PERFORMANCE, AlgParams.KEEP_PROXIMITY, true);
        rowsResult = algorithm.hierarchy(dataset, pref);
        subject.optimize(rowsResult, false);
    }

    @Test
    public void testOptimize_HierarchicalResult_boolean() {
    }

    //TODO fix MOLO ordering
    //@Test
    public void testOptimize_HierarchicalResult() {
        double height = rowsResult.getMaxTreeHeight();
        assertEquals(height, rowsResult.getTreeData().getRoot().getHeight(), DELTA);
        double inc = 0.1;
        double cut = height;
        Clustering c, prev = null;
        int[] clusters;
        int numNodes = 2 * rowsResult.getDataset().size() - 1;
        while (cut > 0.0) {
            c = rowsResult.updateCutoff(cut);
            Dump.array(c.clusterSizes(), "cluster sizes " + cut);
            ///rowsResult.getTreeData().print();
            assertEquals(rowsResult.getDataset().size(), rowsResult.getTreeData().numLeaves());
            assertEquals(numNodes, rowsResult.getTreeData().numNodes());
            assertEquals(rowsResult.getDataset().size(), c.instancesCount());
            cut -= inc;
            System.out.println("cut = " + cut);
            if (prev != null) {
                assertNotSame(c, prev);
            }
            clusters = rowsResult.getClusters(0);
            assertEquals(clusters.length, rowsResult.getDataset().size());
            Dump.array(clusters, "clusters " + cut);
            prev = c;
        }

    }

    @Test
    public void testSortSmallestReverse() {
        //subject.optimize(rowsResult, true);
    }


}
