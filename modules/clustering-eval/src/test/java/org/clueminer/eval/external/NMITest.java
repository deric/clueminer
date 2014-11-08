package org.clueminer.eval.external;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class NMITest {

    private static NMI subject;
    private static Clustering irisCorrect;
    private static Clustering irisWrong;
    private static final double delta = 1e-9;

    public NMITest() throws FileNotFoundException, IOException {
        irisCorrect = FakeClustering.iris();
        irisWrong = FakeClustering.irisWrong2();
    }

    @BeforeClass
    public static void setUpClass() {
        subject = new NMI();
    }

    /**
     * Test of getName method, of class NMI.
     */
    @Test
    public void testGetName() {
    }

    /**
     * Test of score method, of class NMI.
     */
    @Test
    public void testScore_Clustering_Clustering() {
        long start, end;
        start = System.currentTimeMillis();
        double score = subject.score(irisCorrect, irisCorrect);
        end = System.currentTimeMillis();
        //this is fixed clustering which correspods to true classes in dataset
        assertEquals(1.0, score, delta);
        System.out.println("nmi index = " + score);
        System.out.println("measuring NMI took " + (end - start) + " ms");

        start = System.currentTimeMillis();
        score = subject.score(irisWrong, irisCorrect);
        end = System.currentTimeMillis();
        assertEquals(0.6496820278112178, score, delta);
        System.out.println("NMI index = " + score);
        System.out.println("measuring NMI took " + (end - start) + " ms");

        start = System.currentTimeMillis();
        double score2 = subject.score(FakeClustering.irisWrong(), irisCorrect);
        end = System.currentTimeMillis();
        assertTrue(score2 < score);
        assertEquals(0.06793702240876041, score2, delta);
        System.out.println("NMI index = " + score2);
        System.out.println("measuring NMI took " + (end - start) + " ms");
    }

    /**
     * Test of score method, of class NMI.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        long start, end;
        start = System.currentTimeMillis();
        double score = subject.score(irisCorrect, FakeDatasets.irisDataset());
        end = System.currentTimeMillis();
        //this is fixed clustering which correspods to true classes in dataset
        assertEquals(1.0, score, delta);
        System.out.println("measuring NMI took " + (end - start) + " ms");
        System.out.println("nmi class = " + score);

        start = System.currentTimeMillis();
        score = subject.score(irisWrong, FakeDatasets.irisDataset());
        end = System.currentTimeMillis();
        assertEquals(0.6496820278112178, score, delta);
        System.out.println("NMI index = " + score);
        System.out.println("measuring NMI took " + (end - start) + " ms");

        start = System.currentTimeMillis();
        double score2 = subject.score(FakeClustering.irisWrong(), FakeDatasets.irisDataset());
        end = System.currentTimeMillis();
        assertTrue(score2 < score);
        assertEquals(0.06793702240876041, score2, delta);
        System.out.println("NMI index = " + score2);
        System.out.println("measuring NMI took " + (end - start) + " ms");
    }

    /**
     * Test of score method, of class NMI.
     */
    @Test
    public void testScore_3args() {
    }

    /**
     * Test of compareScore method, of class NMI.
     */
    @Test
    public void testCompareScore() {
        //one is better than zero
        assertTrue(subject.compareScore(1.0, 0.0));
        assertTrue(subject.compareScore(1.0, 0.5));
        assertTrue(subject.compareScore(1.0, 0.9999));
    }
}
