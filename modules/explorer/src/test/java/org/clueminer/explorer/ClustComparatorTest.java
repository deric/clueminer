package org.clueminer.explorer;

import org.clueminer.eval.CalinskiHarabasz;
import org.clueminer.eval.external.NMI;
import org.clueminer.eval.Silhouette;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openide.nodes.Node;

/**
 *
 * @author deric
 */
public class ClustComparatorTest {

    private final ClustComparator subject = new ClustComparator(new NMI());

    public ClustComparatorTest() {
    }

    @Test
    public void testCompare() {
        Node n1 = new ClusteringNode(FakeClustering.iris());
        Node n2 = new ClusteringNode(FakeClustering.irisWrong());
        subject.setAscOrder(true);
        //first one is "better"
        assertEquals(1, subject.compare(n1, n2));

        subject.setAscOrder(false);
        //first one is "better" (descending order)
        assertEquals(-1, subject.compare(n1, n2));

        //the very same clusterings
        assertEquals(0, subject.compare(n1, n1));
    }

    @Test
    public void testSetEvaluator() {
        subject.setEvaluator(new Silhouette());
        subject.setAscOrder(true);
        Node a = new ClusteringNode(FakeClustering.irisWrong2());
        Node b = new ClusteringNode(FakeClustering.irisWrong4());
        Node c = new ClusteringNode(FakeClustering.iris());

        //first one is "better" DESC, A > B
        assertEquals(1, subject.compare(a, b));
        //C > B
        assertEquals(1, subject.compare(c, b));
        assertEquals(1, subject.compare(c, a));
        subject.setEvaluator(new CalinskiHarabasz());
        assertEquals(1, subject.compare(a, b));
        assertEquals(1, subject.compare(c, b));
        assertEquals(1, subject.compare(c, a));
    }

    public void testNaN() {
        assertEquals(true, 0.1 > Double.NaN);
        assertEquals(false, Double.NaN > Double.NaN);
        assertEquals(true, Double.NaN == Double.NaN);
    }

}
