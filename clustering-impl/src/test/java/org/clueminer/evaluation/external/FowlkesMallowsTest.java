package org.clueminer.evaluation.external;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.clustering.api.Clustering;
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
    private static final double delta = 1e-9;

    public FowlkesMallowsTest() throws FileNotFoundException, IOException {

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
        double score = test.score(irisCorrect, FakeClustering.irisDataset());
        //this is fixed clustering which correspods to true classes in dataset
        assertEquals(1.0, score, delta);
        System.out.println("fm index = " + score);

        //delta here depends on random initialization of k-means
        long start = System.currentTimeMillis();
        score = test.score(irisWrong, FakeClustering.irisDataset());
        long end = System.currentTimeMillis();
   //     assertEquals(1565, score, 1.0);
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

    /**
     * Test of score method, of class FowlkesMallows.
     */
    @Test
    public void testScore_Clustering_Clustering() {
        long start, end;
        double score;

        start = System.currentTimeMillis();
        score = test.score(FakeClustering.wineClustering(), FakeClustering.wineCorrect());
        end = System.currentTimeMillis();

        //each cluster should have this scores:

        assertEquals(0.6688096636728896, score, delta);
        System.out.println(test.getName() + " = " + score);
        System.out.println("measuring " + test.getName() + " took " + (end - start) + " ms");

        start = System.currentTimeMillis();
        double score2 = test.score(FakeClustering.wineClustering(), FakeClustering.wine());
        end = System.currentTimeMillis();
        System.out.println(test.getName() + " = " + score2);
        //when using class labels result should be the same
        assertEquals(score, score2, delta);

        System.out.println("measuring " + test.getName() + " took " + (end - start) + " ms");
    }
}