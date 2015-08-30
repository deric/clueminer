/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.graph.GraphBuilder.KNNGraphBuilder;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.Partitioning;
import org.clueminer.partitioning.impl.FiducciaMattheyses;
import org.clueminer.partitioning.impl.RecursiveBisection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class GraphClusterTest {

    private static final double delta = 1e-9;
    private static GraphCluster<Instance> cluster;

    public GraphClusterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        Dataset<? extends Instance> dataset = FakeDatasets.schoolData();
        KNNGraphBuilder knn = new KNNGraphBuilder();
        int k = 12;
        int maxPartitionSize = 20;
        Graph g = new AdjMatrixGraph();
        Bisection bisection = new FiducciaMattheyses(10);
        g.ensureCapacity(dataset.size());
        g = knn.getNeighborGraph(dataset, g, k);

        Partitioning partitioning = new RecursiveBisection(bisection);
        ArrayList<LinkedList<Node<Instance>>> partitioningResult = partitioning.partition(maxPartitionSize, g);

        RiRcSimilarity<Instance> merger = new RiRcSimilarity<>();
        merger.setGraph(g);
        merger.setBisection(bisection);
        ArrayList<GraphCluster<Instance>> clusters = merger.createClusters(partitioningResult, bisection);
        cluster = clusters.get(0);
        assertNotNull(cluster);
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testGetIIC() {
        assertEquals(4.055930095394723, cluster.getIIC(), delta);
    }

    @Test
    public void testGetICL() {
        assertEquals(0.07242732313204862, cluster.getICL(), delta);
    }

    @Test
    public void testGetACL() {
        assertEquals(0.062313603475968746, cluster.getACL(), delta);
    }

    @Test
    public void testSetACL() {
    }

    @Test
    public void testGetEdgeCount() {
        assertEquals(120, cluster.getEdgeCount());
    }

    @Test
    public void testGetNodes() {
    }

    @Test
    public void testGetNodeCount() {
    }

    @Test
    public void testGetClusterId() {
        assertEquals(0, cluster.getClusterId());
    }

    @Test
    public void testGetParent() {
    }

    @Test
    public void testSetParent_GraphCluster() {
    }

    @Test
    public void testSetClusterId() {
    }

    @Test
    public void testGetColor() {
    }

    @Test
    public void testSetColor() {
    }

    @Test
    public void testGetCentroid() {
    }

    @Test
    public void testCountMutualElements() {
    }

    @Test
    public void testContains_int() {
    }

    @Test
    public void testGetId() {
    }

    @Test
    public void testSetId() {
    }

    @Test
    public void testGetName() {
    }

    @Test
    public void testSetName() {
    }

    @Test
    public void testGetClasses() {
    }

    @Test
    public void testAdd() {
    }

    @Test
    public void testAddAll_Collection() {
    }

    @Test
    public void testAddAll_Dataset() {
    }

    @Test
    public void testInstance() {
    }

    @Test
    public void testGet_int() {
    }

    @Test
    public void testHasIndex() {
    }

    @Test
    public void testGetRandom() {
    }

    @Test
    public void testSize() {
        assertEquals(17, cluster.size());
    }

    @Test
    public void testIsEmpty() {
        assertEquals(false, cluster.isEmpty());
    }

    @Test
    public void testHasParent() {
    }

    @Test
    public void testAttributeCount() {
    }

    @Test
    public void testClassIndex() {
    }

    @Test
    public void testClassValue() {
    }

    @Test
    public void testChangedClass() {
    }

    @Test
    public void testCopyAttributes() {
    }

    @Test
    public void testAttributeByRole() {
    }

    @Test
    public void testGetAttributes() {
    }

    @Test
    public void testGetAttribute_int() {
    }

    @Test
    public void testAddAttribute() {
    }

    @Test
    public void testGetAttribute_String() {
    }

    @Test
    public void testGetAttributeValue_String_int() {
    }

    @Test
    public void testGetAttributeValue_Attribute_int() {
    }

    @Test
    public void testGet_int_int() {
    }

    @Test
    public void testSetAttributeValue() {
    }

    @Test
    public void testSet_3args() {
    }

    @Test
    public void testSet_int_GenericType() {
    }

    @Test
    public void testSetAttribute() {
    }

    @Test
    public void testSetAttributes() {
    }

    @Test
    public void testBuilder() {
    }

    @Test
    public void testAttributeBuilder() {
    }

    @Test
    public void testCopy() {
    }

    @Test
    public void testDuplicate() {
    }

    @Test
    public void testArrayCopy() {
    }

    @Test
    public void testSetColorGenerator() {
    }

    @Test
    public void testGetPlotter() {
    }

    @Test
    public void testEnsureCapacity() {
    }

    @Test
    public void testGetCapacity() {
    }

    @Test
    public void testAddChild() {
    }

    @Test
    public void testGetChild() {
    }

    @Test
    public void testAsMatrix() {
    }

    @Test
    public void testMin() {
    }

    @Test
    public void testMax() {
    }

    @Test
    public void testResetStats() {
    }

    @Test
    public void testAttrCollection() {
    }

    @Test
    public void testIterator() {
    }

    @Test
    public void testContains_Object() {
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
    }

    @Test
    public void testRemoveAll() {
    }

    @Test
    public void testRetainAll() {
    }

    @Test
    public void testClear() {
    }

    @Test
    public void testSetParent_Dataset() {
    }

}
