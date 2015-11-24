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
package org.clueminer.chameleon.mo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.clueminer.chameleon.similarity.RiRcSimilarity;
import org.clueminer.chameleon.similarity.ShatovskaSimilarity;
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
public class FrontQueueTest {

    private FrontQueue subject;
    private static final double delta = 1e-9;

    public FrontQueueTest() {
    }

    @Test
    public void testSorting() {
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
        objectives.add(new RiRcSimilarity());
        objectives.add(new ShatovskaSimilarity());

        PairMergerMOF merger = new PairMergerMOF();
        merger.initialize(partitioningResult, g, bisection, props);
        merger.setObjectives(objectives);

        ArrayList<MoPair> pairs = merger.createPairs(partitioningResult.size(), props);

        FrontQueue queue = new FrontQueue(pairs, objectives, props);
        System.out.println(queue);
        //System.out.println(queue.stats());
        //there are 21 pairs of clusters
        assertEquals(21, queue.size());
        //we should have 5 fronts
        //assertEquals(5, queue.numFronts());
        //TODO: make sure we can remove and add items to queue in fast manner
        int n = 21, cmp;
        MoPair item, prev = null;
        DominanceComparator comparator = new DominanceComparator(objectives);
        for (int i = 0; i < n; i++) {
            assertEquals(n - i, queue.size());
            item = queue.poll();
            if (i > 0) {
                cmp = comparator.compare(prev, item);
                //System.out.println("cmp = " + cmp);
                //prev should be better or at same level
                //assertEquals(true, cmp <= 0);
            }
            //System.out.println(i + ": " + item.A.toString() + "\n    " + item.B.toString() + "\n " + item.getValue() + item);
            assertNotNull(item);
            prev = item;
        }
        assertEquals(0, queue.size());
    }

}
