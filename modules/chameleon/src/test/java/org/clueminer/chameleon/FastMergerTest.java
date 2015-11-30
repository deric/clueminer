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
import org.clueminer.chameleon.similarity.ShatovskaSimilarity;
import org.clueminer.chameleon.similarity.Standard;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
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
import org.junit.Test;

/**
 *
 * @author deric
 */
public class FastMergerTest {

    private FastMerger merger;

//    @Test
    public void testUsArrest() {
        Dataset<? extends Instance> dataset = FakeDatasets.usArrestData();
        KNNGraphBuilder knn = new KNNGraphBuilder();
        int k = 3;
        int maxPartitionSize = 20;
        Graph g = new AdjMatrixGraph();
        Props props = new Props();
        Bisection bisection = new FiducciaMattheyses(20);
        g.ensureCapacity(dataset.size());
        g = knn.getNeighborGraph(dataset, g, k);

        Partitioning partitioning = new RecursiveBisection(bisection);
        ArrayList<ArrayList<Node>> partitioningResult = partitioning.partition(maxPartitionSize, g, props);

        merger = new FastMerger();
        merger.setMergeEvaluation(new Standard());
        merger.initialize(partitioningResult, g, bisection, props);

        Props pref = new Props();
        HierarchicalResult result = merger.getHierarchy(dataset, pref);
        DendroTreeData tree = result.getTreeData();
        tree.print();
    }

    @Test
    public void testVehicle() {
        Dataset<? extends Instance> dataset = FakeDatasets.vehicleDataset();
        KNNGraphBuilder knn = new KNNGraphBuilder();
        int k = 3;
        int maxPartitionSize = 20;
        Graph g = new AdjMatrixGraph();
        Props props = new Props();
        Bisection bisection = new FiducciaMattheyses(20);
        g.ensureCapacity(dataset.size());
        g = knn.getNeighborGraph(dataset, g, k);

        Partitioning partitioning = new RecursiveBisection(bisection);
        ArrayList<ArrayList<Node>> partitioningResult = partitioning.partition(maxPartitionSize, g, props);

        merger = new FastMerger();
        merger.setDistanceMeasure(EuclideanDistance.getInstance());
        merger.setMergeEvaluation(new ShatovskaSimilarity());
        merger.initialize(partitioningResult, g, bisection, props);

        Props pref = new Props();
        HierarchicalResult result = merger.getHierarchy(dataset, pref);
        DendroTreeData tree = result.getTreeData();
        //tree.print();
    }

}
