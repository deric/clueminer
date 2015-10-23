package org.clueminer.evolution.multim;

import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.AIC;
import org.clueminer.eval.external.Precision;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.report.MemInfo;
import org.clueminer.utils.PropType;
import org.clueminer.utils.Props;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class MultiMuteEvolutionTest {

    protected MultiMuteEvolution subject;
    protected ConsoleReporter report;
    protected MemInfo mem;

    public MultiMuteEvolutionTest() {
    }

    @Before
    public void setUp() {
        subject = new MultiMuteEvolution(new ClusteringExecutorCached());
        report = new ConsoleReporter();
        subject.addEvolutionListener(report);
        mem = new MemInfo();
    }

    /**
     * Test iris dataset evolution
     */
    @Test
    public void testRun() {
        subject.setDataset(FakeDatasets.irisDataset());
        //subject.setAlgorithm(new ));
        subject.setEvaluator(new AIC());
        ExternalEvaluator ext = new Precision();
        subject.setExternal(ext);
        Props params = new Props();
        params.put(PropType.PERFORMANCE, AgglParams.KEEP_PROXIMITY, true);
        subject.setDefaultProps(params);

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
