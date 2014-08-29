package org.clueminer.eval.utils;

import org.clueminer.clustering.api.Clustering;
import org.clueminer.eval.external.Precision;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
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
    private static final double delta = 1e-9;

    public HashEvaluationTableTest() {
        subject = new HashEvaluationTable(irisCorrect, FakeDatasets.irisDataset());
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
        assertEquals(1.0, score, delta);
    }

    @Test
    public void testGetEvaluators() {
        String[] eval = subject.getEvaluators();
        assertEquals(true, eval.length > 0);
        Dump.array(eval, "evaluators");
    }

    @Test
    public void testGetInternal() {
        System.out.println("internal: " + subject.getInternal().toString());
    }

    @Test
    public void testGetExternal() {
        System.out.println("external: " + subject.getExternal().toString());
    }

}
