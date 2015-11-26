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
package org.clueminer.chameleon.similarity;

import java.util.ArrayList;
import org.clueminer.chameleon.GraphCluster;
import org.clueminer.chameleon.PairMerger;
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
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ShatovskaSimilarityTest {

    private final ShatovskaSimilarity<Instance> subject;
    private static final double delta = 1e-9;

    public ShatovskaSimilarityTest() {
        subject = new ShatovskaSimilarity<>();
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
        Props pref = new Props();
        Graph g = new AdjMatrixGraph();
        Bisection bisection = new FiducciaMattheyses(10);
        g.ensureCapacity(dataset.size());
        g = knn.getNeighborGraph(dataset, g, k);

        Partitioning partitioning = new RecursiveBisection(bisection);
        ArrayList<ArrayList<Node<Instance>>> partitioningResult = partitioning.partition(maxPartitionSize, g, pref);

        PairMerger merger = new PairMerger();
        merger.setMergeEvaluation(subject);
        merger.initialize(partitioningResult, g, bisection, pref);
        Clustering<Instance, GraphCluster<Instance>> clusters = merger.createClusters(partitioningResult, bisection, pref);
        merger.computeExternalProperties(clusters);
        assertEquals(12, clusters.size());

        assertEquals(4.464646866748596E-13, subject.score(clusters.get(0), clusters.get(1), pref), delta);
        assertEquals(0.0, subject.score(clusters.get(1), clusters.get(2), pref), delta);
        assertEquals(0.0, subject.score(clusters.get(2), clusters.get(3), pref), delta);
        assertEquals(0.7164750502687347, subject.score(clusters.get(3), clusters.get(4), pref), delta);
        assertEquals(0.0, subject.score(clusters.get(4), clusters.get(5), pref), delta);
        assertEquals(0.0, subject.score(clusters.get(5), clusters.get(6), pref), delta);
        assertEquals(0.3857386356649811, subject.score(clusters.get(6), clusters.get(7), pref), delta);
        assertEquals(5.237476138021256E-13, subject.score(clusters.get(7), clusters.get(8), pref), delta);
        assertEquals(0.07295002241131861, subject.score(clusters.get(9), clusters.get(10), pref), delta);

        assertEquals(0.0, subject.score(clusters.get(8), clusters.get(9), pref), delta);
        assertEquals(0.0, subject.score(clusters.get(10), clusters.get(11), pref), delta);
    }

}
