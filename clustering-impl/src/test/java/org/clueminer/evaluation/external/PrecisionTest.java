package org.clueminer.evaluation.external;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.attributes.AttributeType;
import org.clueminer.cluster.BaseCluster;
import org.clueminer.cluster.ClusterList;
import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.exception.UnsupportedAttributeType;
import org.clueminer.fixtures.CommonFixture;
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
public class PrecisionTest {

    private static Clustering clusters;
    private static CommonFixture tf = new CommonFixture();
    private static Clustering iris;
    private static Precision test;
    private static double delta = 1e-9;

    public PrecisionTest() throws FileNotFoundException, UnsupportedAttributeType, IOException {

        clusters = FakeClustering.iris();
        iris = FakeClustering.irisWrong();
    }

    @BeforeClass
    public static void setUpClass() {
        test = new Precision();
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
     * Test of getName method, of class Precision.
     */
    @Test
    public void testGetName() {
    }

    /**
     * Test of score method, of class Precision.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        double score = test.score(clusters, FakeClustering.irisDataset());
        //this is fixed clustering which correspods to true classes in dataset
        assertEquals(1.0, score, delta);
        System.out.println(test.getName() + " = " + score);

        long start = System.currentTimeMillis();
        score = test.score(iris, FakeClustering.irisDataset());
        long end = System.currentTimeMillis();

        assertEquals(0.36666666666, score, delta);
        System.out.println(test.getName() + " = " + score);
        System.out.println("measuring " + test.getName() + " took " + (end - start) + " ms");
    }

    /**
     * Test of score method, of class Precision.
     */
    @Test
    public void testScore_3args() {
    }

    /**
     * Test of compareScore method, of class Precision.
     */
    @Test
    public void testCompareScore() {
    }

    /**
     * Test of score method, of class Precision.
     */
    @Test
    public void testScore_Clustering_Clustering() {
        long start, end;
        start = System.currentTimeMillis();
        double score = test.score(FakeClustering.iris(), FakeClustering.iris());
        end = System.currentTimeMillis();
        //this is fixed clustering which correspods to true classes in dataset
        assertEquals(1.0, score, delta);
        System.out.println("precision  = " + score);
        System.out.println("measuring precision took " + (end - start) + " ms");

        start = System.currentTimeMillis();
        score = test.score(FakeClustering.irisWrong(), FakeClustering.iris());
        end = System.currentTimeMillis();

        assertEquals(0.3666666666666667, score, delta);
        System.out.println(test.getName() + " = " + score);
        System.out.println("measuring " + test.getName() + " took " + (end - start) + " ms");

        start = System.currentTimeMillis();
        score = test.score(FakeClustering.irisWrong2(), FakeClustering.iris());
        end = System.currentTimeMillis();

        assertEquals(0.8, score, delta);
        System.out.println(test.getName() + " = " + score);
        System.out.println("measuring " + test.getName() + " took " + (end - start) + " ms");



        start = System.currentTimeMillis();
        score = test.score(FakeClustering.wineClustering(), FakeClustering.wineCorrect());
        end = System.currentTimeMillis();

        //each cluster should have this scores:
        //Cabernet = 0.6923	
        //Syrah = 0.5555
        //Pinot = 0.8000
        assertEquals(0.6826210826210826, score, delta);
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