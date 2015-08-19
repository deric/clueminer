package org.clueminer.eval.external;

import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.eval.utils.PairMatch;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class AUCTest extends ExternalTest {

    public AUCTest() {
        subject = new AUC();
    }

    @Test
    public void testMostlyWrong() {
        double score = subject.score(FakeClustering.irisMostlyWrong());
        System.out.println("AUC (mw) = " + score);
        assertEquals(true, score <= 0.5);
    }

    @Test
    public void testIrisCorrect() {
        //this is fixed clustering which correspods to true classes in dataset
        measure(FakeClustering.iris(), 1.0);
    }

    @Test
    public void testOneClassPerCluster() {
        AUC auc = (AUC) subject;
        PairMatch pm = CountingPairs.getInstance().matchPairs(oneClassPerCluster());
        pm.dump();
        assertEquals(Double.NaN, auc.countScore(pm), delta);
    }
}
