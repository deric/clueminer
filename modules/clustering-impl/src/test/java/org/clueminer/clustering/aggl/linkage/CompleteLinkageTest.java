package org.clueminer.clustering.aggl.linkage;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.TreeDiff;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class CompleteLinkageTest extends AbstractLinkageTest {

    public CompleteLinkageTest() {
        subject = new CompleteLinkage();
    }

    @Test
    public void testDistance() {
        Dataset<? extends Instance> dataset = FakeClustering.kumarData();

        Cluster a = new BaseCluster(2);
        a.add(dataset.get(2));
        a.add(dataset.get(5));
        Cluster b = new BaseCluster(2);
        b.add(dataset.get(1));
        b.add(dataset.get(4));

        //max distance between all items in the cluster
        double dist = subject.distance(a, b);
        assertEquals(0.38600518131237566, dist, delta);
        //transitive measure
        double dist2 = subject.distance(b, a);
        assertEquals(dist, dist2, delta);

        Cluster c = new BaseCluster(1);
        c.add(dataset.get(3));
        dist = subject.distance(a, c);
        assertEquals(0.21954498400100148, dist, delta);
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
        assertEquals(0.5, subject.gamma(), delta);
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
        assertEquals(dataset.size(), tree.numLeaves());
        assertEquals(121.11422748793802, tree.getRoot().getHeight(), delta);
        assertEquals(121.11422748793802, lance.getTreeData().getRoot().getHeight(), delta);
        assertEquals(2 * dataset.size() - 1, tree.numNodes());
    }

    /**
     * Make sure that naive approach and Lance-Williams gives same results
     */
    @Test
    public void testLinkageKumar() {
        Dataset<? extends Instance> dataset = FakeClustering.kumarData();

        HierarchicalResult naive = naiveLinkage(dataset);
        HierarchicalResult lance = lanceWilliamsLinkage(dataset);
        assertEquals(true, TreeDiff.compare(naive, lance));
        System.out.println("school - " + subject.getName());
        DendroTreeData tree = naive.getTreeData();
        assertEquals(dataset.size(), tree.numLeaves());
        assertEquals(0.38600518131237566, tree.getRoot().getHeight(), delta);
        assertEquals(2 * dataset.size() - 1, tree.numNodes());
    }

}
