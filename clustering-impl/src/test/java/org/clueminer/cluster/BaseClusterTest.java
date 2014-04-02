package org.clueminer.cluster;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openide.util.Exceptions;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class BaseClusterTest {

    private static Dataset<? extends Instance> irisData;
    private static Clustering<Cluster> irisClusters;

    public BaseClusterTest() {
    }

    @Before
    public void setUp() {
        irisDataset(); //preload
        irisClusters = new ClusterList(3);
        Cluster a = new BaseCluster(50);
        a.setName("cluster 1");
        a.setClusterId(0);
        a.setAttributes(irisData.getAttributes());
        Cluster b = new BaseCluster(50);
        b.setName("cluster 2");
        b.setAttributes(irisData.getAttributes());
        b.setClusterId(1);
        Cluster c = new BaseCluster(50);
        c.setName("cluster 3");
        c.setAttributes(irisData.getAttributes());
        c.setClusterId(2);
        for (int i = 0; i < 50; i++) {
            a.add(irisData.instance(i));
            b.add(irisData.instance(i + 50));
            c.add(irisData.instance(i + 100));
        }

        irisClusters.add(a);
        irisClusters.add(b);
        irisClusters.add(c);
    }

    @After
    public void tearDown() {
    }

    public static Dataset<? extends Instance> irisDataset() {
        if (irisData == null) {
            CommonFixture tf = new CommonFixture();
            irisData = new ArrayDataset<Instance>(150, 4);
            ARFFHandler arff = new ARFFHandler();
            try {
                arff.load(tf.irisArff(), irisData, 4);
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return irisData;
    }

    @Test
    public void testConstructor_1arg() {
        BaseCluster subject = new BaseCluster(irisData);
        assertEquals(4, subject.attributeCount());
    }

    /**
     * Test of add method, of class BaseCluster.
     */
    @Test
    public void testAdd() {
    }

    /**
     * Test of contains method, of class BaseCluster.
     */
    @Test
    public void testContains() {
    }

    /**
     * Test of setClusterId method, of class BaseCluster.
     */
    @Test
    public void testSetClusterId() {

    }

    /**
     * Test of getClusterId method, of class BaseCluster.
     */
    @Test
    public void testGetClusterId() {
        assertEquals(0, irisClusters.get(0).getClusterId());
    }

    /**
     * Test of getColor method, of class BaseCluster.
     */
    @Test
    public void testGetColor() {
    }

    /**
     * Test of setColor method, of class BaseCluster.
     */
    @Test
    public void testSetColor() {
    }

    /**
     * Test of getCentroid method, of class BaseCluster.
     */
    @Test
    public void testGetCentroid() {
    }

    /**
     * Test of countMutualElements method, of class BaseCluster.
     */
    @Test
    public void testCountMutualElements() {
    }

    /**
     * Test of toString method, of class BaseCluster.
     */
    @Test
    public void testToString() {
    }

    @Test
    public void testGetSize() {
        assertEquals(50, irisClusters.get(0).size());
    }

}
