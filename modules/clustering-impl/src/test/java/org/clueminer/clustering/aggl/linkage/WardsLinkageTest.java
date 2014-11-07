package org.clueminer.clustering.aggl.linkage;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class WardsLinkageTest extends AbstractLinkageTest {

    public WardsLinkageTest() {
        subject = new WardsLinkage();
    }


    @Test
    public void testDistance() {
    }

    @Test
    public void testSimilarity() {
    }


    @Test
    public void testAlphaA() {
        assertEquals(0.7, subject.alphaA(2, 3, 5), delta);
    }

    @Test
    public void testAlphaB() {
        assertEquals(0.833333333333, subject.alphaB(2, 4, 6), delta);
    }

    @Test
    public void testBeta() {
        assertEquals(-0.5, subject.beta(2, 3, 5), delta);
    }

    @Test
    public void testGamma() {
        assertEquals(0.0, subject.gamma(), delta);
    }

    @Test
    public void testLinkageKumar() {
        Dataset<? extends Instance> dataset = FakeClustering.kumarData();

        //HierarchicalResult naive = naiveLinkage(dataset);
        HierarchicalResult lance = lanceWilliamsLinkage(dataset);
        //assertEquals(true, TreeDiff.compare(naive, lance));
        System.out.println(dataset.getName() + " - " + subject.getName());
        DendroTreeData tree = lance.getTreeData();
        tree.print();
        assertEquals(dataset.size(), tree.numLeaves());
        DendroNode root = tree.getRoot();
        //   assertEquals(32.542734980330046, root.getHeight(), delta);
        //   assertEquals(32.542734980330046, lance.getTreeData().getRoot().getHeight(), delta);
        assertEquals(2 * dataset.size() - 1, tree.numNodes());
    }

}
