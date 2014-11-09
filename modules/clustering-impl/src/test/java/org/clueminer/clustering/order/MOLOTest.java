package org.clueminer.clustering.order;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.aggl.HACLW;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Dump;
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
    private static final double delta = 1e-9;
    private static MOLO subject;

    @BeforeClass
    public static void setUpClass() {
        subject = new MOLO();
        dataset = FakeClustering.irisDataset();
        algorithm = new HACLW();

        //prepare clustering
        //@TODO: this is too complex, there must be a one-line method for doing this
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, "Single Linkage");
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
        assertEquals(height, rowsResult.getTreeData().getRoot().getHeight(), delta);
        double inc = 0.1;
        double cut = height;
        Clustering c = null, prev = null;
        int[] clusters;
        while (cut > 0.0) {
            c = rowsResult.updateCutoff(cut);
            Dump.array(c.clusterSizes(), "cluster sizes " + cut);
            ///rowsResult.getTreeData().print();
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
    public void testSortSmallest() {
    }

    @Test
    public void testSortSmallestReverse() {
        //subject.optimize(rowsResult, true);
    }

    @Test
    public void testInOrderScore() {
    }

}
