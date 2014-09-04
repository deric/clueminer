package org.clueminer.explorer;

import org.clueminer.eval.CalinskiHarabasz;
import org.clueminer.eval.NMI;
import org.clueminer.eval.Silhouette;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
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

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
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
        Node n1 = new ClusteringNode(FakeClustering.irisWrong2());
        Node n2 = new ClusteringNode(FakeClustering.irisWrong4());

        //first one is "better" DESC
        assertEquals(-1, subject.compare(n1, n2));

        subject.setEvaluator(new CalinskiHarabasz());
        assertEquals(1, subject.compare(n1, n2));
    }

    public void testNaN() {
        assertEquals(true, 0.1 > Double.NaN);
        assertEquals(false, Double.NaN > Double.NaN);
        assertEquals(true, Double.NaN == Double.NaN);
    }

}
