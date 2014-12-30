package org.clueminer.partitioning.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.chameleon.GraphPrinter;
import org.clueminer.chameleon.KNN;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.io.FileHandler;
import org.junit.Test;

/**
 *
 * @author Tomas Bruna
 */
public class P2PPartitioningTest extends PartitioningTest {

    /*@Test
     public void twoDistinctNeighborTest() {
     Dataset<? extends Instance> dataset = twoDistinctNeighbors();
     DistanceMeasure dm = new EuclideanDistance();
     KNN knn = new KNN(3);
     int[][] a = knn.getNeighborArray(dataset);

     AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
     g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);

     P2PPartitioning p = new P2PPartitioning();
     p.partition(2, g);

     p.removeUnusedEdges();
     }

     @Test
     public void threeDistinctNeighborTest() {
     Dataset<? extends Instance> dataset = threeDistinctNeighbors();
     DistanceMeasure dm = new EuclideanDistance();
     KNN knn = new KNN(2);

     AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
     g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);

     P2PPartitioning p = new P2PPartitioning();
     p.partition(3, g);

     p.removeUnusedEdges();
     } */
    @Test
    public void irisDataTest() throws IOException, FileNotFoundException, UnsupportedEncodingException, InterruptedException {
        CommonFixture tf = new CommonFixture();
        Dataset data = new SampleDataset();
        DistanceMeasure distanceMeasure = new EuclideanDistance();
        data.attributeBuilder().create("sepal length", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("sepal width", BasicAttrType.NUMERICAL);
        FileHandler.loadDataset(tf.irisData(), data, 2, ",");

        int k = 3;
        KNN knn = new KNN(k);

        AdjMatrixGraph g = new AdjMatrixGraph(data.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(data, g);

        P2PPartitioning p = new P2PPartitioning();
        p.partition(15, g);
        p.removeUnusedEdges();

        GraphPrinter gp = new GraphPrinter();
        gp.printGraph(g, 10, "/home/tomas/Desktop", "output.png");
    }

}
