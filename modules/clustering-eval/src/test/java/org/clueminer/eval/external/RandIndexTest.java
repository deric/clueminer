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
public class RandIndexTest extends ExternalTest {

    private static Clustering irisCorrect;
    private static Clustering irisWrong;

    public RandIndexTest() throws FileNotFoundException, IOException {
        subject = new RandIndex();
        irisCorrect = FakeClustering.iris();
        irisWrong = FakeClustering.irisWrong2();
    }

    /**
     * Test of score method, of class RandIndex.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        measure(irisCorrect, FakeDatasets.irisDataset(), 1.0);

        measure(irisWrong, FakeDatasets.irisDataset(), 0.6888888888888888);

        //this clustering shouldn't be better than the previous one, 142 items are in one
        //cluster, so not really the best solution - though the coefficient would prefere this one
        measure(FakeClustering.irisWrong(), FakeDatasets.irisDataset(), 0.5777777777777778);
    }

    /**
     * Test of isBetter method, of class RandIndex.
     */
    @Test
    public void testCompareScore() {
        //one should be the best value
        assertTrue(subject.isBetter(1.0, 0.0));
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
        assertEquals(0.0, subject.score(oneClass, data), delta);
    }

    @Test
    public void testMostlyWrong() {
        double score = subject.score(FakeClustering.irisMostlyWrong(), FakeClustering.iris());
        System.out.println("rand (mw): " + score);
        assertEquals(true, score < 0.3);
    }
}
