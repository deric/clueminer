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
package org.clueminer.partitioning.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.fast.FastGraph;
import org.clueminer.graph.knn.KNNGraphBuilder;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Tomas Bruna
 */
public class RecursiveBisectionTest extends PartitioningTest {

    @Test
    public void irisDataTest() throws IOException, FileNotFoundException, UnsupportedEncodingException, InterruptedException {
        KNNGraphBuilder knn = new KNNGraphBuilder();

        Dataset dataset = FakeDatasets.irisDataset();

        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g, 5);
        RecursiveBisection rb = new RecursiveBisection(new FiducciaMattheyses());

        ArrayList<ArrayList<Node>> result = rb.partition(5, g, new Props());
        assertEquals(36, result.size());
        assertEquals(510, g.getEdgeCount());
    }

    @Test
    public void irisDataTestFast() throws IOException, FileNotFoundException, UnsupportedEncodingException, InterruptedException {
        System.out.println("============");
        KNNGraphBuilder knn = new KNNGraphBuilder();

        Dataset dataset = FakeDatasets.irisDataset();

        Graph g = new FastGraph(dataset.size());
        g = knn.getNeighborGraph(dataset, g, 5);
        RecursiveBisection rb = new RecursiveBisection(new FiducciaMattheyses());

        ArrayList<ArrayList<Node>> result = rb.partition(5, g, new Props());
        //System.out.println("partitions:" + result);
        //assertEquals(36, result.size());
    }

}
