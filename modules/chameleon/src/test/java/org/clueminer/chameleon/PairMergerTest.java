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

import org.clueminer.chameleon.similarity.RiRcSimilarity;
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
import org.junit.Test;

/**
 *
 * @author deric
 */
public class PairMergerTest {

    private PairMerger merger;

    @Test
    public void testGetHierarchy() {

        Dataset<? extends Instance> dataset = FakeDatasets.usArrestData();
        KNNGraphBuilder knn = new KNNGraphBuilder();
        int k = 5;
        int maxPartitionSize = 20;
        Graph g = new AdjMatrixGraph();
        Bisection bisection = new FiducciaMattheyses(10);
        g.ensureCapacity(dataset.size());
        g = knn.getNeighborGraph(dataset, g, k);

        Partitioning partitioning = new RecursiveBisection(bisection);
        ArrayList<LinkedList<Node<Instance>>> partitioningResult = partitioning.partition(maxPartitionSize, g);

        RiRcSimilarity<Instance> eval = new RiRcSimilarity<>();
        merger = new PairMerger();
        merger.initialize(partitioningResult, g, bisection);
        merger.setMergeEvaluation(eval);

        ArrayList<GraphCluster<Instance>> clusters = merger.createClusters(partitioningResult, bisection);

        merger.computeExternalProperties();
        assertEquals(7, clusters.size());

        assertEquals(3, clusters.get(0).size());
        assertEquals(13, clusters.get(1).size());
        assertEquals(13, clusters.get(2).size());

    }

}
