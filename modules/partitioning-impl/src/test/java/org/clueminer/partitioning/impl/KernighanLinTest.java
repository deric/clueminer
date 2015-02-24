//package org.clueminer.partitioning.impl;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.LinkedList;
//import org.clueminer.chameleon.GraphPrinter;
//import org.clueminer.chameleon.KNN;
//import org.clueminer.dataset.api.Dataset;
//import org.clueminer.dataset.api.Instance;
//import org.clueminer.distance.EuclideanDistance;
//import org.clueminer.distance.api.DistanceMeasure;
//import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
//import org.clueminer.graph.api.Node;
//import org.junit.Test;
//
///**
// *
// * @author Tomas Bruna
// */
//public class KernighanLinTest extends PartitioningTest {
//
//    @Test
//    public void twoDistinctTest() {
//        Dataset<? extends Instance> dataset = twoDistinctNeighbors();
//        DistanceMeasure dm = new EuclideanDistance();
//        KNN knn = new KNN(3);
//        int[][] a = knn.getNeighborArray(dataset);
//
//        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
//        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);
//
//        KernighanLin kl = new KernighanLin();
//        kl.bisect(g);
//        kl.removeUnusedEdges();
//
//    }
//
//    @Test
//    public void threeDistinctTest() {
//        Dataset<? extends Instance> dataset = threeDistinctNeighbors();
//        DistanceMeasure dm = new EuclideanDistance();
//        KNN knn = new KNN(2);
//
//        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
//        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);
//
//        KernighanLin kl = new KernighanLin();
//        kl.bisect(g);
//        kl.removeUnusedEdges();
//        kl.printClusters();
//
//    }
//
//    @Test
//    public void twoDistinct2Test() throws UnsupportedEncodingException, IOException, FileNotFoundException, InterruptedException {
//        Dataset<? extends Instance> dataset = twoDistinctNeighbors2();
//        DistanceMeasure dm = new EuclideanDistance();
//        KNN knn = new KNN(4);
//        int[][] a = knn.getNeighborArray(dataset);
//
//        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
//        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);
//
//        KernighanLin kl = new KernighanLin();
//        ArrayList<LinkedList<Node>> result = kl.bisect(g);
//        //printResult(result);
//        kl.removeUnusedEdges();
//        kl.printClusters();
//
//        GraphPrinter gp = new GraphPrinter();
//        gp.printGraph(g, 1, "/home/tomas/Desktop", "output.png");
//    }
//
//}
