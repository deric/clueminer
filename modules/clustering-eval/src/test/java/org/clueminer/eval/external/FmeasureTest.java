package org.clueminer.eval.external;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class FmeasureTest {

    private static Fmeasure subject;
    private static final double delta = 1e-9;

    public FmeasureTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        subject = new Fmeasure();
    }

    /**
     * Test of getName method, of class Fmeasure.
     */
    @Test
    public void testGetName() {
    }

    /**
     * Test of countScore method, of class Fmeasure.
     */
    @Test
    public void testCountScore() {
    }

    /**
     * Test of score method, of class Fmeasure.
     */
    @Test
    public void testScore_Clustering_Clustering() {
    }

    /**
     * Test of score method, of class Fmeasure.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        long start, end;
        double score;

        start = System.currentTimeMillis();
        score = subject.score(FakeClustering.wineClustering(), FakeClustering.wineCorrect());
        end = System.currentTimeMillis();

        //each cluster should have this scores:
        //Cabernet = 0.7200
        //Syrah = 0.5555
        //Pinot = 0.7272
        assertEquals(0.6676094276094275, score, delta);
        System.out.println(subject.getName() + " = " + score);
        System.out.println("measuring " + subject.getName() + " took " + (end - start) + " ms");

        start = System.currentTimeMillis();
        double score2 = subject.score(FakeClustering.wineClustering(), FakeClustering.wine());
        end = System.currentTimeMillis();
        //when using class labels result should be the same
        assertEquals(score, score2, delta);
        System.out.println(subject.getName() + " = " + score2);
        System.out.println("measuring " + subject.getName() + " took " + (end - start) + " ms");
    }

    /**
     * Test of score method, of class Fmeasure.
     */
    @Test
    public void testScore_3args() {
    }

    /**
     * Test of compareScore method, of class Fmeasure.
     */
    @Test
    public void testCompareScore() {
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
        assertEquals(true, score < 0.2);
    }
}
