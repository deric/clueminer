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
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class RiRcSimilarityTest {

    private final RiRcSimilarity<Instance> subject;
    private static final double delta = 1e-9;

    public RiRcSimilarityTest() {
        subject = new RiRcSimilarity<>();
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    @Test
    public void testScore() {
        Dataset<? extends Instance> dataset = FakeDatasets.irisDataset();
        KNNGraphBuilder knn = new KNNGraphBuilder();
        int k = 5;
        int maxPartitionSize = 20;
        double closenessPriority = 2.0;
        Graph g = new AdjMatrixGraph();
        Bisection bisection = new FiducciaMattheyses(10);
        g.ensureCapacity(dataset.size());
        g = knn.getNeighborGraph(dataset, g, k);

        Partitioning partitioning = new RecursiveBisection(bisection);
        ArrayList<LinkedList<Node>> partitioningResult = partitioning.partition(maxPartitionSize, g);

        StandardSimilarity merger = new StandardSimilarity(g, bisection, closenessPriority);
        ArrayList<GraphCluster<Instance>> clusters = merger.createClusters(partitioningResult, bisection);
        merger.computeExternalProperties();
        System.out.println("size: " + clusters.size());
        GraphCluster<Instance> cluster = clusters.get(0);

        int j;
        Props pref = new Props();
        double sc;
        for (int i = 0; i < clusters.size() - 1; i++) {
            j = i + 1;
            sc = subject.score(clusters.get(i), clusters.get(j), pref);
            assertEquals(merger.computeSimilarity(i, j), sc, delta);
        }
    }

}
