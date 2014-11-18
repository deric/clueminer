package org.clueminer.partitioning.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.chameleon.KNN;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Node;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Tomas Bruna
 */
public class KernighanLinTest {

    private Dataset<? extends Instance> twoDistinctNeighbors() {
        Dataset<Instance> data = new ArrayDataset<>(4, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        data.builder().create(new double[]{1, 1});
        data.builder().create(new double[]{6, 6});
        data.builder().create(new double[]{6, 5});
        data.builder().create(new double[]{2, 2});
        data.builder().create(new double[]{5, 5});
        data.builder().create(new double[]{5, 6});
        data.builder().create(new double[]{2, 1});
        data.builder().create(new double[]{1, 2});
        return data;
    }

    private Dataset<? extends Instance> threeDistinctNeighbors() {
        Dataset<Instance> data = new ArrayDataset<>(4, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        data.builder().create(new double[]{1, 6});
        data.builder().create(new double[]{1, 5});
        data.builder().create(new double[]{2, 4});
        data.builder().create(new double[]{1, 1});
        data.builder().create(new double[]{2, 0.5});
        data.builder().create(new double[]{2.2, 2.5});
        data.builder().create(new double[]{3.5, 4});
        data.builder().create(new double[]{4, 5});
        data.builder().create(new double[]{4.3, 4.2});
        data.builder().create(new double[]{6, 7});
        return data;
    }

    private Dataset<? extends Instance> twoDistinctNeighbors2() {
        Dataset<Instance> data = new ArrayDataset<>(4, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        data.builder().create(new double[]{1, 1});
        data.builder().create(new double[]{6, 6});
        data.builder().create(new double[]{6, 5});
        data.builder().create(new double[]{3, 3});
        data.builder().create(new double[]{4, 4});
        data.builder().create(new double[]{5, 6});
        data.builder().create(new double[]{2, 1});
        data.builder().create(new double[]{1, 2});
        data.builder().create(new double[]{4, 5});
        data.builder().create(new double[]{5, 4});
        data.builder().create(new double[]{0, 1});
        data.builder().create(new double[]{1, 0});
        data.builder().create(new double[]{1, 1.3});
        data.builder().create(new double[]{1.3, 1});
        data.builder().create(new double[]{0.3, 0.2});
        data.builder().create(new double[]{0.1, 0.1});
        data.builder().create(new double[]{9, 0.1});

        data.builder().create(new double[]{9, 9});
        data.builder().create(new double[]{8, 9});
        return data;
    }

 /*   @Test
    public void twoDistinctTest() {
        Dataset<? extends Instance> dataset = twoDistinctNeighbors();
        DistanceMeasure dm = new EuclideanDistance();
        KNN knn = new KNN(3);
        int[][] a = knn.getNeighborArray(dataset);

        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);
        System.out.println(g.graphVizExport());
        KernighanLin kl = new KernighanLin(g);
        kl.partition();
        kl.removeUnusedEdges();
        System.out.println(g.graphVizExport());
    }

    @Test
    public void threeDistinctTest() {
        Dataset<? extends Instance> dataset = threeDistinctNeighbors();
        DistanceMeasure dm = new EuclideanDistance();
        KNN knn = new KNN(2);

        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);
        System.out.println(g.graphVizExport());
        KernighanLin kl = new KernighanLin(g);
        kl.partition();
        kl.removeUnusedEdges();
        kl.printClusters();
        System.out.println(g.graphVizExport());
    } */

    @Test
    public void twoDistinct2Test() {
        Dataset<? extends Instance> dataset = twoDistinctNeighbors2();
        DistanceMeasure dm = new EuclideanDistance();
        KNN knn = new KNN(4);
        int[][] a = knn.getNeighborArray(dataset);

        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);
        System.out.println(g.graphVizExport());
        KernighanLin kl = new KernighanLin();
        ArrayList<LinkedList<Node>> result = kl.bisect(g);
        //printResult(result);
        kl.removeUnusedEdges();
        kl.printClusters();
        System.out.println(g.graphVizExport());

    }
    
        private void printResult(ArrayList<LinkedList<Node>> result) {
        for (int i = 0;i<result.size();i++) {
            System.out.print("Cluster " + i + ": ");
            for (Node node : result.get(i)) {
                System.out.print(node.getId() + ", ");
            }
            System.out.println("");
        }
    }
}
