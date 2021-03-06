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

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.exception.ParserError;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.arff.ARFFHandler;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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
    private static final double DELTA = 1e-9;

    @Before
    public void setUp() {
        irisDataset(); //preload
        irisClusters = new ClusterList(3);
        irisClusters.lookupAdd(irisData);
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
            } catch (IOException | ParserError ex) {
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

    @Test
    public void testAdd() {
    }

    @Test
    public void testContains() {
        //assertEquals(true, irisClusters.contains(irisClusters.get(0)));
    }

    @Test
    public void testSetClusterId() {
        Cluster x = new BaseCluster(1);
        assertEquals(0, x.getClusterId());
        x.setClusterId(99);
        assertEquals(99, x.getClusterId());
    }

    @Test
    public void testGetClusterId() {
        //cluster ID start from 0 (programmers readable)
        assertEquals(0, irisClusters.get(0).getClusterId());
    }

    @Test
    public void testGetColor() {
        Cluster x = new BaseCluster(1);
        assertNull(x.getColor());
    }

    @Test
    public void testSetColor() {
        Cluster x = new BaseCluster(1);
        Color c = Color.RED;
        x.setColor(c);
        assertEquals(c, x.getColor());
    }

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
        assertEquals(2, a.getCentroid().get(0), DELTA);
        assertEquals(2, a.getCentroid().get(1), DELTA);
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
        assertEquals(3, a.getCentroid().get(0), DELTA);
        assertEquals(1, a.getCentroid().get(1), DELTA);
        //after adding instance centroid must be recomputed
        a.builder().create(new double[]{6, 1});
        assertEquals(4, a.getCentroid().get(0), DELTA);
        assertEquals(1, a.getCentroid().get(1), DELTA);
    }

    @Test
    public void testCountMutualElements() {
        Cluster a = irisClusters.get(0);
        Cluster b = irisClusters.get(1);

        assertEquals(0, a.countMutualElements(b));
        assertEquals(50, a.countMutualElements(a));
    }

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
        System.out.println("iris hash: " + hash);
        assertNotEquals(0, hash);
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
