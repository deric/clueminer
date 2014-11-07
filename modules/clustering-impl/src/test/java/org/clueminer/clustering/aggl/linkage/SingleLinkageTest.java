package org.clueminer.clustering.aggl.linkage;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.TreeDiff;
import org.clueminer.clustering.aggl.HAC;
import org.clueminer.clustering.aggl.HACLW;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class SingleLinkageTest {

    private final SingleLinkage subject = new SingleLinkage();
    private final double delta = 1e-9;
    private static final HAC hac = new HAC();
    private static final HACLW haclw = new HACLW();

    @Test
    public void testDistance() {
    }

    @Test
    public void testSimilarity() {
    }

    private HierarchicalResult naiveLinkage(Dataset<? extends Instance> dataset) {
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, subject.getName());
        pref.putBoolean(AgglParams.CLUSTER_ROWS, true);
        HierarchicalResult result = hac.hierarchy(dataset, pref);
        return result;
    }

    private HierarchicalResult lanceWilliamsLinkage(Dataset<? extends Instance> dataset) {
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, subject.getName());
        pref.putBoolean(AgglParams.CLUSTER_ROWS, true);
        HierarchicalResult result = haclw.hierarchy(dataset, pref);
        return result;
    }

    @Test
    public void testLinkageSchool() {
        Dataset<? extends Instance> dataset = FakeClustering.schoolData();
        assertEquals(17, dataset.size());

        HierarchicalResult naive = naiveLinkage(dataset);
        HierarchicalResult lance = lanceWilliamsLinkage(dataset);
        assertEquals(true, TreeDiff.compare(naive, lance));
        System.out.println("school - " + subject.getName());
        DendroTreeData tree = naive.getTreeData();
        tree.print();
        assertEquals(dataset.size(), tree.numLeaves());
        DendroNode root = tree.getRoot();
        assertEquals(32.542734980330046, root.getHeight(), delta);
        assertEquals(32.542734980330046, lance.getTreeData().getRoot().getHeight(), delta);
        assertEquals(2 * dataset.size() - 1, tree.numNodes());
    }

    /**
     * TODO: this might fail because of same distance between nodes: 0.14
     */
    //@Test
    public void testLinkageKumar() {
        Dataset<? extends Instance> dataset = FakeClustering.kumarData();

        HierarchicalResult naive = naiveLinkage(dataset);
        HierarchicalResult lance = lanceWilliamsLinkage(dataset);
        assertEquals(true, TreeDiff.compare(naive, lance));
        System.out.println(dataset.getName() + " - " + subject.getName());
        DendroTreeData tree = naive.getTreeData();
        tree.print();
        assertEquals(dataset.size(), tree.numLeaves());
        DendroNode root = tree.getRoot();
     //   assertEquals(32.542734980330046, root.getHeight(), delta);
        //   assertEquals(32.542734980330046, lance.getTreeData().getRoot().getHeight(), delta);
        assertEquals(2 * dataset.size() - 1, tree.numNodes());
    }

    @Test
    public void testAlphaA() {
        assertEquals(0.5, subject.alphaA(1, 3, 1), delta);
    }

    @Test
    public void testAlphaB() {
        assertEquals(0.5, subject.alphaB(1, 3, 1), delta);
    }

    @Test
    public void testBeta() {
        assertEquals(0.0, subject.beta(1, 2, 3), delta);
    }

    @Test
    public void testGamma() {
        assertEquals(-0.5, subject.gamma(), delta);
    }

}
