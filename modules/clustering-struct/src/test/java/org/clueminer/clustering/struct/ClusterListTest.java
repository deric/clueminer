package org.clueminer.clustering.struct;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
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
public class ClusterListTest {

    private static ClusterList subject;
    private static final CommonFixture tf = new CommonFixture();

    public ClusterListTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        subject = new ClusterList(5);
        subject.createCluster();
        subject.createCluster();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetName() {
    }

    @Test
    public void testEnsureCapacity() {
    }

    @Test
    public void testHasAt() {
        assertEquals(true, subject.hasAt(0));
        assertEquals(true, subject.hasAt(1));
    }

    @Test
    public void testAdd() {
        ClusterList clusters = new ClusterList(3);
        clusters.createCluster();
        clusters.createCluster();
        clusters.createCluster();
        assertEquals(3, clusters.size());
        assertEquals(0, clusters.get(0).getClusterId());
        assertEquals(2, clusters.get(2).getClusterId());
    }

    @Test
    public void testGetCapacity() {
        assertEquals(5, subject.getCapacity());
    }

    @Test
    public void testGetClusterLabel() {
        assertEquals("cluster 1", subject.getClusterLabel(0));
    }

    @Test
    public void testFirst() {
    }

    @Test
    public void testPut_int_Cluster() {
        Clustering<Cluster> clusters = new ClusterList(5);
        Cluster clus = new BaseCluster(5);
        clusters.put(3, clus);
        assertEquals(clus, clusters.get(3));
    }

    @Test
    public void testPut_int_0_Cluster() {
        ClusterList list = new ClusterList(10);
        assertEquals(0, list.size());
        assertEquals(10, list.getCapacity());

        list.put(0, new BaseCluster(1));
        assertEquals(1, list.size());
    }

    @Test
    public void testMerge() {
    }

    @Test
    public void testSize() {
        assertEquals(2, subject.size());
    }

    @Test
    public void testInstancesCount() {
    }

    //@Test
    public void testGetCentroid() {
        Clustering<Cluster> clusters = createClusters();
        assertEquals(3, clusters.get(0).size());
        Instance inst = clusters.getCentroid();
        System.out.println("centroid: " + inst.toString());
        assertEquals(3, clusters.get(0).size());
    }

    private Clustering<Cluster> createClusters() {
        Clustering<Cluster> clusters = new ClusterList(5);
        instanceIter(clusters);
        Cluster clust = clusters.createCluster();
        clust.add(new DoubleArrayDataRow(new double[]{1.0, 1.0}));
        clust.add(new DoubleArrayDataRow(new double[]{1.0, 0.0}));
        clust.add(new DoubleArrayDataRow(new double[]{1.0, 2.0}));
        return clusters;
    }

    private Clustering<Cluster> createClustersDifferentOrder() {
        Clustering<Cluster> clusters = new ClusterList(5);
        instanceIter(clusters);
        Cluster clust = clusters.createCluster();
        clust.add(new DoubleArrayDataRow(new double[]{1.0, 0.0}));
        clust.add(new DoubleArrayDataRow(new double[]{1.0, 1.0}));
        clust.add(new DoubleArrayDataRow(new double[]{1.0, 2.0}));
        return clusters;
    }

    private Clustering<Cluster> createClusters2() {
        Clustering<Cluster> clusters = new ClusterList(5);
        instanceIter(clusters);
        Cluster clust = clusters.createCluster();
        clust.add(new DoubleArrayDataRow(new double[]{1.0, 0.0}));
        clust.add(new DoubleArrayDataRow(new double[]{5.0, 1.0}));
        clust.add(new DoubleArrayDataRow(new double[]{1.0, 2.0}));
        return clusters;
    }

    @Test
    public void testInstancesIterator() {
        Clustering<Cluster> clusters = createClusters();
        Cluster clust = clusters.get(0);
        assertEquals(3, clust.size());
        assertEquals(3, clusters.instancesCount());
    }

    private void instanceIter(Clustering<Cluster> clusters) {
        Iterator<Instance> iter = clusters.instancesIterator();
        Instance elem;
        int i = 0;
        while (iter.hasNext()) {
            elem = iter.next();
            assertNotNull(elem);
            i++;
        }
        assertEquals(clusters.instancesCount(), i);
    }

    @Test
    public void testClusterSizes() {
        int[] sizes = subject.clusterSizes();
        for (int i = 0; i < sizes.length; i++) {
            assertEquals(0, sizes[i]);
        }
    }

    @Test
    public void testAssignedCluster() {
    }

    @Test
    public void testGet() {
    }

    /**
     * Test of iterator method, of class ClusterList.
     */
    @Test
    public void testIterator() {
        Clustering<Cluster> clusters = new ClusterList(10);
        //create 6 empty clusters
        for (int i = 0; i < 6; i++) {
            clusters.createCluster(i + 1);
        }
        assertEquals(6, clusters.size());

        int i = 0;
        for (Cluster c : clusters) {
            i++;
        }
        assertEquals(6, i);

        Iterator<Cluster> iter = clusters.iterator();
        i = 0;
        while (iter.hasNext()) {
            iter.next();
            i++;
        }
        assertEquals(6, i);
    }

    @Test
    public void testShortForEach() {
        Clustering<Cluster> clust = createClusters();
        for (Cluster elem : clust) {
            assertNotNull(elem);
        }
    }

    @Test
    public void testIsEmpty() {
        assertEquals(false, subject.isEmpty());

        //empty clusters
        Clustering<Cluster> clusters = new ClusterList(3);
        assertEquals(true, clusters.isEmpty());
        clusters.add(new BaseCluster(1));
        assertEquals(false, clusters.isEmpty());
    }

    @Test
    public void testContains() {
        Clustering<Cluster> c1 = createClusters();
        Clustering<Cluster> c2 = createClustersDifferentOrder();

        for (int i = 0; i < c2.size(); i++) {
            assertEquals(true, c1.contains(c2.get(i)));
        }
    }

    @Test
    public void testToArray_0args() {
    }

    @Test
    public void testToArray_GenericType() {
    }

    @Test
    public void testRemove() {
    }

    @Test
    public void testContainsAll() {
        Clustering<Cluster> c1 = createClusters();
        Clustering<Cluster> c2 = createClustersDifferentOrder();
        assertEquals(true, c1.containsAll(c2));

        Clustering<Cluster> c3 = createClusters2();
        assertEquals(false, c1.containsAll(c3));
    }

    @Test
    public void testAddAll() {
    }

    @Test
    public void testRemoveAll() {
    }

    @Test
    public void testRetainAll() {
    }

    @Test
    public void testClear() {
        Clustering<Cluster> clust = createClusters();
        assertEquals(1, clust.size());
        clust.clear();
        assertEquals(0, clust.size());
    }

    @Test
    public void testCreateCluster_int() {
        ClusterList list = new ClusterList(3);
        //create cluster with given ID
        Cluster c = list.createCluster(0);
        assertEquals(0, c.getClusterId());
        assertEquals("cluster 1", c.getName());
        assertEquals(1, list.size());

        //start from non-zero index
        list = new ClusterList(5);
        //create cluster with given ID
        c = list.createCluster(2);
        assertEquals(2, c.getClusterId());
        assertEquals("cluster 3", c.getName());
        assertEquals(1, list.size());

    }

    @Test
    public void testCreateCluster_0args() {
        ClusterList list = new ClusterList(3);
        //create cluster with given ID
        Cluster c = list.createCluster();
        assertEquals(0, c.getClusterId());
        assertEquals(1, list.size());
    }

    private Dataset<? extends Instance> loadIris() throws FileNotFoundException, IOException {
        Dataset<? extends Instance> iris = new ArrayDataset(150, 4);
        ARFFHandler arff = new ARFFHandler();
        arff.load(tf.irisArff(), iris, 4);
        return iris;
    }

    @Test
    public void testCreateCluster_int_int() throws FileNotFoundException, IOException {
        ClusterList list = new ClusterList(3);

        Dataset<? extends Instance> iris = loadIris();

        //Dataset<? extends Instance> iris = ;
        //create cluster with given ID
        Cluster c = list.createCluster(0, 5);

        //ID for programmers should start from 0
        assertEquals(0, c.getClusterId());
        assertEquals(1, list.size());
        assertEquals(5, c.getCapacity());
    }

    @Test
    public void testToString() {
    }

    @Test
    public void testGetLookup() {
    }

    @Test
    public void testLookupAdd() {
    }

    @Test
    public void testLookupRemove() {
    }

    @Test
    public void testGetParams() {
        assertNotNull(subject.getParams());
    }

    @Test
    public void testSetParams() {
    }

    @Test
    public void testPut_Cluster() {
    }

    @Test
    public void testHashCode() {
        Clustering<Cluster> c1 = createClusters();
        Clustering<Cluster> c2 = createClustersDifferentOrder();
        assertEquals(c1.hashCode(), c2.hashCode());

        Clustering<Cluster> c3 = createClusters2();
        assertNotSame(c2.hashCode(), c3.hashCode());
    }

    @Test
    public void testEquals() {
        Clustering<Cluster> c1 = createClusters();
        Clustering<Cluster> c2 = createClustersDifferentOrder();
        assertEquals(true, c1.equals(c2));
        assertEquals(true, c2.equals(c1));

        Clustering<Cluster> c3 = createClusters2();
        assertEquals(false, c3.equals(c1));
    }

    @Test
    public void testMergeParams() {
        Props p = subject.getParams();
        p.put("foo", "foo");
        Props other = new Props();
        other.put("bar", "bar");
        subject.mergeParams(other);

        assertEquals(2, subject.getParams().size());
        assertEquals("bar", subject.getParams().get("bar"));
        assertEquals("foo", subject.getParams().get("foo"));
    }
}
