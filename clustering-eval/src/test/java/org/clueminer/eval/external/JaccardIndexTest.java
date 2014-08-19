package org.clueminer.eval.external;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class JaccardIndexTest {

    private static Clustering clusters;
    private static final CommonFixture tf = new CommonFixture();
    private static Clustering iris;
    private static JaccardIndex test;
    private static final double delta = 1e-9;

    public JaccardIndexTest() throws FileNotFoundException, IOException {

        clusters = FakeClustering.iris();

        //now try some real clustering
        ClusteringAlgorithm km = new KMeans(3, 100, new EuclideanDistance());
        iris = FakeClustering.irisWrong();
    }

    @BeforeClass
    public static void setUpClass() {
        test = new JaccardIndex();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class JaccardIndex.
     */
    @Test
    public void testGetName() {
    }

    /**
     * Test of score method, of class JaccardIndex.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        double score = test.score(clusters, FakeDatasets.irisDataset());
        //this is fixed clustering which correspods to true classes in dataset
        assertEquals(1.0, score, delta);
        System.out.println("jaccard index = " + score);

        //delta here depends on random initialization of k-means
        long start = System.currentTimeMillis();
        score = test.score(iris, FakeDatasets.irisDataset());
        long end = System.currentTimeMillis();
        //it should be 0.8045 or 0.81...
        assertEquals(0.15032686686154664, score, delta);
        System.out.println("jaccard index = " + score);
        System.out.println("measuring Jaccard took " + (end - start) + " ms");

        Clustering<Cluster> irisWrong2 = FakeClustering.irisWrong2();
        score = test.score(irisWrong2, FakeDatasets.irisDataset());
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
        score = test.score(FakeClustering.wineClustering(), FakeClustering.wineCorrect());
        end = System.currentTimeMillis();

        //each cluster should have this scores:
        //Cabernet = 0.5625
        //Syrah = 0.3846
        //Pinot = 0.5714
        assertEquals(0.5061813186813187, score, delta);
        System.out.println(test.getName() + " = " + score);
        System.out.println("measuring " + test.getName() + " took " + (end - start) + " ms");

        start = System.currentTimeMillis();
        double score2 = test.score(FakeClustering.wineClustering(), FakeClustering.wine());
        end = System.currentTimeMillis();
        //when using class labels result should be the same
        assertEquals(score, score2, delta);
        System.out.println(test.getName() + " = " + score2);
        System.out.println("measuring " + test.getName() + " took " + (end - start) + " ms");
    }
}
