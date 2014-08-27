package org.clueminer.eval.hclust;

import org.clueminer.clustering.aggl.AgglParams;
import org.clueminer.clustering.aggl.HAC;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.hclust.linkage.SingleLinkage;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.JMatrix;
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
        Matrix input = new JMatrix(dataset.arrayCopy());
        HAC alg = new HAC();
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        pref.putBoolean(AgglParams.CLUSTER_ROWS, true);
        HierarchicalResult result = alg.hierarchy(input, dataset, pref);

        result.getTreeData().print();

        double cut = subject.findCutoff(result);
        assertEquals(true, cut > 0);
        System.out.println("cutoff = " + cut);
    }

    @Test
    public void testFindCutoffOld() {
    }

}
