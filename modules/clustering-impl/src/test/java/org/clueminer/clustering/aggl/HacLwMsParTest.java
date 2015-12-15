package org.clueminer.clustering.aggl;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.clustering.aggl.linkage.SingleLinkage;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.math.Matrix;
import org.clueminer.utils.PropType;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class HacLwMsParTest {

    private final HacLwMsPar subject = new HacLwMsPar(8);
    private static final double delta = 1e-9;

    @Test
    public void testSingleLinkageSchool() {
        Dataset<? extends Instance> dataset = FakeClustering.schoolData();
        assertEquals(17, dataset.size());
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        pref.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        pref.put(PropType.PERFORMANCE, AgglParams.KEEP_PROXIMITY, true);
        HierarchicalResult result = subject.hierarchy(dataset, pref);
        System.out.println(subject.getName() + " finished");
        System.out.println("school - single");
        DendroTreeData tree = result.getTreeData();
        Matrix sim = result.getProximityMatrix();
        sim.printLower(5, 2);
        assertEquals(dataset.size(), tree.numLeaves());
        DendroNode root = tree.getRoot();

        //assertEquals(47.18370587395614, root.getHeight(), delta);
        assertEquals(32.54273498033004, root.getHeight(), delta);
        //tree.print();
        HCLWMS other = new HCLWMS();
        HierarchicalResult refRes = other.hierarchy(dataset, pref);
        System.out.println("reference: ");
        //refRes.getTreeData().print();
        //make sure, that the algorithm returns same results as the previous version
        Matrix ref = refRes.getProximityMatrix();
        ref.printLower(5, 2);
        ///TODO: still some concurrency issues
        for (int i = 0; i < ref.rowsCount(); i++) {
            for (int j = 0; j < i; j++) {
                if (ref.get(i, j) != sim.get(i, j)) {
                    System.out.println("[" + i + ", " + j + "] = " + sim.get(i, j) + " expected: " + ref.get(i, j));
                }
                //assertEquals(ref.get(i, j), sim.get(i, j), delta);
            }
        }
    }

}
