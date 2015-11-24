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
package edu.umn.metis;

import java.io.IOException;
import java.util.ArrayList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.graph.adjacencyList.AdjListGraph;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.knn.KNNGraphBuilder;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class HMetisTest extends PartitioningTest {

    private static HMetis subject;

    public HMetisTest() {
        subject = new HMetis();
    }

    @Test
    public void simpleGraphTest() throws IOException, InterruptedException {
        //skip test when binary is not found (e.g. on Travis)
        assertTrue("binary does not exists", subject.getBinary().exists());
        if (subject.getBinary().exists()) {
            Dataset<? extends Instance> dataset = twoDistinctNeighbors();
            KNNGraphBuilder knn = new KNNGraphBuilder();
            Graph g = new AdjMatrixGraph(dataset.size());
            g = knn.getNeighborGraph(dataset, g, 4);
            ArrayList<ArrayList<Node>> res = subject.partition(2, g, new Props());
            //doesn't work on Travis
            //assertEquals(4, res.size());
            assertNotNull(res);
        }
    }

    @Test
    public void irisTest() throws IOException, InterruptedException {
        //skip test when binary is not found (e.g. on Travis)
        if (subject.getBinary().exists()) {
            KNNGraphBuilder knn = new KNNGraphBuilder();
            Dataset dataset = FakeDatasets.irisDataset();
            Graph g = new AdjListGraph(dataset.size());
            g = knn.getNeighborGraph(dataset, g, 20);
            ArrayList<ArrayList<Node>> res = subject.partition(10, g, new Props());
            assertNotNull(res);
            //the result is randomized - we can't be sure to get exactly
            //the same number of partitions as requested
            assertEquals(true, res.size() >= 10);
        }
    }

}
