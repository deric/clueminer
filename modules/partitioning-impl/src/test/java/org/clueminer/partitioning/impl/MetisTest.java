package org.clueminer.partitioning.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.graph.GraphBuilder.KNNGraphBuilder;
import org.clueminer.graph.adjacencyList.AdjListGraph;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.junit.Test;

/**
 *
 * @author Tomas Bruna
 */
public class MetisTest extends PartitioningTest {

    private final Metis subject;

    public MetisTest() {
        subject = new Metis();
    }

    @Test
    public void simpleGraphTest() {
        Dataset<? extends Instance> dataset = twoDistinctNeighbors();
        KNNGraphBuilder knn = new KNNGraphBuilder();
        Graph g = new AdjMatrixGraph(dataset.size());
        g = knn.getNeighborGraph(dataset, g, 4);
        subject.setPtype("rb");
        ArrayList<LinkedList<Node>> res = subject.partition(2, g);
        //the result is randomized typically the size should be 4 or 8
        assertEquals(true, res.size() > 3);
    }

    @Test
    public void irisTest() {
        KNNGraphBuilder knn = new KNNGraphBuilder();
        Dataset dataset = FakeDatasets.irisDataset();
        Graph g = new AdjListGraph(dataset.size());
        g = knn.getNeighborGraph(dataset, g, 20);
        subject.setPtype("rb");
        ArrayList<LinkedList<Node>> res = subject.partition(10, g);
        assertNotNull(res);
        assertEquals(true, res.size() > 10);
    }
}
