package org.clueminer.chameleon;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.io.FileHandler;
import org.clueminer.partitioning.impl.KernighanLin;
import org.clueminer.partitioning.impl.KernighanLinRecursive;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Tomas Bruna
 */
public class MergerTest {

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

    private Dataset<? extends Instance> fourClusters() {
        Dataset<Instance> data = new ArrayDataset<>(4, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        data.builder().create(new double[]{0, 0});
        data.builder().create(new double[]{1, 1});
        data.builder().create(new double[]{2, 0});
        data.builder().create(new double[]{0, 1});
        data.builder().create(new double[]{0, 3});
        data.builder().create(new double[]{0, 4});
        data.builder().create(new double[]{1, 4});
        data.builder().create(new double[]{1, 3});
        data.builder().create(new double[]{3, 2.5});
        data.builder().create(new double[]{3, 1.5});
        data.builder().create(new double[]{5, 1});
        data.builder().create(new double[]{4, 0});
        data.builder().create(new double[]{4, 4});
        data.builder().create(new double[]{5, 4});
        data.builder().create(new double[]{4, 5});
        data.builder().create(new double[]{5, 5});
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

        KernighanLin kl = new KernighanLin(g);
        ArrayList<LinkedList<Node>> result = kl.bisect();
        Merger m = new Merger(g);
        m.merge(result);

        //Assert external interconnectivity
        assertEquals(m.getEIC(1, 0), 1 / (sqrt(2)) + 1 / (sqrt(10)) + 1 / (sqrt(5)) + 1 / (sqrt(8)) + 1 / (sqrt(5)), 0.0001);
        //Assert external closeness
        assertEquals(m.getECL(0, 1), (1 / (sqrt(2)) + 1 / (sqrt(10)) + 1 / (sqrt(5)) + 1 / (sqrt(8)) + 1 / (sqrt(5))) / 5, 0.0001);
    }

    /*   @Test
     public void fourClustersTest() throws UnsupportedEncodingException, IOException, FileNotFoundException, InterruptedException {
     Dataset<? extends Instance> dataset = fourClusters();
     DistanceMeasure dm = new EuclideanDistance();
     KNN knn = new KNN(3);
     int[][] a = knn.getNeighborArray(dataset);

     AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
     g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);

     GraphPrinter gp = new GraphPrinter();
     gp.printGraph(g, 1, "/home/tomas/Desktop", "knn2.png");
     KernighanLinRecursive klr = new KernighanLinRecursive();
     ArrayList<LinkedList<Node>> result = klr.partition(4, g);

     gp.printClusters(g, 1, result, "/home/tomas/Desktop", "partitionedClusters2OLD.png");
     AdjMatrixGraph resultGraph = (AdjMatrixGraph) klr.removeUnusedEdges();
     gp.printGraph(resultGraph, 1, "/home/tomas/Desktop", "paritioned.png");

     Merger m = new Merger(g);
     m.merge(result);

     ArrayList<LinkedList<Node>> result2 = m.getNewClusters();
     gp.printClusters(g, 1, result2, "/home/tomas/Desktop", "aaa.png");

     assertEquals(m.getEIC(1, 0), 1 / sqrt(1 + 1.5 * 1.5), 0.00001);
     assertEquals(m.getEIC(2, 0), 0, 0.00001);
     assertEquals(m.getEIC(3, 0), 0, 0.00001);
     assertEquals(m.getEIC(1, 2), 1 / sqrt(4 + 0.5 * 0.5), 0.00001);
     assertEquals(m.getEIC(1, 3), 1 / 2.0 + 1 / sqrt(1 + 1.5 * 1.5), 0.00001);
     assertEquals(m.getECL(1, 3), (1 / 2.0 + 1 / sqrt(1 + 1.5 * 1.5)) / 2, 0.00001);
     assertEquals(m.getEIC(2, 3), 1 / 2.0, 0.00001);
     } */
    protected void printClusters(Graph g, ArrayList<LinkedList<Node>> result) {
        for (LinkedList<Node> cluster : result) {
            for (Node node : cluster) {
                System.out.print(" ," + g.getIndex(node));
            }
            System.out.println("");
        }
    }

    @Test
    public void test() throws IOException, UnsupportedEncodingException, FileNotFoundException, InterruptedException {
        Dataset<? extends Instance> dataset = fourClusters();
        DistanceMeasure dm = new EuclideanDistance();
        KNN knn = new KNN(3);
        int[][] a = knn.getNeighborArray(dataset);

        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);

        //printGraph(g.graphVizExport(1), "/home/tomas/Desktop", "knn.png");
        KernighanLinRecursive klr = new KernighanLinRecursive();
        ArrayList<LinkedList<Node>> result = klr.partition(4, g);

        AdjMatrixGraph resultGraph = (AdjMatrixGraph) klr.removeUnusedEdges();
        //printGraph(resultGraph.graphVizExport(1), "/home/tomas/Desktop", "paritioned.png");

        Merger m = new Merger(g);
        ArrayList<LinkedList<Node>> r = m.merge(result);
        // m.printExternalProperties();
        int nodeToCluster[] = m.getNodeToCluster();
        GraphPrinter gp = new GraphPrinter();
        gp.printClusters(g, 1, nodeToCluster, r.size(), "/home/tomas/Desktop", "simple.png");
    }

    @Test
    public void irisDataTest() throws IOException, FileNotFoundException, UnsupportedEncodingException, InterruptedException {
        CommonFixture tf = new CommonFixture();
        Dataset data = new SampleDataset();
        DistanceMeasure distanceMeasure = new EuclideanDistance();
        data.attributeBuilder().create("sepal length", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("sepal width", BasicAttrType.NUMERICAL);
        FileHandler.loadDataset(tf.irisData(), data, 2, ",");

        KNN knn = new KNN(5);

        AdjMatrixGraph g = new AdjMatrixGraph(data.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(data, g);

        GraphPrinter gp = new GraphPrinter();
        gp.printGraph(g, 5, "/home/tomas/Desktop", "knngraph.png");

        KernighanLinRecursive klr = new KernighanLinRecursive();
        ArrayList<LinkedList<Node>> result = klr.partition(10, g);

        gp.printClusters(g, 5, result, "/home/tomas/Desktop", "partitionedClusters.png");
        AdjMatrixGraph resultGraph = (AdjMatrixGraph) klr.removeUnusedEdges();
        gp.printGraph(resultGraph, 5, "/home/tomas/Desktop", "partitioned.png");

        Merger m = new Merger(g);
        ArrayList<LinkedList<Node>> r = m.merge(result);
        r = m.merge(r);
        m.printExternalProperties();

        System.out.println(r.size());
        gp.printClusters(g, 5, r, "/home/tomas/Desktop", "clusters.png");

    }
}
