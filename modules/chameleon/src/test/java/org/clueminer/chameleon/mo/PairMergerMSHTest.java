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
package org.clueminer.chameleon.mo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.clueminer.chameleon.similarity.Closeness;
import org.clueminer.chameleon.similarity.Interconnectivity;
import org.clueminer.chameleon.similarity.ShatovskaSimilarity;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.MergeEvaluation;
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
public class PairMergerMSHTest<E extends Instance, C extends Cluster<E>, P extends MoPair<E, C>> extends AbstractQueueTest<E, C, P> {

    private FrontHeapQueueMsh queue;
    private PairMergerMSH subject;

    @Test
    public void testPairsRemoval() {
        Dataset<? extends Instance> dataset = FakeDatasets.usArrestData();
        KNNGraphBuilder knn = new KNNGraphBuilder();
        int k = 3;
        Props props = new Props();
        int maxPartitionSize = 20;
        Graph g = new AdjMatrixGraph();
        Bisection bisection = new FiducciaMattheyses(20);
        g.ensureCapacity(dataset.size());
        g = knn.getNeighborGraph(dataset, g, k);

        Partitioning partitioning = new RecursiveBisection(bisection);
        ArrayList<ArrayList<Node>> partitioningResult = partitioning.partition(maxPartitionSize, g, props);

        List<MergeEvaluation> objectives = new LinkedList<>();
        objectives.add(new Closeness());
        objectives.add(new Interconnectivity());

        PairMergerMOH merger = new PairMergerMOH();
        merger.initialize(partitioningResult, g, bisection, props);
        merger.setObjectives(objectives);
        merger.setSortEvaluation(new ShatovskaSimilarity());

        ArrayList<MoPair> pairs = merger.createPairs(partitioningResult.size(), props);
        HashSet<Integer> blacklist = new HashSet<>();
        queue = new FrontHeapQueueMsh(5, blacklist, objectives, props);
        queue.buffer.addAll(pairs);

        //for (MoPair<Instance, GraphCluster<Instance>> p : pairs) {
        //    queue.blacklist.insertIntoFront(p.A.getClusterId());
        //}
        queue.blacklist.add(1);
        queue.blacklist.add(2);
        queue.rebuildQueue();
        assertEquals(0, queue.buffer.size());
    }

    @Test
    public void testIris() {
        Props props = new Props();
        subject = initializeMerger((Dataset<E>) FakeDatasets.irisDataset(), new PairMergerMSH());
        ArrayList<P> pairs = subject.createPairs(subject.getClusters().size(), props);
        HashSet<Integer> blacklist = new HashSet<>();
        subject.queue = new FrontHeapQueueMsh<>(5, blacklist, subject.objectives, props);
        subject.queue.addAll(pairs);

        //merge some items - just enough to overflow queue to buffer
        for (int i = 0; i < 5; i++) {
            subject.singleMerge(subject.queue.poll(), props);
        }
        //make sure we iterate over all items
        int i = 0;
        for (Object p : subject.queue) {
            assertNotNull(p);
            i++;
        }
        assertEquals(i, subject.queue.size());
    }

}
