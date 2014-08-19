package org.clueminer.eval.external;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class RandIndexTest {

    private static Clustering irisCorrect;
    private static Clustering irisWrong;
    private static RandIndex test;
    private static double delta = 1e-9;

    public RandIndexTest() throws FileNotFoundException, IOException {
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
        long start, end;
        start = System.currentTimeMillis();
        double score = test.score(irisCorrect, FakeDatasets.irisDataset());
        end = System.currentTimeMillis();
        System.out.println("AdjustedRand = " + score);
        System.out.println("measuring AdjustedRand took " + (end - start) + " ms");
        //this is the ideal case
        assertEquals(1.0, score, delta);

        start = System.currentTimeMillis();
        score = test.score(irisWrong, FakeDatasets.irisDataset());
        end = System.currentTimeMillis();
        assertEquals(0.6888888888888888, score, delta);
        System.out.println("AdjustedRand = " + score);
        System.out.println("measuring AdjustedRand took " + (end - start) + " ms");

        //this clustering shouldn't be better than the previous one, 142 items are in one
        //cluster, so not really the best solution - though the coefficient would prefere this one
        start = System.currentTimeMillis();
        score = test.score(FakeClustering.irisWrong(), FakeDatasets.irisDataset());
        end = System.currentTimeMillis();
        assertEquals(0.5777777777777778, score, delta);
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
