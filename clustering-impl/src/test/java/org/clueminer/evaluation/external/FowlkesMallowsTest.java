package org.clueminer.evaluation.external;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.exception.UnsupportedAttributeType;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
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
public class FowlkesMallowsTest {

    private static Clustering clusters;
    private static CommonFixture tf = new CommonFixture();
    private static Clustering iris;
    private static FowlkesMallows test;
    private static double delta = 1e-9;

    public FowlkesMallowsTest() throws FileNotFoundException, UnsupportedAttributeType, IOException {

        clusters = FakeClustering.iris();

        //now try some real clustering
        ClusteringAlgorithm km = new KMeans(3, 100, new EuclideanDistance());
        ARFFHandler arff = new ARFFHandler();
        Dataset<Instance> irisDataset = new SampleDataset();
        arff.load(tf.irisArff(), irisDataset, 4);
        iris = km.partition(irisDataset);
    }

    @BeforeClass
    public static void setUpClass() {
        test = new FowlkesMallows();
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
     * Test of getName method, of class FowlkesMallows.
     */
    @Test
    public void testGetName() {
    }

    /**
     * Test of score method, of class FowlkesMallows.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        double score = test.score(clusters, null);
        //this is fixed clustering which correspods to true classes in dataset
        assertEquals(2500.0, score, delta);
        System.out.println("fm index = " + score);

        //delta here depends on random initialization of k-means
        long start = System.currentTimeMillis();
        score = test.score(iris, null);
        long end = System.currentTimeMillis();
        assertEquals(2228.45, score, 25.0);
        System.out.println("fm index = " + score);
        System.out.println("measuring Fowlkes-Mallows took " + (end - start) + " ms");
    }

    /**
     * Test of score method, of class FowlkesMallows.
     */
    @Test
    public void testScore_3args() {
    }

    /**
     * Test of compareScore method, of class FowlkesMallows.
     */
    @Test
    public void testCompareScore() {
        //bigger is better
        assertTrue(test.compareScore(2600, 2000));
    }
}