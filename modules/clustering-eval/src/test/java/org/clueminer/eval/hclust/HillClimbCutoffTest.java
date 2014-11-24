package org.clueminer.eval.hclust;

import org.clueminer.clustering.aggl.HAC;
import org.clueminer.clustering.aggl.HACLW;
import org.clueminer.clustering.aggl.linkage.SingleLinkage;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.AICScore;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.Props;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class HillClimbCutoffTest {

    private static final HillClimbCutoff subject = new HillClimbCutoff();
    private Dataset<? extends Instance> dataset;

    public HillClimbCutoffTest() {
        subject.setEvaluator(new AICScore());
    }

    @Before
    public void setUp() {
        dataset = FakeDatasets.schoolData();
    }

    @Test
    public void testFindCutoff() {
        HACLW alg = new HACLW();
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        pref.putBoolean(AgglParams.CLUSTER_ROWS, true);
        HierarchicalResult result = alg.hierarchy(dataset, pref);

        result.getTreeData().print();

        double cut = subject.findCutoff(result);
        assertEquals(true, cut > 0);
        System.out.println("cutoff = " + cut);
        int numClusters = result.getClustering().size();
        System.out.println("clustering size: " + numClusters);
        assertEquals(true, numClusters < 4);
    }


}
