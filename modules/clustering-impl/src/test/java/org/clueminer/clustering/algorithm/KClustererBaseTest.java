package org.clueminer.clustering.algorithm;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.HardAssignment;
import org.clueminer.clustering.api.Assignment;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class KClustererBaseTest {

    public KClustererBaseTest() {
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
    public void testFromAssignment() {
        Dataset<? extends Instance> dataset = FakeClustering.irisDataset();

        int[] memebership = new int[dataset.size()];
        for (int i = 0; i < 50; i++) {
            memebership[i] = 1;
            memebership[i + 50] = 2;
            memebership[i + 100] = 3;
        }
        Assignment assign = new HardAssignment(memebership);
        assertEquals(3, assign.distinct());

        Clustering<Instance, Cluster<Instance>> clustering = KClustererBase.fromAssignment(assign, dataset);
        //we should have all instances
        assertEquals(dataset.size(), clustering.instancesCount());
        assertEquals(3, clustering.size());
    }

}
