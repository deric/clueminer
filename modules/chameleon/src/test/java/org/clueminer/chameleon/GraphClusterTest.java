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
import org.clueminer.chameleon.similarity.RiRcSimilarity;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.knn.KNNGraphBuilder;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.Partitioning;
import org.clueminer.partitioning.impl.FiducciaMattheyses;
import org.clueminer.partitioning.impl.RecursiveBisection;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        Props props = new Props();
        Graph g = new AdjMatrixGraph();
        Bisection bisection = new FiducciaMattheyses(10);
        g.ensureCapacity(dataset.size());
        g = knn.getNeighborGraph(dataset, g, k);

        Partitioning partitioning = new RecursiveBisection(bisection);
        ArrayList<ArrayList<Node<Instance>>> partitioningResult = partitioning.partition(maxPartitionSize, g, props);

        RiRcSimilarity<Instance> eval = new RiRcSimilarity<>();
        PairMerger merger = new PairMerger();
        merger.initialize(partitioningResult, g, bisection, props);
        merger.setMergeEvaluation(eval);
        Clustering<Instance, GraphCluster<Instance>> clusters = merger.createClusters(partitioningResult, bisection, props);
        cluster = clusters.get(0);
        assertNotNull(cluster);
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
    public void testGetEdgeCount() {
        assertEquals(120, cluster.getEdgeCount());
    }

    @Test
    public void testGetClusterId() {
        assertEquals(0, cluster.getClusterId());
    }

}
