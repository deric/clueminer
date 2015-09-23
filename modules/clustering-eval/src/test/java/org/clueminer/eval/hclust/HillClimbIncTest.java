package org.clueminer.eval.hclust;

import org.clueminer.clustering.aggl.HACLW;
import org.clueminer.clustering.aggl.linkage.SingleLinkage;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.AIC;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.Props;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class HillClimbIncTest {

    private static final HillClimbInc subject = new HillClimbInc();
    private final Dataset<? extends Instance> dataset;
    private final HACLW alg;

    public HillClimbIncTest() {
        subject.setEvaluator(new AIC());
        dataset = FakeDatasets.schoolData();
        alg = new HACLW();
    }

    @Test
    public void testFindCutoff() {
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        pref.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        HierarchicalResult result = alg.hierarchy(dataset, pref);

        result.getTreeData().print();

        double cut = subject.findCutoff(result, pref);
        System.out.println("cutoff = " + cut);
        assertEquals(true, cut > 0);
        int numClusters = result.getClustering().size();
        System.out.println("clustering size: " + numClusters);
        assertEquals(true, numClusters == 6);
    }

}
