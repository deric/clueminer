package org.clueminer.chameleon;

import static java.lang.Math.sqrt;
import java.util.LinkedList;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.graph.GraphBuilder.KNNGraphBuilder;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.impl.FiducciaMattheyses;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Tomas Bruna
 */
public class ClusterTest {

    private Dataset<? extends Instance> simpleData() {
        Dataset<Instance> data = new ArrayDataset<>(4, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        data.builder().create(new double[]{0, 0});
        data.builder().create(new double[]{1, 3});
        data.builder().create(new double[]{2, 2});
        data.builder().create(new double[]{2, 1});
        data.builder().create(new double[]{4, 4});
        return data;
    }

    @Test
    public void testClosenessAndInterconnectivity() {
        Dataset<? extends Instance> dataset = simpleData();
        DistanceMeasure dm = new EuclideanDistance();
        KNNGraphBuilder knn = new KNNGraphBuilder();
        int[][] a = knn.getNeighborArray(dataset, 3);

        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g, 3);

        LinkedList<Node> nodes = new LinkedList<>();
        nodes.addAll(g.getNodes().toCollection());

        Partition c = new Partition(nodes, g, 1, new FiducciaMattheyses());
        // Assert internal interconnectivity
        assertEquals(c.getIIC(), 1 / (sqrt(2)) + 1 / (sqrt(10)) + 1 / (sqrt(5)) + 1 / (sqrt(8)) + 1 / (sqrt(5)), 0.0001);
        // Assert internal closeness
        assertEquals(c.getICL(), (1 / (sqrt(2)) + 1 / (sqrt(10)) + 1 / (sqrt(5)) + 1 / (sqrt(8)) + 1 / (sqrt(5))) / 5, 0.0001);

    }

    @Test
    public void testAverageCloseness() {
        Dataset<? extends Instance> dataset = simpleData();
        DistanceMeasure dm = new EuclideanDistance();
        KNNGraphBuilder knn = new KNNGraphBuilder();
        int[][] a = knn.getNeighborArray(dataset, 3);

        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g, 3);

        LinkedList<Node> nodes = new LinkedList<>();
        nodes.addAll(g.getNodes().toCollection());

        Partition c = new Partition(nodes, g, 1, new FiducciaMattheyses());
        assertEquals(0.4687162648354779, c.getACL(), 1e-9);
    }
}
