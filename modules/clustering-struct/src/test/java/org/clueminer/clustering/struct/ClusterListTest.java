/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.clustering.struct;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.exception.ParserError;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.arff.ARFFHandler;
import org.clueminer.utils.Props;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class ClusterListTest<E extends Instance, C extends Cluster<E>> {

    private static ClusterList subject;
    private static final CommonFixture CF = new CommonFixture();
    private static final double DELTA = 1e-9;
    private static Dataset<? extends Instance> irisCache;

    private static final Logger LOG = LoggerFactory.getLogger(ClusterListTest.class);

    @Before
    public void setUp() {
        subject = new ClusterList(5);
        subject.createCluster();
        subject.createCluster();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetName() {
        String str = "my clustering";
        subject.setName(str);
        assertEquals(str, subject.getName());
    }

    @Test
    public void testEnsureCapacity() {
        ClusterList clusters = new ClusterList();
        clusters.ensureCapacity(4);
        clusters.createCluster();
        assertEquals(1, clusters.size());
        assertEquals(6, clusters.getCapacity());
    }

    @Test
    public void testHasAt() {
        assertEquals(true, subject.hasAt(0));
        assertEquals(true, subject.hasAt(1));

        ClusterList clusters = new ClusterList(3);
        assertEquals(false, clusters.hasAt(0));
        assertEquals(false, clusters.hasAt(1));
        assertEquals(false, clusters.hasAt(2));
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
        Clustering<E, C> clusters = new ClusterList<>(5);
        C clus = (C) new BaseCluster(5);
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
        Clustering<E, C> clusters = createClusters();
        assertEquals(12, clusters.instancesCount());
    }

    @Test
    public void testGetCentroid() {
        Clustering<E, C> clusters = createClusters2();
        assertEquals(3, clusters.get(0).size());
        Instance centroid = clusters.getCentroid();
        System.out.println("centroid: " + centroid.toString());
        assertEquals(3, clusters.get(0).size());
        //centroid shoould be average of all cluster's centroids
        assertEquals(false, Double.isNaN(centroid.get(0)));
        assertEquals(1.0, centroid.get(1), DELTA);
    }

    private Clustering<E, C> createEmptyClusters() {
        Clustering<E, C> clusters = new ClusterList<>(5);
        return clusters;
    }

    private Clustering<E, C> createClusters() {
        Clustering<E, C> clusters = new ClusterList<>(5);
        assertEquals(0, clusters.size());
        instanceIter(clusters);
        int size = 4;
        for (int i = 0; i < size; i++) {
            Cluster clust = clusters.createCluster();
            clust.attributeBuilder().create("x", "NUMERIC");
            clust.attributeBuilder().create("y", "NUMERIC");
            clust.builder().create(new double[]{1.0, 1.0});
            clust.builder().create(new double[]{1.0, 0.0});
            clust.builder().create(new double[]{1.0, 2.0});
        }

        return clusters;
    }

    private Clustering<E, C> createClusters(int size) {
        Clustering<E, C> clusters = new ClusterList<>(size);
        assertEquals(0, clusters.size());
        instanceIter(clusters);
        for (int i = 0; i < size; i++) {
            Cluster clust = clusters.createCluster();
            clust.attributeBuilder().create("x", "NUMERIC");
            clust.attributeBuilder().create("y", "NUMERIC");
            clust.builder().create(new double[]{2.0 * i, 7 * i});
            clust.builder().create(new double[]{0.5 * i, 6 * i});
            clust.builder().create(new double[]{1.0, 0.0});
        }

        return clusters;
    }

    private Clustering<E, C> createClustersOrdered() {
        Clustering<E, C> clusters = new ClusterList<>(5);
        instanceIter(clusters);
        Cluster clust = clusters.createCluster();
        clust.attributeBuilder().create("x", "NUMERIC");
        clust.attributeBuilder().create("y", "NUMERIC");
        clust.builder().create(new double[]{1.0, 1.0});
        clust.builder().create(new double[]{1.0, 0.0});
        clust.builder().create(new double[]{1.0, 2.0});
        return clusters;
    }

    private Clustering<E, C> createClustersDifferentOrder() {
        Clustering<E, C> clusters = new ClusterList<>(5);
        instanceIter(clusters);
        Cluster clust = createXYCluster(clusters);
        clust.add(new DoubleArrayDataRow(new double[]{1.0, 0.0}));
        clust.add(new DoubleArrayDataRow(new double[]{1.0, 1.0}));
        clust.add(new DoubleArrayDataRow(new double[]{1.0, 2.0}));
        return clusters;
    }

    private Clustering<E, C> createClusters2() {
        Clustering<E, C> clusters = new ClusterList<>(5);
        instanceIter(clusters);
        Cluster clust = createXYCluster(clusters);
        clust.add(new DoubleArrayDataRow(new double[]{1.0, 0.0}));
        clust.add(new DoubleArrayDataRow(new double[]{5.0, 1.0}));
        clust.add(new DoubleArrayDataRow(new double[]{1.0, 2.0}));
        return clusters;
    }

    private C createXYCluster(Clustering<E, C> clusters) {
        C clust = clusters.createCluster();
        clust.attributeBuilder().create("x", "NUMERIC");
        clust.attributeBuilder().create("y", "NUMERIC");
        return clust;
    }

    private Clustering<E, C> createClustersWithNoise() {
        Clustering<E, C> clusters = new ClusterList<>(5);
        assertEquals(0, clusters.size());
        instanceIter(clusters);
        int size = 4;
        Cluster clust;
        for (int i = 0; i < size; i++) {
            clust = createXYCluster(clusters);
            clust.builder().create(new double[]{1.0 * i, 2.0 * i});
            clust.builder().create(new double[]{1.0 * i, i});
            clust.builder().create(new double[]{1.0 * i + 1, 2 * i + 2});
        }
        clust = clusters.createCluster();
        clust.setName(Algorithm.OUTLIER_LABEL);
        for (int i = 0; i < 5; i++) {
            clust.builder().create(new double[]{99.0 * i, 99.0 * i + 20});
        }

        return clusters;
    }

    /**
     * Noisy cluster is created as the first one
     *
     * @return
     */
    private Clustering<E, C> createClustersWithNoise2() {
        Clustering<E, C> clusters = new ClusterList<>(5);
        assertEquals(0, clusters.size());
        instanceIter(clusters);
        int size = 4;
        Cluster clust;
        clust = createXYCluster(clusters);
        clust.setName(Algorithm.OUTLIER_LABEL);
        for (int i = 0; i < 5; i++) {
            clust.builder().create(new double[]{99.0 * i, 99.0 * i + 20});
        }
        for (int i = 0; i < size; i++) {
            clust = createXYCluster(clusters);
            clust.builder().create(new double[]{1.0 * i, 2.0 * i});
            clust.builder().create(new double[]{1.0 * i, i});
            clust.builder().create(new double[]{1.0 * i + 1, 2 * i + 2});
        }

        return clusters;
    }

    @Test
    public void testWithoutNoise() {
        Clustering<E, C> clusters = createClustersWithNoise();
        Iterator<C> iter = clusters.withoutNoise();
        //including noisy cluster
        assertEquals(5, clusters.size());
        int i = 0;
        while (iter.hasNext()) {
            C clust = iter.next();
            i++;
            assertEquals(false, clust.isNoise());
        }
        assertEquals(4, i);
    }

    @Test
    public void testWithoutNoise2() {
        Clustering<E, C> clusters = createClustersWithNoise2();
        Iterator<C> iter = clusters.withoutNoise();
        //including noisy cluster
        assertEquals(5, clusters.size());
        int i = 0;
        while (iter.hasNext()) {
            C clust = iter.next();
            i++;
            assertEquals(false, clust.isNoise());
        }
        assertEquals(4, i);
    }

    @Test
    public void testInstancesIterator() {
        Clustering<E, C> clusters = createClustersOrdered();
        Cluster clust = clusters.get(0);
        assertEquals(3, clust.size());
        assertEquals(3, clusters.instancesCount());
    }

    private void instanceIter(Clustering<E, C> clusters) {
        Iterator<E> iter = clusters.instancesIterator();
        E elem;
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
        Clustering<E, C> clusters = new ClusterList<>(3);
        instanceIter(clusters);
        C c1 = clusters.createCluster();
        c1.attributeBuilder().create("x", "NUMERIC");
        c1.attributeBuilder().create("y", "NUMERIC");
        E i1 = (E) new DoubleArrayDataRow(new double[]{1.0, 0.0});
        i1.setIndex(0);
        c1.add(i1);
        C c2 = clusters.createCluster();
        c2.setAttributes(c1.getAttributes());
        E i2 = (E) new DoubleArrayDataRow(new double[]{5.0, 1.0});
        i2.setIndex(1);
        c2.add(i2);
        C c3 = clusters.createCluster();
        c3.setAttributes(c1.getAttributes());
        E i3 = (E) new DoubleArrayDataRow(new double[]{1.0, 2.0});
        i3.setIndex(2);
        c3.add(i3);

        assertEquals(c1, clusters.assignedCluster(i1));
        assertEquals(c1.getClusterId(), clusters.assignedCluster(i1.getIndex()));
        assertEquals(c2, clusters.assignedCluster(i2));
        assertEquals(c2.getClusterId(), clusters.assignedCluster(i2.getIndex()));
        assertEquals(c3, clusters.assignedCluster(i3));
        assertEquals(c3.getClusterId(), clusters.assignedCluster(i3.getIndex()));

    }

    @Test
    public void testGet() {
        Clustering<E, C> clusters = createClusters();
        Cluster clust = clusters.get(3);
        assertEquals(3, clust.size());
    }

    /**
     * Test of iterator method, of class ClusterList.
     */
    @Test
    public void testIterator() {
        Clustering<E, C> clusters = new ClusterList(10);
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

        Iterator<C> iter = clusters.iterator();
        i = 0;
        while (iter.hasNext()) {
            iter.next();
            i++;
        }
        assertEquals(6, i);
    }

    @Test
    public void testShortForEach() {
        Clustering<E, C> clust = createClusters();
        for (C elem : clust) {
            assertNotNull(elem);
        }
    }

    @Test
    public void testIsEmpty() {
        assertEquals(false, subject.isEmpty());

        //empty clusters
        Clustering<E, C> clusters = new ClusterList(3);
        assertEquals(true, clusters.isEmpty());
        clusters.add((C) new BaseCluster(1));
        assertEquals(false, clusters.isEmpty());
    }

    @Test
    public void testContains() {
        Clustering<E, C> c1 = createClusters();
        Clustering<E, C> c2 = createClustersDifferentOrder();

        for (int i = 0; i < c2.size(); i++) {
            assertEquals(true, c1.contains(c2.get(i)));
        }
    }

    @Test
    public void testRemove() {
        Clustering<E, C> c1 = createClusters();
        assertEquals(4, c1.size());

        c1.remove(c1.get(0));
        assertEquals(3, c1.size());
        //another cluster with ID 0 will be present
        assertEquals(0, c1.get(0).getClusterId());

        c1.remove(2);
        for (Cluster<E> a : c1) {
            assertNotNull(a);
            assertNotEquals(0, a.size());
        }
    }

    @Test
    public void testRemove2() {
        int n = 10;
        Clustering<E, C> c1 = createClusters(n);
        assertEquals(n, c1.size());
        LOG.debug("{} clusters", n);
        for (Cluster<E> a : c1) {
            LOG.debug("{}", a.toString());
            assertNotNull(a);
            assertNotEquals(0, a.size());
        }
        assertEquals(n, c1.size());
        assertTrue(c1.remove(0));
        LOG.debug("first {}", c1.get(0));
        assertEquals(n - 1, c1.size());
        assertTrue(c1.remove(5));
        assertTrue(c1.remove(6));
        assertFalse(c1.remove(7));
        LOG.debug("7 clusters");
        for (Cluster<E> a : c1) {
            LOG.debug("{}", a.toString());
            assertNotNull(a);
            assertNotEquals(0, a.size());
        }

        Clustering<E, C> c2 = createClusters(1);
        c2.remove(0);
        assertEquals(0, c2.size());
    }

    @Test
    public void testRemoveInt() {
        Clustering<E, C> c1 = createClusters();
        assertEquals(4, c1.size());

        assertTrue(c1.remove(0));
        assertEquals(3, c1.size());
        //another cluster with ID 0 will be present
        assertEquals(0, c1.get(0).getClusterId());
    }

    @Test
    public void testContainsAll() {
        Clustering<E, C> c1 = createClusters();
        Clustering<E, C> c2 = createClustersDifferentOrder();
        assertEquals(true, c1.containsAll(c2));

        Clustering<E, C> c3 = createClusters2();
        assertEquals(false, c1.containsAll(c3));
    }

    @Test
    public void testGetByName() {
        Clustering c1 = createClusters();
        Cluster c = c1.get("cluster 1");
        assertNotNull(c);
        assertEquals(3, c.size());
    }

    @Test
    public void testClear() {
        Clustering clust = createClusters();
        assertEquals(4, clust.size());
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
        if (irisCache == null) {
            irisCache = new ArrayDataset(150, 4);
            ARFFHandler arff = new ARFFHandler();
            try {
                arff.load(CF.irisArff(), irisCache, 4);
            } catch (ParserError ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return irisCache;
    }

    private Clustering irisWrong() throws IOException {
        Dataset<? extends Instance> irisData = loadIris();
        Clustering irisWrong = new ClusterList(3);
        irisWrong.lookupAdd(irisCache);
        Cluster a = new BaseCluster(4);
        a.setName("cluster 1");
        a.setAttributes(irisData.getAttributes());
        //add few instances to first cluster
        a.add(irisData.instance(0));
        a.add(irisData.instance(1));
        a.add(irisData.instance(149));

        Cluster b = new BaseCluster(50);
        b.setName("cluster 2");
        b.setAttributes(irisData.getAttributes());
        b.add(irisData.instance(3));
        b.add(irisData.instance(4));
        b.add(irisData.instance(5));
        b.add(irisData.instance(6));
        b.add(irisData.instance(2));
        Cluster c = new BaseCluster(50);
        c.setName("cluster 3");
        c.setAttributes(irisData.getAttributes());
        //rest goes to the last cluster
        for (int i = 7; i < 149; i++) {
            c.add(irisData.instance(i));
        }

        irisWrong.add(c);
        irisWrong.add(a);
        irisWrong.add(b);

        return irisWrong;
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
    public void testFingerprint() throws IOException {
        Clustering clusters = createClustersOrdered();
        assertEquals("[3]", clusters.fingerprint());

        clusters = irisWrong();
        assertEquals("[3,5,142]", clusters.fingerprint());
        assertEquals(150, clusters.instancesCount());
    }

    @Test
    public void testHashCode() {
        Clustering c1 = createClustersOrdered();
        Clustering c2 = createClustersDifferentOrder();
        assertEquals(c1.hashCode(), c2.hashCode());

        Clustering c3 = createClusters2();
        Clustering c4 = createEmptyClusters();
        assertNotSame(c2.hashCode(), c3.hashCode());
        assertNotSame(c4.hashCode(), c3.hashCode());
    }

    @Test
    public void testEquals() {
        Clustering c1 = createClustersOrdered();
        Clustering c2 = createClustersDifferentOrder();
        assertEquals(true, c1.equals(c2));
        assertEquals(true, c2.equals(c1));

        Clustering c3 = createClusters2();
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

    @Test
    public void testAddingToZeroSize() {
        //clustering with 0 capacity
        Clustering clusters = new ClusterList(0);
        clusters.createCluster(0);
        assertEquals(1, clusters.size());
    }

    @Test
    public void testSetName() {
    }

    @Test
    public void testPut_Cluster() {
    }

    /**
     * Creates fake clustering of iris data with k clusters. Instances are
     * assigned according to ordering in original dataset.
     *
     * @param k
     * @return
     * @throws IOException
     */
    public Clustering<E, C> irisClustering(int k) throws IOException {
        Dataset<? extends Instance> iris = loadIris();
        Clustering clust = new ClusterList(k);
        Cluster c = null;
        int perCluster = iris.size() / k;
        int j = 0;
        for (int i = 0; i < iris.size(); i++) {
            if (i % perCluster == 0) {
                c = clust.createCluster(j++);
            }
            c.add(iris.get(i));
        }
        return clust;
    }

    /**
     * Test access to instances in all clusters
     *
     * @throws IOException
     */
    @Test
    public void testInstance() throws IOException {
        // iris clustering with 10 clusters
        Clustering iris10 = irisClustering(10);
        assertEquals(10, iris10.size());
        assertEquals(150, iris10.instancesCount());

        Dataset<? extends Instance> iris = loadIris();

        for (int i = 0; i < iris.size(); i++) {
            //i-th object in dataset should equal i-th object in clustering
            //(no matter how many clusters do we have)
            assertEquals(iris.get(i), iris10.instance(i));
        }
    }

    @Test
    public void testAssignedCluster_int() {
    }

    @Test
    public void testAssignedCluster_Instance() {
    }

    @Test
    public void testGet_int() {
    }

    @Test
    public void testGetId() {
        assertEquals(0, subject.getId());
        subject.setId(123);
        assertEquals(123, subject.getId());
    }

    @Test
    public void testGet_String() {
    }

    @Test
    public void testGetEvaluationTable() {
    }

    @Test
    public void testGetNoise() {
        Clustering<E, C> list = createClusters();
        C noise = list.getNoise();
        assertNotNull(noise);
        assertEquals(0, noise.size());
    }

    @Test
    public void testSetEvaluationTable() {
    }

    @Test
    public void testValidation() {
        ClusterList list = new ClusterList(3);
        assertEquals(false, list.hasValidation("foo"));
        list.setValidation("foo", 123.0);
        assertEquals(true, list.hasValidation("foo"));
        assertEquals(123.0, list.getValidation("foo"), DELTA);
    }
}
