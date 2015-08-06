package org.clueminer.eval;

import org.clueminer.clustering.api.Clustering;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class CalinskiHarabaszTest {

    private static Clustering clusters;
    private static CalinskiHarabasz subject;
    private static final double delta = 1e-9;

    public CalinskiHarabaszTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        clusters = FakeClustering.iris();
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
        double score = subject.score(clusters);
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
        double s1 = subject.score(FakeClustering.iris());
        double s2 = subject.score(FakeClustering.irisMostlyWrong());
        double s3 = subject.score(FakeClustering.irisWrong5());
        assertEquals(false, subject.isBetter(s1, s2));
        assertEquals(false, subject.isBetter(s1, s3));
    }

    /**
     * Test of isBetter method, of class CalinskiHarabasz.
     */
    @Test
    public void testCompareScore() {
        assertEquals(true, subject.isBetter(2, 20));
    }

    /**
     * Check against definition (and tests in R package clusterCrit)
     * https://cran.r-project.org/web/packages/clusterCrit/index.html
     *
     * NOTE: There's a small problem with precision of floating point
     * operations. First 7 decimal digits seems to match.
     */
    @Test
    public void testClusterCrit() {
        double score = subject.score(FakeClustering.int100p4());
        //clusterCrit = 3959.80613603063
        assertEquals(3959.80613603063, score, delta);
    }
}
