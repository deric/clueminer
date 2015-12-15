package edu.umn.metis;

import java.util.ArrayList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.graph.adjacencyList.AdjListGraph;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.knn.KNNGraphBuilder;
import org.clueminer.utils.Props;
import org.clueminer.utils.SystemInfo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        assertNotNull(g);
        if (SystemInfo.isLinux()) {
            ArrayList<ArrayList<Node>> res = subject.partition(2, g, new Props());
            //the result is randomized typically the size should be 4 or 8
            assertEquals(true, res.size() > 3);
        }
    }

    @Test
    public void irisTest() {
        KNNGraphBuilder knn = new KNNGraphBuilder();
        Dataset dataset = FakeDatasets.irisDataset();
        Graph g = new AdjListGraph(dataset.size());
        g = knn.getNeighborGraph(dataset, g, 20);
        subject.setPtype("rb");
        assertNotNull(g);
        if (SystemInfo.isLinux()) {
            ArrayList<ArrayList<Node>> res = subject.partition(10, g, new Props());
            assertNotNull(res);
            assertEquals(true, res.size() > 10);
        }
    }
}
