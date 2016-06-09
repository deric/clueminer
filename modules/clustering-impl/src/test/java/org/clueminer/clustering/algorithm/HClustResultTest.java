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
package org.clueminer.clustering.algorithm;

import java.util.HashSet;
import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.aggl.HCLW;
import org.clueminer.clustering.aggl.linkage.SingleLinkage;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Dump;
import org.clueminer.utils.PropType;
import org.clueminer.utils.Props;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class HClustResultTest {

    private static AgglomerativeClustering algorithm;
    private static Dataset<? extends Instance> dataset;
    private static HierarchicalResult rowsResult;
    private static final double DELTA = 1e-9;

    public HClustResultTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        dataset = FakeClustering.irisDataset();
        algorithm = new HCLW();

        //prepare clustering
        //@TODO: this is too complex, there must be a one-line method for doing this
        Props pref = new Props();
        pref.put(AlgParams.LINKAGE, SingleLinkage.name);
        pref.put(PropType.PERFORMANCE, AlgParams.KEEP_PROXIMITY, true);
        rowsResult = algorithm.hierarchy(dataset, pref);
    }

    /**
     * Test of getProximityMatrix method, of class HClustResult.
     */
    @Test
    public void testGetProximityMatrix() {
        Matrix m = rowsResult.getProximityMatrix();
        assertEquals(dataset.size(), m.rowsCount());
        assertEquals(dataset.size(), m.columnsCount());

    }

    @Test
    public void testGetClusters() {
        Clustering c, prev = rowsResult.getClustering();
        c = rowsResult.getClustering();
        //clustering should be cached (not created on each request)
        assertEquals(prev, c);
    }

    /**
     * When we cut dendrogram at any height, we always must have same number of
     * instances in the dataset
     */
    @Test
    public void testUpdateCutoff() {
        double height = rowsResult.getMaxTreeHeight();
        assertEquals(height, rowsResult.getTreeData().getRoot().getHeight(), DELTA);
        double inc = 0.1;
        double cut = height;
        Clustering c, prev = null;
        while (cut > 0.0) {
            c = rowsResult.updateCutoff(cut);
            assertEquals(rowsResult.getDataset().size(), c.instancesCount());
            cut -= inc;
            if (prev != null) {
                assertNotSame(c, prev);
            }
            prev = c;
        }
        //magic constant
        cut = 0.7708573149422662;
        c = rowsResult.updateCutoff(cut);
        assertEquals(rowsResult.getDataset().size(), c.instancesCount());
        int[] clusters = rowsResult.getClusters(0);
        Dump.array(clusters, "clusters");
    }

    @Test
    public void testCutTreeByLevel() {
        rowsResult.findCutoff();
    }

    /**
     * Test of getMapping method, of class HClustResult.
     */
    @Test
    public void testGetMapping() {
        int[] mapping = rowsResult.getMapping();
        //number of leaves should be the same as number of rows in similarity matrix
        assertEquals(rowsResult.getProximityMatrix().rowsCount(), mapping.length);

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        //check that each mapped number is there just once
        HashSet<Integer> hash = new HashSet<>();

        for (int i = 0; i < mapping.length; i++) {
            assertEquals(false, hash.contains(mapping[i]));
            hash.add(mapping[i]);
            if (mapping[i] < min) {
                min = mapping[i];
            }

            if (mapping[i] > max) {
                max = mapping[i];
            }
        }
        //minimum value must be 0
        assertEquals(0, min);
        //all numbers of rows should be there
        assertEquals(mapping.length - 1, max);

        assertEquals(mapping.length, rowsResult.getDataset().size());
        Dump.array(mapping, "mapping");

        //@TODO implement tests
    }

}
