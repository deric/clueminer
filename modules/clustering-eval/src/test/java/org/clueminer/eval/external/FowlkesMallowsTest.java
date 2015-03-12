package org.clueminer.eval.external;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class FowlkesMallowsTest extends ExternalTest {

    private static Clustering irisCorrect;
    private static Clustering irisWrong;

    public FowlkesMallowsTest() throws FileNotFoundException, IOException {
        irisCorrect = FakeClustering.iris();
        irisWrong = FakeClustering.irisWrong2();
        subject = new FowlkesMallows();
    }

    /**
     * Test of score method, of class FowlkesMallows.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        //this is fixed clustering which correspods to true classes in dataset
        measure(irisCorrect, FakeDatasets.irisDataset(), 1.0);

        measure(irisWrong, FakeDatasets.irisDataset(), 0.49390115014267694);

    }

    /**
     * Test of isBetter method, of class FowlkesMallows.
     */
    @Test
    public void testCompareScore() {
        //bigger is better
        assertTrue(subject.isBetter(2600, 2000));
    }

    /**
     * Test of score method, of class FowlkesMallows.
     */
    @Test
    public void testScore_Clustering_Clustering() {
        double score;
        score = measure(FakeClustering.wineClustering(), FakeClustering.wineCorrect(), 0.6688096636728896);

        //when using class labels result should be the same
        measure(FakeClustering.wineClustering(), FakeClustering.wine(), score);
    }

    @Test
    public void testOneClassPerCluster() {
        Clustering<Cluster> oneClass = new ClusterList(3);
        int size = 10;
        Dataset<? extends Instance> data = new ArrayDataset<>(size, 2);
        data.attributeBuilder().create("x1", "NUMERIC");
        data.attributeBuilder().create("x2", "NUMERIC");

        for (int i = 0; i < size; i++) {
            Instance inst = data.builder().create(new double[]{1, 2}, "same class");
            //cluster with single class
            BaseCluster clust = new BaseCluster(1);
            clust.add(inst);
            oneClass.add(clust);
        }
        assertEquals(0.0, subject.score(oneClass), delta);
    }

    @Test
    public void testMostlyWrong() {
        double score = subject.score(FakeClustering.irisMostlyWrong());
        System.out.println("f-m (mw): " + score);
        assertEquals(true, score < 0.4);
    }
}
