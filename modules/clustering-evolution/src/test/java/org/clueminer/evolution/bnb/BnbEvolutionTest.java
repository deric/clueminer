package org.clueminer.evolution.bnb;

import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.external.NMI;
import org.clueminer.eval.external.Precision;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.report.MemInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class BnbEvolutionTest {

    protected BnbEvolution subject;
    protected ConsoleReporter report;
    protected MemInfo mem;

    public BnbEvolutionTest() {
    }

    @Before
    public void setUp() {
        subject = new BnbEvolution(new ClusteringExecutorCached());
        report = new ConsoleReporter();
        subject.addEvolutionListener(report);
        mem = new MemInfo();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetName() {
    }

    /**
     * Test iris dataset evolution
     */
    @Test
    public void testRun() {
        subject.setDataset(FakeDatasets.irisDataset());
        //subject.setAlgorithm(new ));
        subject.setEvaluator(new NMI());
        ExternalEvaluator ext = new Precision();
        subject.setExternal(ext);

        mem.startClock();
        subject.run();
        mem.report();
    }

    @Test
    public void testMakeClusters() {
    }

    @Test
    public void testStandartize() {
    }

    @Test
    public void testFinish() {
    }

    @Test
    public void testIndividualCreated() {
    }
}
