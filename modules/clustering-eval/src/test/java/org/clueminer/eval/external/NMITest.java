package org.clueminer.eval.external;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class NMITest extends ExternalTest {

    private static Clustering irisCorrect;
    private static Clustering irisWrong;

    public NMITest() throws FileNotFoundException, IOException {
        irisCorrect = FakeClustering.iris();
        irisWrong = FakeClustering.irisWrong2();
        subject = new NMI();
    }

    /**
     * Test of score method, of class NMI.
     */
    @Test
    public void testScore_Clustering_Clustering() {
        //this is fixed clustering which correspods to true classes in dataset
        measure(FakeClustering.iris(), FakeClustering.iris(), 1.0);

        double score = measure(irisWrong, irisCorrect, 0.6496820278112178);

        double score2 = measure(FakeClustering.irisWrong(), irisCorrect, 0.06793702240876041);
        assertTrue(score2 < score);
    }

    /**
     * Test of score method, of class NMI.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        measure(FakeClustering.iris(), 1.0);

        double score = measure(irisWrong, 0.6496820278112178);
        double score2 = measure(FakeClustering.irisWrong(), irisCorrect, 0.06793702240876041);

        assertTrue(score2 < score);
    }

    /**
     * Test of isBetter method, of class NMI.
     */
    @Test
    public void testCompareScore() {
        //one is better than zero
        assertTrue(subject.isBetter(1.0, 0.0));
        assertTrue(subject.isBetter(1.0, 0.5));
        assertTrue(subject.isBetter(1.0, 0.9999));
    }

    /**
     * TODO: make sure this test is correct
     */
    @Ignore
    public void testScore() {
        Clustering c = new ClusterList(2);
        Dataset<? extends Instance> d = new ArrayDataset(8, 2);
        d.builder().create(new double[]{0, 0}, "0");
        d.builder().create(new double[]{0, 0}, "0");
        d.builder().create(new double[]{0, 0}, "0");
        d.builder().create(new double[]{1, 1}, "0");
        d.builder().create(new double[]{1, 1}, "1");
        d.builder().create(new double[]{1, 1}, "1");
        d.builder().create(new double[]{1, 1}, "1");
        d.builder().create(new double[]{1, 1}, "1");
        assertEquals(8, d.size());
        Cluster a = c.createCluster(0, 4);
        Cluster b = c.createCluster(1, 4);
        for (int i = 0; i < 4; i++) {
            a.add(d.get(i));
            b.add(d.get(i + 4));
        }
        assertEquals(2, c.size());
        assertEquals(0.14039740914097984, subject.score(c), delta);
    }
}
