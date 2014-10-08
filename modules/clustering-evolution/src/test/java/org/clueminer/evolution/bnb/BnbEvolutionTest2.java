package org.clueminer.evolution.bnb;

import org.clueminer.clustering.ClusteringExecutor2;
import org.clueminer.report.MemInfo;
import org.junit.Before;

/**
 *
 * @author Tomas Barton
 */
public class BnbEvolutionTest2 extends BnbEvolutionTest {

    @Before
    public void setUp() {
        subject = new BnbEvolution(new ClusteringExecutor2());
        report = new ConsoleReporter();
        subject.addEvolutionListener(report);
        mem = new MemInfo();
    }

}
