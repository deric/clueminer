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
public class JaccardIndexTest {

    private static Clustering clusters;
    private static CommonFixture tf = new CommonFixture();
    private static Clustering iris;
    private static JaccardIndex test;
    private static double delta = 1e-9;

    public JaccardIndexTest() throws FileNotFoundException, UnsupportedAttributeType, IOException {

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
        double score = test.score(clusters, null);
        //this is fixed clustering which correspods to true classes in dataset
        assertEquals(1.0, score, delta);
        System.out.println("jaccard index = " + score);

        //delta here depends on random initialization of k-means
        long start = System.currentTimeMillis();
        score = test.score(iris, null);
        long end = System.currentTimeMillis();
        //it should be 0.8045 or 0.81...
        assertEquals(0.8141, score, 0.1);
        System.out.println("jaccard index = " + score);
        System.out.println("measuring Jaccard took " + (end - start) + " ms");
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
}