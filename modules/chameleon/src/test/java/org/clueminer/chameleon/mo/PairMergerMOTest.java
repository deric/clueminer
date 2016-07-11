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
import org.clueminer.chameleon.GraphCluster;
import org.clueminer.chameleon.similarity.Closeness;
import org.clueminer.chameleon.similarity.Interconnectivity;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
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
public class PairMergerMOTest<E extends Instance, C extends GraphCluster<E>, P extends MoPair<E, C>> extends AbstractQueueTest<E, C, P> {

    private PairMergerMO<E, C, P> subject;

    @Test
    public void testUsArrest() {
        Dataset<E> dataset = (Dataset<E>) FakeDatasets.usArrestData();
        KNNGraphBuilder knn = new KNNGraphBuilder();
        int k = 3;
        int maxPartitionSize = 20;
        Props pref = new Props();
        Graph g = new AdjMatrixGraph();
        Bisection bisection = new FiducciaMattheyses(20);
        g.ensureCapacity(dataset.size());
        g = knn.getNeighborGraph(dataset, g, k);

        Partitioning partitioning = new RecursiveBisection(bisection);
        ArrayList<ArrayList<Node<E>>> partitioningResult = partitioning.partition(maxPartitionSize, g, pref);

        subject = new PairMergerMO();
        subject.addObjective(new Closeness());
        subject.addObjective(new Interconnectivity());

        subject.initialize(partitioningResult, g, bisection, pref);

        HierarchicalResult result = subject.getHierarchy(dataset, pref);
        DendroTreeData tree = result.getTreeData();
        tree.print();
    }

    @Test
    public void testIris() {
        Props props = new Props();
        subject = initializeMerger((Dataset<E>) FakeDatasets.irisDataset(), new PairMergerMO());
        ArrayList<P> pairs = subject.createPairs(subject.getClusters().size(), props);
        HashSet<Integer> blacklist = new HashSet<>();
        subject.queue = new FhQueue(5, blacklist, subject.objectives, props);
        subject.queue.addAll(pairs);

        //merge some items - just enough to overflow queue to buffer
        for (int i = 0; i < 5; i++) {
            subject.singleMerge(subject.queue.poll(), props, 0);
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
