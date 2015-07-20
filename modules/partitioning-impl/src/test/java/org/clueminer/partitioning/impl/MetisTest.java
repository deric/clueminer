package org.clueminer.partitioning.impl;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.graph.GraphBuilder.KNNGraphBuilder;
import org.clueminer.graph.adjacencyList.AdjListGraph;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Graph;

/**
 *
 * @author Tomas Bruna
 */
public class MetisTest extends PartitioningTest {

    //@Test
    public void simpleGraphTest() {
        Dataset<? extends Instance> dataset = twoDistinctNeighbors();
        DistanceMeasure dm = new EuclideanDistance();
        KNNGraphBuilder knn = new KNNGraphBuilder();
        Graph g = new AdjMatrixGraph(dataset.size());
        g = knn.getNeighborGraph(dataset, g, 4);
        Metis m = new Metis();
        m.setPtype("rb");
        m.partition(2, g);
    }

    // @Test
    public void irisTest() {
        KNNGraphBuilder knn = new KNNGraphBuilder();
        Dataset dataset = FakeDatasets.irisDataset();
        Graph g = new AdjListGraph(dataset.size());
        g = knn.getNeighborGraph(dataset, g, 20);
        Metis m = new Metis();
        m.setPtype("rb");
        m.partition(10, g);
    }
}
