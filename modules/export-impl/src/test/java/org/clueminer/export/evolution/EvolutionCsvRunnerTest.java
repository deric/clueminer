package org.clueminer.export.evolution;

import java.util.List;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class EvolutionCsvRunnerTest {

    private EvolutionCsvRunner subject;

    @Before
    public void setUp() {
        subject = new EvolutionCsvRunner();
    }


    @Test
    public void testRun() {
        List<ClusterEvaluation> eval = subject.getEvaluators();
        assertNotSame(0, eval.size());
    }


}
