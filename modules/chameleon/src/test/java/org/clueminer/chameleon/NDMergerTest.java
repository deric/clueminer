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
package org.clueminer.chameleon;

import java.util.ArrayList;
import org.clueminer.chameleon.similarity.Standard;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
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
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class NDMergerTest<E extends Instance> {

    private NDMerger merger;
    private static final Logger LOG = LoggerFactory.getLogger(NDMergerTest.class);

    @Test
    public void testIris() {
        Dataset<E> dataset = (Dataset<E>) FakeDatasets.irisDataset();
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

        merger = new NDMerger();
        merger.setDistanceMeasure(EuclideanDistance.getInstance());
        merger.setMergeEvaluation(new Standard());
        merger.initialize(partitioningResult, g, bisection, props);

        Props pref = new Props();
        HierarchicalResult result = merger.getHierarchy(dataset, pref);
        Clustering<E, Cluster<E>> clust = result.getClustering();

        DendroTreeData tree = result.getTreeData();
        LOG.debug("tree: ");
        tree.print();
        Clustering<E, Cluster<E>> c = result.getClustering();
        assertEquals(dataset.size(), clust.instancesCount());
        //assertEquals(dataset.size(), c.instancesCount());
        //we don't want empty clusters
        for (Cluster<E> a : c) {
            assertNotNull(a);
            Assert.assertNotEquals(0, a.size());
        }
    }

}
