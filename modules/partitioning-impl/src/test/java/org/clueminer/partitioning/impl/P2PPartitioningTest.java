package org.clueminer.partitioning.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.chameleon.KNN;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Node;
import org.clueminer.io.FileHandler;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Tomas Bruna
 */
public class P2PPartitioningTest {


    private Dataset<? extends Instance> twoDistinctNeighbors() {
        Dataset<Instance> data = new ArrayDataset<>(4, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        data.builder().create(new double[]{1, 1});
        data.builder().create(new double[]{3, 3});
        data.builder().create(new double[]{1, 2});
        data.builder().create(new double[]{2, 1});
        data.builder().create(new double[]{4, 4});
        data.builder().create(new double[]{6, 6});
        data.builder().create(new double[]{5, 6});
        data.builder().create(new double[]{6, 5});
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



    
  /*  @Test
    public void twoDistinctNeighborTest() {
        Dataset<? extends Instance> dataset = twoDistinctNeighbors();
        DistanceMeasure dm = new EuclideanDistance();
        KNN knn = new KNN(3);
        int[][] a = knn.getNeighborArray(dataset);

        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);
       // System.out.println(g.graphVizExport());
        P2PPartitioning p = new P2PPartitioning();
        p.partition(2, g);
        //System.out.println(g.graphVizExport());
        p.removeUnusedEdges();
        assertEquals(g.graphVizExport(),"Graph G {\n    0[fontsize=11 pos=\"1.0,1.0!\" width=0.1 height=0.1 shape=point];\n    1[fontsize=11 pos=\"3.0,3.0!\" width=0.1 height=0.1 shape=point];\n    2[fontsize=11 pos=\"1.0,2.0!\" width=0.1 height=0.1 shape=point];\n    3[fontsize=11 pos=\"2.0,1.0!\" width=0.1 height=0.1 shape=point];\n    4[fontsize=11 pos=\"4.0,4.0!\" width=0.1 height=0.1 shape=point];\n    5[fontsize=11 pos=\"6.0,6.0!\" width=0.1 height=0.1 shape=point];\n    6[fontsize=11 pos=\"5.0,6.0!\" width=0.1 height=0.1 shape=point];\n    7[fontsize=11 pos=\"6.0,5.0!\" width=0.1 height=0.1 shape=point];\n    0 -- 1;\n    2 -- 0;\n    3 -- 0;\n    2 -- 1;\n    3 -- 1;\n    3 -- 2;\n    5 -- 4;\n    6 -- 4;\n    7 -- 4;\n    6 -- 5;\n    7 -- 5;\n    7 -- 6;\n}\n");
    }*/
    
   /* @Test
    public void threeDistinctNeighborTest() {
        Dataset<? extends Instance> dataset = threeDistinctNeighbors();
        DistanceMeasure dm = new EuclideanDistance();
        KNN knn = new KNN(2);

        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);
        System.out.println(g.graphVizExport());
        P2PPartitioning p = new P2PPartitioning();
        p.partition(3, g);
        System.out.println(g.graphVizExport());
        p.removeUnusedEdges();
        assertEquals(g.graphVizExport(),"Graph G {\n    0[fontsize=11 pos=\"1.0,6.0!\" width=0.1 height=0.1 shape=point];\n    1[fontsize=11 pos=\"1.0,5.0!\" width=0.1 height=0.1 shape=point];\n    2[fontsize=11 pos=\"2.0,4.0!\" width=0.1 height=0.1 shape=point];\n    3[fontsize=11 pos=\"1.0,1.0!\" width=0.1 height=0.1 shape=point];\n    4[fontsize=11 pos=\"2.0,0.5!\" width=0.1 height=0.1 shape=point];\n    5[fontsize=11 pos=\"2.2,2.5!\" width=0.1 height=0.1 shape=point];\n    6[fontsize=11 pos=\"3.5,4.0!\" width=0.1 height=0.1 shape=point];\n    7[fontsize=11 pos=\"4.0,5.0!\" width=0.1 height=0.1 shape=point];\n    8[fontsize=11 pos=\"4.3,4.2!\" width=0.1 height=0.1 shape=point];\n    9[fontsize=11 pos=\"6.0,7.0!\" width=0.1 height=0.1 shape=point];\n    1 -- 0;\n    0 -- 2;\n    2 -- 1;\n    4 -- 3;\n    5 -- 3;\n    4 -- 5;\n    7 -- 6;\n    8 -- 6;\n    8 -- 7;\n    9 -- 7;\n    9 -- 8;\n}\n");
    }*/
    




    @Test
    public void irisDataTest() throws IOException {
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
        System.out.println(g.graphVizExport());
        P2PPartitioning p = new P2PPartitioning();
        p.partition(15,g);
        p.removeUnusedEdges();
        System.out.println(g.graphVizExport());

    }


    


}
