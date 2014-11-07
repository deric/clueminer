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
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class AverageLinkageTest extends AbstractLinkageTest {

    public AverageLinkageTest() {
        subject = new AverageLinkage();
    }
    @Test
    public void testDistance() {
    }

    @Test
    public void testSimilarity() {
    }

    @Test
    public void testAlphaA() {
        assertEquals(0.25, subject.alphaA(1, 3, 1), delta);
    }

    @Test
    public void testAlphaB() {
        assertEquals(0.75, subject.alphaB(1, 3, 1), delta);
    }

    @Test
    public void testBeta() {
        assertEquals(0.0, subject.beta(1, 2, 3), delta);
    }

    @Test
    public void testGamma() {
        assertEquals(0.0, subject.gamma(), delta);
    }

    @Test
    public void testAverageLinkageSchool() {
        Dataset<? extends Instance> dataset = FakeClustering.schoolData();
        assertEquals(17, dataset.size());

        HierarchicalResult naive = naiveLinkage(dataset);
        HierarchicalResult lance = lanceWilliamsLinkage(dataset);
        assertEquals(true, TreeDiff.compare(naive, lance));
        System.out.println("school - average");
        DendroTreeData tree = naive.getTreeData();
        tree.print();
        assertEquals(dataset.size(), tree.numLeaves());
        DendroNode root = tree.getRoot();
        assertEquals(64.62613681177399, root.getHeight(), delta);
        assertEquals(64.62613681177399, lance.getTreeData().getRoot().getHeight(), delta);
        assertEquals(2 * dataset.size() - 1, tree.numNodes());
    }

}
