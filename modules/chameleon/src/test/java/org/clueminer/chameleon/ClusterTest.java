package org.clueminer.chameleon;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static java.lang.Math.sqrt;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.partitioning.impl.KernighanLin;
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
    public void simpleDataTest() throws UnsupportedEncodingException, IOException, FileNotFoundException, InterruptedException {
        Dataset<? extends Instance> dataset = simpleData();
        DistanceMeasure dm = new EuclideanDistance();
        KNN knn = new KNN(3);
        int[][] a = knn.getNeighborArray(dataset);

        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);

        //Print knn graph
        GraphPrinter gp = new GraphPrinter();
        gp.printGraph(g, 1, "/home/tomas/Desktop", "knn.png");
        Cluster c = new Cluster(g, 1);
        c.computeProperties(new KernighanLin());

        //Assert internal interconnectivity
        assertEquals(c.getIIC(), 1 / (sqrt(2)) + 1 / (sqrt(10)) + 1 / (sqrt(5)) + 1 / (sqrt(8)) + 1 / (sqrt(5)), 0.0001);

        //Assert internal closeness
        assertEquals(c.getICL(), (1 / (sqrt(2)) + 1 / (sqrt(10)) + 1 / (sqrt(5)) + 1 / (sqrt(8)) + 1 / (sqrt(5))) / 5, 0.0001);

        // Print bisected graph
        //Bisection b = new KernighanLin(g);
        //b.bisect();
        //b.removeUnusedEdges();
        //gp.printGraph(g, 1, "/home/tomas/Desktop", "bisected.png");
    }
}
