package org.clueminer.eval;

import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class CalinskiHarabaszTest {

    private static Clustering clusters;
    private static Dataset<? extends Instance> dataset;
    private static CalinskiHarabasz subject;

    public CalinskiHarabaszTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        clusters = FakeClustering.iris();
        dataset = FakeDatasets.irisDataset();
        subject = new CalinskiHarabasz(new EuclideanDistance());
    }

    /**
     * Test of getName method, of class CalinskiHarabasz.
     */
    @Test
    public void testGetName() {
        assertEquals("Calinski-Harabasz", subject.getName());
    }

    /**
     * Test of score method, of class CalinskiHarabasz.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        long start = System.currentTimeMillis();
        double score = subject.score(clusters, dataset);
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
     * Test of isBetter method, of class CalinskiHarabasz.
     */
    @Test
    public void testCompareScore() {
        assertEquals(true, subject.isBetter(15, 2));
    }
}
