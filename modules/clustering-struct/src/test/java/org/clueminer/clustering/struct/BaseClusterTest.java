package org.clueminer.clustering.struct;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class BaseClusterTest {

    private static Dataset<Instance> irisData;
    private static Clustering<Instance, Cluster<Instance>> irisClusters;
    private static final double delta = 1e-9;

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
            irisData = new ArrayDataset<>(150, 4);
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
        //assertEquals(true, irisClusters.contains(irisClusters.get(0)));
    }

    /**
     * Test of setClusterId method, of class BaseCluster.
     */
    @Test
    public void testSetClusterId() {
        Cluster x = new BaseCluster(1);
        assertEquals(0, x.getClusterId());
        x.setClusterId(99);
        assertEquals(99, x.getClusterId());
    }

    /**
     * Test of getClusterId method, of class BaseCluster.
     */
    @Test
    public void testGetClusterId() {
        //cluster ID start from 0 (programmers readable)
        assertEquals(0, irisClusters.get(0).getClusterId());
    }

    /**
     * Test of getColor method, of class BaseCluster.
     */
    @Test
    public void testGetColor() {
        Cluster x = new BaseCluster(1);
        assertNull(x.getColor());
    }

    /**
     * Test of setColor method, of class BaseCluster.
     */
    @Test
    public void testSetColor() {
        Cluster x = new BaseCluster(1);
        Color c = Color.RED;
        x.setColor(c);
        assertEquals(c, x.getColor());
    }

    /**
     * Test of getCentroid method, of class BaseCluster.
     */
    @Test
    public void testGetCentroid() {
        Cluster a = irisClusters.get(0);
        Instance centroid = a.getCentroid();
        //artificial instance, should not be included in original data
        assertEquals(false, a.contains(centroid));
    }

    @Test
    public void testSingletonCentroid() {
        Cluster a = new BaseCluster(5);
        a.attributeBuilder().create("x", "NUMERIC");
        a.attributeBuilder().create("y", "NUMERIC");

        a.builder().create(new double[]{2, 2});
        assertEquals(2, a.attributeCount());
        //centroid should be [2, 3]
        assertEquals(2, a.getCentroid().get(0), delta);
        assertEquals(2, a.getCentroid().get(1), delta);
    }

    @Test
    public void testModifyingCentroid() {
        Cluster a = new BaseCluster(5);
        a.attributeBuilder().create("x", "NUMERIC");
        a.attributeBuilder().create("y", "NUMERIC");

        a.builder().create(new double[]{2, 2});
        a.builder().create(new double[]{4, 0});
        assertEquals(2, a.attributeCount());
        //centroid should be [2, 3]
        assertEquals(3, a.getCentroid().get(0), delta);
        assertEquals(1, a.getCentroid().get(1), delta);
        //after adding instance centroid must be recomputed
        a.builder().create(new double[]{6, 1});
        assertEquals(4, a.getCentroid().get(0), delta);
        assertEquals(1, a.getCentroid().get(1), delta);
    }

    /**
     * Test of countMutualElements method, of class BaseCluster.
     */
    @Test
    public void testCountMutualElements() {
        Cluster a = irisClusters.get(0);
        Cluster b = irisClusters.get(1);

        assertEquals(0, a.countMutualElements(b));
        assertEquals(50, a.countMutualElements(a));
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

    @Test
    public void testHashCode() {
        int hash = irisClusters.hashCode();
        assertEquals(true, hash > 0);
        assertEquals(false, irisClusters.get(0).hashCode() == irisClusters.get(1).hashCode());
        assertEquals(false, irisClusters.get(0).hashCode() == irisClusters.get(2).hashCode());
        assertEquals(false, irisClusters.get(1).hashCode() == irisClusters.get(2).hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(false, irisClusters.get(0).equals(irisClusters.get(1)));
    }

    @Test
    public void testAttrAccessor() {
        int i = 0;
        Iterator it = irisClusters.get(0).attrCollection(0).iterator();
        while (it.hasNext()) {
            assertNotNull(it.next());
            i++;
        }
        assertEquals(50, i);
    }

}
