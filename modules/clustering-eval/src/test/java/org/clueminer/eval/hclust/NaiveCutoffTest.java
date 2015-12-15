package org.clueminer.eval.hclust;

import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.aggl.HC;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.clustering.aggl.linkage.SingleLinkage;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.utils.Props;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class NaiveCutoffTest {

    private final NaiveCutoff subject = new NaiveCutoff();

    public NaiveCutoffTest() {
    }

    @BeforeClass
    public static void setUpClass() {
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
    public void testFindCutoff() {
        Dataset<? extends Instance> dataset = FakeDatasets.schoolData();
        HC alg = new HC();
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        pref.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        HierarchicalResult result = alg.hierarchy(dataset, pref);

        result.getTreeData().print();

        double cut = subject.findCutoff(result, pref);
        assertEquals(true, cut > 0);
        System.out.println("cutoff = " + cut);
        System.out.println("clustering size: " + result.getClustering().size());
    }

    @Test
    public void testFindCutoffOld() {
    }

}
