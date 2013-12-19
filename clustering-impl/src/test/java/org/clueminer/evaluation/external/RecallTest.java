package org.clueminer.evaluation.external;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.api.Clustering;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tombart
 */
public class RecallTest {

    private static Clustering irisCorrect;
    private static Clustering irisWrong;
    private static Recall test;
    private static final double delta = 1e-9;

    public RecallTest() throws FileNotFoundException, IOException {

        irisCorrect = FakeClustering.iris();
        irisWrong = FakeClustering.irisWrong();

    }

    @BeforeClass
    public static void setUpClass() {
        test = new Recall();
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
     * Test of getName method, of class Recall.
     */
    @Test
    public void testGetName() {
    }

    /**
     * Test of score method, of class Recall.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        double score = test.score(irisCorrect, FakeClustering.irisDataset());
        //this is fixed clustering which correspods to true classes in dataset
        assertEquals(1.0, score, delta);
        System.out.println(test.getName() + " = " + score);

        //delta here depends on random initialization of k-means
        long start = System.currentTimeMillis();
        score = test.score(irisWrong, FakeClustering.irisDataset());
        long end = System.currentTimeMillis();

        assertEquals(0.53403755868544, score, delta);
        System.out.println(test.getName() + " = " + score);
        System.out.println("measuring " + test.getName() + " took " + (end - start) + " ms");
    }

    /**
     * Test of score method, of class Recall.
     */
    @Test
    public void testScore_3args() {
    }

    /**
     * Test of compareScore method, of class Recall.
     */
    @Test
    public void testCompareScore() {
    }

    /**
     * Test of score method, of class Recall.
     * @see http://alias-i.com/lingpipe/docs/api/com/aliasi/classify/PrecisionRecallEvaluation.html
     */
    @Test
    public void testScore_Clustering_Clustering() {
        long start, end;
        double score;

        start = System.currentTimeMillis();
        score = test.score(FakeClustering.wineClustering(), FakeClustering.wineCorrect());
        end = System.currentTimeMillis();

        //each cluster should have this scores:
        //Cabernet = 0.7500
        //Syrah = 0.5555
        //Pinot = 0.6666
        assertEquals(0.6574074074074074, score, delta);
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
}