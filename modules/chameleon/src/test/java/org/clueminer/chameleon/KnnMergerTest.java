/*
 * Copyright (C) 2011-2016 clueminer.org
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
import org.clueminer.chameleon.similarity.BBK1;
import org.clueminer.chameleon.similarity.ShatovskaSimilarity;
import org.clueminer.chameleon.similarity.Standard;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.graph.adjacencyList.AdjListGraph;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.knn.KNNGraphBuilder;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.Partitioning;
import org.clueminer.partitioning.impl.FiducciaMattheyses;
import org.clueminer.partitioning.impl.RecursiveBisection;
import org.clueminer.report.NanoBench;
import org.clueminer.utils.Props;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class KnnMergerTest<E extends Instance> {

    private KnnMerger merger;
    private static final Logger LOG = LoggerFactory.getLogger(KnnMergerTest.class);

    @Test
    public void testUsArrest() {
        Dataset<E> dataset = (Dataset<E>) FakeDatasets.usArrestData();
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

        merger = new KnnMerger();
        merger.setDistanceMeasure(EuclideanDistance.getInstance());
        merger.setMergeEvaluation(new Standard());
        merger.initialize(partitioningResult, g, bisection, props);

        Props pref = new Props();
        HierarchicalResult result = merger.getHierarchy(dataset, pref);
        Clustering<E, Cluster<E>> clust = result.getClustering();

        int sum = 0;
        for (Cluster c : clust) {
            if (c != null) {
                sum += c.size();
            }
        }
        LOG.info("instances = " + sum);
        // assertEquals(dataset.size(), clust.instancesCount());
        DendroTreeData tree = result.getTreeData();
        LOG.debug("tree: ");
        tree.print();
        Clustering<E, Cluster<E>> c = result.getClustering();
        assertEquals(dataset.size(), c.instancesCount());
        //we don't want empty clusters
        for (Cluster<E> a : c) {
            Assert.assertNotEquals(0, a.size());
        }
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

        merger = new KnnMerger();
        merger.setDistanceMeasure(EuclideanDistance.getInstance());
        merger.setMergeEvaluation(new ShatovskaSimilarity());
        merger.initialize(partitioningResult, g, bisection, props);

        Props pref = new Props();
        HierarchicalResult result = merger.getHierarchy(dataset, pref);
        DendroTreeData tree = result.getTreeData();
        assertNotNull(tree);
        Clustering c = result.getClustering();
        LOG.info("clusters: {}", c.size());
        assertEquals(dataset.size(), c.instancesCount());
        //tree.print();
    }

    @Test
    public void testMemory() {
        final Dataset<? extends Instance> dataset = FakeDatasets.vehicleDataset();
        final KNNGraphBuilder knn = new KNNGraphBuilder();
        final int k = 3;
        final int maxPartitionSize = 20;
        final Props props = new Props();
        final Bisection bisection = new FiducciaMattheyses(20);

        merger = new KnnMerger();
        merger.setDistanceMeasure(EuclideanDistance.getInstance());
        merger.setMergeEvaluation(new ShatovskaSimilarity());

        //measure clustering run
        NanoBench.create().measurements(3).measure(
                "chameleon - knn",
                new Runnable() {

            @Override
                    public void run() {
                Props pref = new Props();
                Graph g = new AdjMatrixGraph();
                g.ensureCapacity(dataset.size());
                g = knn.getNeighborGraph(dataset, g, k);
                Partitioning partitioning = new RecursiveBisection(bisection);
                ArrayList<ArrayList<Node>> partitioningResult = partitioning.partition(maxPartitionSize, g, props);
                merger.initialize(partitioningResult, g, bisection, props);
                HierarchicalResult result = merger.getHierarchy(dataset, pref);
            }

        }
        );
        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Test
    public void testLsunFull() {
        Dataset<E> dataset = (Dataset<E>) FakeDatasets.lsun();
        Chameleon ch = new Chameleon();
        Props props = new Props();
        props.put("merger", "k-NN merger");
        props.put("k-estim", "log10");
        HierarchicalResult res = ch.hierarchy(dataset, props);
        DendroTreeData tree = res.getTreeData();
        assertEquals(dataset.size(), tree.numLeaves());
        assertEquals(dataset.size(), tree.getMapping().length);
        Clustering<E, Cluster<E>> clust = res.getClustering();
        LOG.info("res: {}", clust.fingerprint());
    }

    @Test
    public void testLsun() {
        Dataset<E> dataset = (Dataset<E>) FakeDatasets.lsun();
        KNNGraphBuilder knn = new KNNGraphBuilder();
        int k = 2;
        int maxPartitionSize = 5;
        Graph g = new AdjListGraph();
        Props props = new Props();
        Bisection bisection = new FiducciaMattheyses(20);
        g.ensureCapacity(dataset.size());
        g = knn.getNeighborGraph(dataset, g, k);

        Partitioning partitioning = new RecursiveBisection(bisection);
        ArrayList<ArrayList<Node>> partitioningResult = partitioning.partition(maxPartitionSize, g, props);

        merger = new KnnMerger();
        merger.setDistanceMeasure(EuclideanDistance.getInstance());
        merger.setMergeEvaluation(new BBK1());
        merger.initialize(partitioningResult, g, bisection, props);

        Props pref = new Props();
        HierarchicalResult result = merger.getHierarchy(dataset, pref);
        Clustering<E, Cluster<E>> clust = result.getClustering();

        int sum = 0;
        for (Cluster c : clust) {
            if (c != null) {
                sum += c.size();
            }
        }
        LOG.info("instances = " + sum);
        // assertEquals(dataset.size(), clust.instancesCount());
        DendroTreeData tree = result.getTreeData();
        assertEquals(dataset.size(), tree.getMapping().length);
        //LOG.debug("tree: ");
        //tree.print();
        Clustering<E, Cluster<E>> c = result.getClustering();
        assertEquals(dataset.size(), c.instancesCount());
        //we don't want empty clusters
        for (Cluster<E> a : c) {
            Assert.assertNotEquals(0, a.size());
        }
    }

}
