package org.clueminer.clustering;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.utils.Props;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ClusteringExecutorCachedTest {

    private ClusteringExecutorCached subject = new ClusteringExecutorCached();

    public ClusteringExecutorCachedTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    //@Test
    public void testHclustRows() {
        DistanceMeasure dm = new EuclideanDistance();
        Props pref = new Props();
        Clustering<Cluster> clust = subject.clusterRows(FakeClustering.irisDataset(), dm, pref);
        assertNotNull(clust);
        //cutoff implementation is needed
    }

    @Test
    public void testHclustColumns() {
        DistanceMeasure dm = new EuclideanDistance();
        Props pref = new Props();
        HierarchicalResult hres = subject.hclustRows(FakeClustering.irisDataset(), dm, pref);
        assertNotNull(hres);
        assertEquals(150, hres.size());
    }

    @Test
    public void testClusterRows() {
    }

    @Test
    public void testClusterAll() {
    }

}
