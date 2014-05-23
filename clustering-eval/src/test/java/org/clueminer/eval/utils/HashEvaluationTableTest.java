package org.clueminer.eval.utils;

import org.clueminer.clustering.api.Clustering;
import org.clueminer.eval.external.Precision;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.utils.Dump;
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
public class HashEvaluationTableTest {

    private HashEvaluationTable subject;
    private static Clustering irisCorrect;
    public HashEvaluationTableTest() {
        subject = new HashEvaluationTable(irisCorrect, FakeClustering.irisDataset());
    }

    @BeforeClass
    public static void setUpClass() {
        irisCorrect = FakeClustering.iris();
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

    @Test
    public void testSetData() {
    }

    @Test
    public void testGetScore() {
        double score = subject.getScore(new Precision());
        System.out.println("precision = " + score);
    }

    @Test
    public void testGetEvaluators() {
        String[] eval = subject.getEvaluators();
        assertEquals(true, eval.length > 0);
        Dump.array(eval, "evaluators");
    }

}
