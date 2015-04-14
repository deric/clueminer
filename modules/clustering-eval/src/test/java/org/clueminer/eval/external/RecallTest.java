package org.clueminer.eval.external;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author tombart
 */
public class RecallTest extends ExternalTest {

    private static Clustering irisCorrect;
    private static Clustering irisWrong;

    public RecallTest() throws FileNotFoundException, IOException {
        irisCorrect = FakeClustering.iris();
        irisWrong = FakeClustering.irisWrong();
        subject = new Recall();
    }

    /**
     * Test of score method, of class Recall.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        measure(irisCorrect, 1.0);

        measure(irisWrong, 0.33053975855532275);
    }

    /**
     * Test of isBetter method, of class Recall.
     */
    @Test
    public void testCompareScore() {
    }

    /**
     * Test of score method, of class Recall.
     *
     * @see
     * http://alias-i.com/lingpipe/docs/api/com/aliasi/classify/PrecisionRecallEvaluation.html
     */
    @Test
    public void testScore_Clustering_Clustering() {
        double score;

        //each cluster should have this scores:
        //Cabernet = 0.7500
        //Syrah = 0.5555
        //Pinot = 0.6666
        score = measure(FakeClustering.wineClustering(), FakeClustering.wineCorrect(), 0.49572649572649574);

        //when using class labels result should be the same
        measure(FakeClustering.wineClustering(), score);
    }

    @Test
    public void testOneClassPerCluster() {
        assertEquals(Double.NaN, subject.score(oneClassPerCluster()), delta);
    }

    @Test
    public void testMostlyWrong() {
        double score = subject.score(FakeClustering.irisMostlyWrong());
        System.out.println("recall (mw) = " + score);
        assertEquals(true, score < 0.33);
    }
}
