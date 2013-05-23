package org.clueminer.evaluation.external;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.exception.UnsupportedAttributeType;
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
public class RandIndexTest {

    private static Clustering irisCorrect;
    private static Clustering irisWrong;
    private static RandIndex test;
    private static double delta = 1e-9;

    public RandIndexTest() throws FileNotFoundException, UnsupportedAttributeType, IOException {
        test = new RandIndex();
        irisCorrect = FakeClustering.iris();
        irisWrong = FakeClustering.irisWrong2();
    }

    @BeforeClass
    public static void setUpClass() {
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
     * Test of getName method, of class RandIndex.
     */
    @Test
    public void testGetName() {
    }

    /**
     * Test of score method, of class RandIndex.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        double score = test.score(irisCorrect, null);
        //this is the ideal case
        assertEquals(1.0, score, delta);
        System.out.println("fm index = " + score);

        long start = System.currentTimeMillis();
        score = test.score(irisWrong, null);
        long end = System.currentTimeMillis();
        assertEquals(0.5111111111111111, score, delta);
        System.out.println("AdjustedRand = " + score);
        System.out.println("measuring AdjustedRand took " + (end - start) + " ms");
    }

    /**
     * Test of score method, of class RandIndex.
     */
    @Test
    public void testScore_3args() {
    }

    /**
     * Test of compareScore method, of class RandIndex.
     */
    @Test
    public void testCompareScore() {
        //one should be the best value
        assertTrue(test.compareScore(1.0, 0.0));
    }
}