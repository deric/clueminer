package org.clueminer.eval.external;

import org.clueminer.eval.utils.CountingPairs;
import com.google.common.collect.Table;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.fixtures.clustering.FakeClustering;
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
public class AdjustedRandTest {

    private static AdjustedRand test;
    private static final double delta = 1e-9;

    public AdjustedRandTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        test = new AdjustedRand();
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
     * Test of getName method, of class AdjustedRand.
     */
    @Test
    public void testGetName() {
    }

    /**
     * Test of score method, of class AdjustedRand.
     */
    @Test
    public void testScore_Clustering_Dataset() {

    }

    /**
     * Test of score method, of class AdjustedRand.
     */
    @Test
    public void testScore_3args() {
    }

    /**
     * Test of compareScore method, of class AdjustedRand.
     */
    @Test
    public void testCompareScore() {
        assertTrue(test.compareScore(1.0, 0.5));
    }

    /**
     * Test of score method, of class AdjustedRand.
     */
    @Test
    public void testScore_Clustering_Clustering() {

        long start, end;
        double score;

        start = System.currentTimeMillis();
        score = test.score(FakeClustering.wineClustering(), FakeClustering.wineCorrect());
        end = System.currentTimeMillis();

        assertEquals(0.13473684210526315, score, delta);
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

    /**
     * Test based on Details of the Adjusted Rand index and Clustering
     * algorithms Supplement to the paper “An empirical study on Principal
     * Component Analysis for clustering gene expression data” (to appear in
     * Bioinformatics)
     *
     * @see http://faculty.washington.edu/kayee/pca/supp.pdf
     * @see http://faculty.washington.edu/kayee/pca/pca.pdf
     *
     */
    @Test
    public void testCountScore() {
        Table<String, String, Integer> table = CountingPairs.newTable();
        //fake contingency table
  /*     table.put("u1", "v1", 1);
         table.put("u1", "v2", 1);
         table.put("u1", "v3", 0);
         table.put("u2", "v1", 1);
         table.put("u2", "v2", 2);
         table.put("u2", "v3", 1);
         table.put("u3", "v1", 0);
         table.put("u3", "v2", 0);
         table.put("u3", "v3", 4);
         */
        //rows and columns can't be interchanged
        table.put("v1", "u1", 1);
        table.put("v1", "u2", 1);
        table.put("v1", "u3", 0);
        table.put("v2", "u1", 1);
        table.put("v2", "u2", 2);
        table.put("v2", "u3", 0);
        table.put("v3", "u1", 0);
        table.put("v3", "u2", 1);
        table.put("v3", "u3", 4);

        double score = test.countScore(table);
        assertEquals(0.31257344300822565, score, delta);
    }

    @Test
    public void testScoreDataset() {
        Clustering<Cluster> clustering = FakeClustering.irisWrong4();
        double score = test.score(clustering, FakeClustering.irisDataset());
        System.out.println("clust(4) = " + score);
    }
}
