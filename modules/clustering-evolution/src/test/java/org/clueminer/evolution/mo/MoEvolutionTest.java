package org.clueminer.evolution.mo;

import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.AICScore;
import org.clueminer.eval.CalinskiHarabasz;
import org.clueminer.eval.external.Precision;
import org.clueminer.evolution.multim.ConsoleReporter;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.report.MemInfo;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class MoEvolutionTest {

    protected MoEvolution subject;
    protected ConsoleReporter report;
    protected MemInfo mem;


    public MoEvolutionTest() {
    }

    @Before
    public void setUp() {
        subject = new MoEvolution(new ClusteringExecutorCached());
        //report = new ConsoleReporter();
        //subject.addEvolutionListener(report);
        mem = new MemInfo();
    }

    @Test
    public void testRun() {
        subject.setDataset(FakeDatasets.irisDataset());
        subject.setGenerations(1);
        subject.setPopulationSize(5);
        //subject.setAlgorithm(new ));
        subject.addObjective(new CalinskiHarabasz());
        subject.addObjective(new AICScore());
        ExternalEvaluator ext = new Precision();
        subject.setExternal(ext);

        mem.startClock();
        //TODO: make sure evolution works
        subject.run();
        mem.report();
    }

}
