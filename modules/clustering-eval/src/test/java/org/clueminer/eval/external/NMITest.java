package org.clueminer.eval.external;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class NMITest extends ExternalTest {

    private static Clustering irisCorrect;
    private static Clustering irisWrong;

    public NMITest() throws FileNotFoundException, IOException {
        irisCorrect = FakeClustering.iris();
        irisWrong = FakeClustering.irisWrong2();
        subject = new NMI();
    }

    /**
     * Test of score method, of class NMI.
     */
    @Test
    public void testScore_Clustering_Clustering() {
        //this is fixed clustering which correspods to true classes in dataset
        measure(FakeClustering.iris(), FakeClustering.iris(), 1.0);

        double score = measure(irisWrong, irisCorrect, 0.6496820278112178);

        double score2 = measure(FakeClustering.irisWrong(), irisCorrect, 0.06793702240876041);
        assertTrue(score2 < score);
    }

    /**
     * Test of score method, of class NMI.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        measure(FakeClustering.iris(), FakeDatasets.irisDataset(), 1.0);

        double score = measure(irisWrong, FakeDatasets.irisDataset(), 0.6496820278112178);
        double score2 = measure(FakeClustering.irisWrong(), irisCorrect, 0.06793702240876041);

        assertTrue(score2 < score);
    }

    /**
     * Test of isBetter method, of class NMI.
     */
    @Test
    public void testCompareScore() {
        //one is better than zero
        assertTrue(subject.isBetter(1.0, 0.0));
        assertTrue(subject.isBetter(1.0, 0.5));
        assertTrue(subject.isBetter(1.0, 0.9999));
    }
}
