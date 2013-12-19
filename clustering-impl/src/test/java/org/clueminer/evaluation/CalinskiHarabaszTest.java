package org.clueminer.evaluation;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
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
public class CalinskiHarabaszTest {

    private static Clustering clusters;
    private static Dataset<Instance> dataset;
    private static CalinskiHarabasz test;

    public CalinskiHarabaszTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        clusters = FakeClustering.iris();
        dataset = FakeClustering.irisDataset();
        test = new CalinskiHarabasz();
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
     * Test of getName method, of class CalinskiHarabasz.
     */
    @Test
    public void testGetName() {
        assertEquals("Calinski-Harabasz", test.getName());
    }

    /**
     * Test of score method, of class CalinskiHarabasz.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        long start = System.currentTimeMillis();
        double score = test.score(clusters, dataset);
        long end = System.currentTimeMillis();
        System.out.println("Calinski-Harabasz = " + score);
        System.out.println("measuring took " + (end - start) + " ms");
    }

    /**
     * Test of getSumOfSquaredError method, of class CalinskiHarabasz.
     */
    @Test
    public void testGetSumOfSquaredError() {
    }

    /**
     * Test of score method, of class CalinskiHarabasz.
     */
    @Test
    public void testScore_3args() {
    }

    /**
     * Test of compareScore method, of class CalinskiHarabasz.
     */
    @Test
    public void testCompareScore() {
    }
}