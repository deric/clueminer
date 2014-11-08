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
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class RandIndexTest {

    private static Clustering irisCorrect;
    private static Clustering irisWrong;
    private static RandIndex subject;
    private static final double delta = 1e-9;

    public RandIndexTest() throws FileNotFoundException, IOException {
        subject = new RandIndex();
        irisCorrect = FakeClustering.iris();
        irisWrong = FakeClustering.irisWrong2();
    }

    /**
     * Test of getName method, of class RandIndex.
     */
    @Test
    public void testGetName() {
    }

    /**
     * Test of score method, of class RandIndex.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        long start, end;
        start = System.currentTimeMillis();
        double score = subject.score(irisCorrect, FakeDatasets.irisDataset());
        end = System.currentTimeMillis();
        System.out.println("AdjustedRand = " + score);
        System.out.println("measuring AdjustedRand took " + (end - start) + " ms");
        //this is the ideal case
        assertEquals(1.0, score, delta);

        start = System.currentTimeMillis();
        score = subject.score(irisWrong, FakeDatasets.irisDataset());
        end = System.currentTimeMillis();
        assertEquals(0.6888888888888888, score, delta);
        System.out.println("AdjustedRand = " + score);
        System.out.println("measuring AdjustedRand took " + (end - start) + " ms");

        //this clustering shouldn't be better than the previous one, 142 items are in one
        //cluster, so not really the best solution - though the coefficient would prefere this one
        start = System.currentTimeMillis();
        score = subject.score(FakeClustering.irisWrong(), FakeDatasets.irisDataset());
        end = System.currentTimeMillis();
        assertEquals(0.5777777777777778, score, delta);
        System.out.println("AdjustedRand = " + score);
        System.out.println("measuring AdjustedRand took " + (end - start) + " ms");
    }

    /**
     * Test of score method, of class RandIndex.
     */
    @Test
    public void testScore_3args() {
    }

    /**
     * Test of compareScore method, of class RandIndex.
     */
    @Test
    public void testCompareScore() {
        //one should be the best value
        assertTrue(subject.compareScore(1.0, 0.0));
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
        System.out.println("mostly wrong: " + score);
        assertEquals(true, score < 0.2);
    }
}
