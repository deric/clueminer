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
import org.clueminer.dataset.api.Instance;
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
public class RecursiveBisectionTest<E extends Instance> extends PartitioningTest<E> {

    private Dataset<E> iris;

    public RecursiveBisectionTest() {
        iris = (Dataset<E>) FakeDatasets.irisDataset();
    }

    @Test
    public void irisDataTest() throws IOException, FileNotFoundException, UnsupportedEncodingException, InterruptedException {
        KNNGraphBuilder knn = new KNNGraphBuilder();

        AdjMatrixGraph g = new AdjMatrixGraph(iris.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(iris, g, 5);
        RecursiveBisection rb = new RecursiveBisection(new FiducciaMattheyses());

        ArrayList<ArrayList<Node>> result = rb.partition(5, g, new Props());
        assertEquals(36, result.size());
        assertEquals(510, g.getEdgeCount());
    }

    //@Test
    public void irisDataTestFast() throws IOException, FileNotFoundException, UnsupportedEncodingException, InterruptedException {
        System.out.println("============");
        KNNGraphBuilder knn = new KNNGraphBuilder();

        /** TODO: seems to end up in an infinite cycle */
        Graph g = new FastGraph(iris.size());
        g = knn.getNeighborGraph(iris, g, 5);
        RecursiveBisection rb = new RecursiveBisection(new FiducciaMattheyses());

        ArrayList<ArrayList<Node>> result = rb.partition(5, g, new Props());
        System.out.println("partitions:" + result.size());
        //assertEquals(36, result.size());
        assertEquals(510, g.getEdgeCount());
    }

}
