package org.clueminer.eval.hclust;

import org.clueminer.clustering.aggl.HACLW;
import org.clueminer.clustering.aggl.linkage.SingleLinkage;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class CophCutoffTest {

    private static final CophCutoff subject = new CophCutoff();

    public CophCutoffTest() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testFindCutoff() {
        Dataset<? extends Instance> dataset = FakeDatasets.schoolData();
        HACLW alg = new HACLW();
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        pref.putBoolean(AgglParams.CLUSTER_ROWS, true);
        HierarchicalResult result = alg.hierarchy(dataset, pref);

        result.getTreeData().print();

        double cut = subject.findCutoff(result, pref);
        assertEquals(true, cut > 0);
        System.out.println("cutoff = " + cut);
        System.out.println("clustering size " + result.getClustering().size());
        assertEquals(2, result.getClustering().size());
    }

}
