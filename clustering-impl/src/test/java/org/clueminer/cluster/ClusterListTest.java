package org.clueminer.cluster;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.exception.UnsupportedAttributeType;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class ClusterListTest {

    private Clustering clusters;
    private static Dataset<Instance> data;

    public ClusterListTest() {
    }

    @BeforeClass
    public static void setUpClass() throws UnsupportedAttributeType, IOException {
        CommonFixture tf = new CommonFixture();
        data = new SampleDataset();
        ARFFHandler arff = new ARFFHandler();
        try {
            arff.load(tf.irisArff(), data, 4);
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        ClusteringAlgorithm km = new KMeans(3, 100, new EuclideanDistance());
        clusters = km.partition(data);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of hasAt method, of class ClusterList.
     */
    @Test
    public void testHasAt() {
    }

    /**
     * Test of getClusterLabel method, of class ClusterList.
     */
    @Test
    public void testGetClusterLabel() {
    }

    /**
     * Test of put method, of class ClusterList.
     */
    @Test
    public void testPut_Cluster() {
    }

    /**
     * Test of put method, of class ClusterList.
     */
    @Test
    public void testPut_int_Cluster() {
    }

    /**
     * Test of merge method, of class ClusterList.
     */
    @Test
    public void testMerge() {
    }

    /**
     * Test of instancesCount method, of class ClusterList.
     */
    @Test
    public void testInstancesCount() {
        assertEquals(data.size(), clusters.instancesCount());
    }

    /**
     * Test of getCentroid method, of class ClusterList.
     */
    @Test
    public void testGetCentroid() {
        Instance centroid = clusters.getCentroid();
        System.out.println("centroid: " + centroid);
    }

    /**
     * Test of instancesIterator method, of class ClusterList.
     */
    @Test
    public void testInstancesIterator() {
        Iterator<Instance> iter = clusters.instancesIterator();
        Instance elem;
        int i = 0;
        while(iter.hasNext()){
            elem = iter.next();
            assertNotNull(elem);
            i++;
        }
        assertEquals(clusters.instancesCount(), i);
    }
    
}