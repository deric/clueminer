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
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tombart
 */
public class PrecisionTest {

    private static Clustering clusters;
    private static Clustering iris;
    private static Precision subject;
    private static final double delta = 1e-9;

    public PrecisionTest() throws FileNotFoundException, IOException {

        clusters = FakeClustering.iris();
        iris = FakeClustering.irisWrong();
    }

    @BeforeClass
    public static void setUpClass() {
        subject = new Precision();
    }

    /**
     * Test of score method, of class Precision.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        double score = subject.score(clusters, FakeDatasets.irisDataset());
        //this is fixed clustering which correspods to true classes in dataset
        assertEquals(1.0, score, delta);
        System.out.println(subject.getName() + " = " + score);

        long start = System.currentTimeMillis();
        score = subject.score(iris, FakeDatasets.irisDataset());
        long end = System.currentTimeMillis();

        assertEquals(0.36666666666, score, delta);
        System.out.println(subject.getName() + " = " + score);
        System.out.println("measuring " + subject.getName() + " took " + (end - start) + " ms");
    }

    /**
     * Test of score method, of class Precision.
     */
    @Test
    public void testScore_3args() {
    }

    /**
     * Test of compareScore method, of class Precision.
     */
    @Test
    public void testCompareScore() {
        //first one should be better
        assertEquals(true, subject.compareScore(1.0, 0.4));
    }

    @Test
    public void testOneClassPerCluster() {
        Clustering<Cluster> oneClass = new ClusterList(3);
        int size = 3;
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

    /**
     * Test of score method, of class Precision.
     */
    @Test
    public void testScore_Clustering_Clustering() {
        long start, end;
        start = System.currentTimeMillis();
        double score = subject.score(FakeClustering.iris(), FakeClustering.iris());
        end = System.currentTimeMillis();
        //this is fixed clustering which correspods to true classes in dataset
        assertEquals(1.0, score, delta);
        System.out.println("precision  = " + score);
        System.out.println("measuring precision took " + (end - start) + " ms");

        start = System.currentTimeMillis();
        score = subject.score(FakeClustering.irisWrong(), FakeClustering.iris());
        end = System.currentTimeMillis();

        assertEquals(0.3666666666666667, score, delta);
        System.out.println(subject.getName() + " = " + score);
        System.out.println("measuring " + subject.getName() + " took " + (end - start) + " ms");

        start = System.currentTimeMillis();
        score = subject.score(FakeClustering.irisWrong2(), FakeClustering.iris());
        end = System.currentTimeMillis();

        assertEquals(0.5333333333333333, score, delta);
        System.out.println(subject.getName() + " = " + score);
        System.out.println("measuring " + subject.getName() + " took " + (end - start) + " ms");

        start = System.currentTimeMillis();
        score = subject.score(FakeClustering.wineClustering(), FakeClustering.wineCorrect());
        end = System.currentTimeMillis();

        //each cluster should have this scores:
        //Cabernet = 0.6923
        //Syrah = 0.5555
        //Pinot = 0.8000
        assertEquals(0.6826210826210826, score, delta);
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
     * Test of countScore method, of class Precision.
     */
    @Test
    public void testCountScore() {
        double score = subject.score(FakeClustering.irisMostlyWrong(), FakeClustering.iris());
        assertEquals(true, score < 0.4);
        System.out.println("mostly wrong precision  = " + score);
    }
}
