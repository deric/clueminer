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
public class FowlkesMallowsTest {

    private static FowlkesMallows test;
    private static Clustering irisCorrect;    
    private static Clustering irisWrong;    
    private static double delta = 1e-9;

    public FowlkesMallowsTest() throws FileNotFoundException, UnsupportedAttributeType, IOException {

        irisCorrect = FakeClustering.iris();
        irisWrong = FakeClustering.irisWrong2();
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
        double score = test.score(irisCorrect, null);
        //this is fixed clustering which correspods to true classes in dataset
        assertEquals(2500.0, score, delta);
        System.out.println("fm index = " + score);

        //delta here depends on random initialization of k-means
        long start = System.currentTimeMillis();
        score = test.score(irisWrong, null);
        long end = System.currentTimeMillis();
        assertEquals(1565, score, 1.0);
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