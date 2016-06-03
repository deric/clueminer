package org.clueminer.evolution.singlem;

import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.CalinskiHarabasz;
import org.clueminer.eval.external.Precision;
import org.clueminer.evolution.multim.ConsoleReporter;
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
public class SingleMuteEvolutionTest {

    protected SingleMuteEvolution subject;
    protected ConsoleReporter report;
    protected MemInfo mem;

    public SingleMuteEvolutionTest() {
    }

    @Before
    public void setUp() {
        subject = new SingleMuteEvolution(new ClusteringExecutorCached());
        report = new ConsoleReporter();
        subject.addEvolutionListener(report);
        mem = new MemInfo();
    }

    @Test
    public void testRun() {
        subject.setDataset(FakeDatasets.irisDataset());
        subject.setGenerations(1);
        subject.setPopulationSize(5);
        //subject.setAlgorithm(new ));
        subject.setEvaluator(new CalinskiHarabasz());
        Props params = new Props();
        params.put(PropType.PERFORMANCE, AlgParams.KEEP_PROXIMITY, true);
        subject.setDefaultProps(params);
        ExternalEvaluator ext = new Precision();
        subject.setExternal(ext);

        mem.startClock();
        //TODO: make sure evolution works
        subject.run();
        mem.report();
    }

}
