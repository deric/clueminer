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
        System.out.println("Calinski-Harabasz = " + score);
        long end = System.currentTimeMillis();
        System.out.println("measuring took " + (end - start) + " ms");
        assertEquals(false, Double.isNaN(score));
    }

    /**
     * TODO: fix index computation
     */
    @Test
    public void testIris() {
        double s1 = subject.score(FakeClustering.iris(), dataset);
        double s2 = subject.score(FakeClustering.irisMostlyWrong(), dataset);
        double s3 = subject.score(FakeClustering.irisWrong5(), dataset);
        assertEquals(true, subject.isBetter(s1, s2));
        assertEquals(true, subject.isBetter(s1, s3));
    }

    /**
     * Test of isBetter method, of class CalinskiHarabasz.
     */
    @Test
    public void testCompareScore() {
        assertEquals(true, subject.isBetter(15, 2));
    }
}
