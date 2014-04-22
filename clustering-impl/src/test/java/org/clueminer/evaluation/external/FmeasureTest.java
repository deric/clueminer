package org.clueminer.evaluation.external;

import org.clueminer.cluster.FakeClustering;
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
public class FmeasureTest {

    private static Fmeasure test;
    private static final double delta = 1e-9;

    public FmeasureTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        test = new Fmeasure();
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
     * Test of getName method, of class Fmeasure.
     */
    @Test
    public void testGetName() {
    }

    /**
     * Test of countScore method, of class Fmeasure.
     */
    @Test
    public void testCountScore() {
    }

    /**
     * Test of score method, of class Fmeasure.
     */
    @Test
    public void testScore_Clustering_Clustering() {
    }

    /**
     * Test of score method, of class Fmeasure.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        long start, end;
        double score;

        start = System.currentTimeMillis();
        score = test.score(FakeClustering.wineClustering(), FakeClustering.wineCorrect());
        end = System.currentTimeMillis();

        //each cluster should have this scores:
        //Cabernet = 0.7200
        //Syrah = 0.5555
        //Pinot = 0.7272
        assertEquals(0.6676094276094275, score, delta);
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
     * Test of score method, of class Fmeasure.
     */
    @Test
    public void testScore_3args() {
    }

    /**
     * Test of compareScore method, of class Fmeasure.
     */
    @Test
    public void testCompareScore() {
    }

    /**
     * Test of getBeta method, of class Fmeasure.
     */
    @Test
    public void testGetBeta() {
    }

    /**
     * Test of setBeta method, of class Fmeasure.
     */
    @Test
    public void testSetBeta() {
    }
}
