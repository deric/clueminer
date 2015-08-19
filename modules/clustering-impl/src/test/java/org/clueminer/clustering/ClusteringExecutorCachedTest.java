package org.clueminer.clustering;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ClusteringExecutorCachedTest {

    private final ClusteringExecutorCached subject = new ClusteringExecutorCached();

    public ClusteringExecutorCachedTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Ignore
    public void testHclustRows() {
        Props pref = new Props();
        Clustering<Instance, Cluster<Instance>> clust = subject.clusterRows(FakeClustering.irisDataset(), pref);
        assertNotNull(clust);
        //cutoff implementation is needed
    }

    @Test
    public void testHclustColumns() {
        Distance dm = new EuclideanDistance();
        Props pref = new Props();
        HierarchicalResult hres = subject.hclustRows(FakeClustering.irisDataset(), pref);
        assertNotNull(hres);
        assertEquals(150, hres.size());
    }

    @Test
    public void testClusterRows() {
    }

    //TODO: move some cutoff strategy to this package
    @Ignore
    public void testClusterAll() {
        Distance dm = new EuclideanDistance();
        Props pref = new Props();
        DendrogramMapping mapping = subject.clusterAll(FakeClustering.irisDataset(), pref);
        assertNotNull(mapping);
        HierarchicalResult rows = mapping.getRowsResult();
        Matrix mr = rows.getProximityMatrix();
        assertEquals(150, mr.rowsCount());
        assertEquals(150, mr.columnsCount());
        HierarchicalResult cols = mapping.getColsResult();
        assertEquals(150, rows.size());
        assertEquals(4, cols.size());
        Matrix mc = cols.getProximityMatrix();
        assertEquals(4, mc.rowsCount());
        assertEquals(4, mc.columnsCount());
    }

}
