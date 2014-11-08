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
 * @author deric
 */
public class JaccardIndexTest {

    private static Clustering clusters;
    private static Clustering iris;
    private static JaccardIndex subject;
    private static final double delta = 1e-9;

    public JaccardIndexTest() throws FileNotFoundException, IOException {
        clusters = FakeClustering.iris();
        iris = FakeClustering.irisWrong();
    }

    @BeforeClass
    public static void setUpClass() {
        subject = new JaccardIndex();
    }

    /**
     * Test of score method, of class JaccardIndex.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        double score = subject.score(clusters, FakeDatasets.irisDataset());
        //this is fixed clustering which correspods to true classes in dataset
        assertEquals(1.0, score, delta);
        System.out.println("jaccard index = " + score);

        //delta here depends on random initialization of k-means
        long start = System.currentTimeMillis();
        score = subject.score(iris, FakeDatasets.irisDataset());
        long end = System.currentTimeMillis();
        //it should be 0.8045 or 0.81...
        assertEquals(0.15032686686154664, score, delta);
        System.out.println("jaccard index = " + score);
        System.out.println("measuring Jaccard took " + (end - start) + " ms");

        Clustering<Cluster> irisWrong2 = FakeClustering.irisWrong2();
        score = subject.score(irisWrong2, FakeDatasets.irisDataset());
        assertEquals(0.3666666666666667, score, delta);
        System.out.println("jaccard index (wrong clust2) = " + score);

    }

    /**
     * Test of score method, of class JaccardIndex.
     */
    @Test
    public void testScore_3args() {
    }

    /**
     * Test of compareScore method, of class JaccardIndex.
     */
    @Test
    public void testCompareScore() {
    }

    /**
     * Test of score method, of class JaccardIndex.
     */
    @Test
    public void testScore_Clustering_Clustering() {
        long start, end;
        double score;

        start = System.currentTimeMillis();
        score = subject.score(FakeClustering.wineClustering(), FakeClustering.wineCorrect());
        end = System.currentTimeMillis();

        //each cluster should have this scores:
        //Cabernet = 0.5625
        //Syrah = 0.3846
        //Pinot = 0.5714
        assertEquals(0.5061813186813187, score, delta);
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
