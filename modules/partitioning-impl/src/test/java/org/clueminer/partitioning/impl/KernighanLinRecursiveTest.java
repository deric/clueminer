/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clueminer.partitioning.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.clueminer.attributes.BasicAttrType;
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
 * @author tomas
 */
public class KernighanLinRecursiveTest extends PartitioningTest {

    //Will not work correctly until distance measure for nodes is implemented
    @Test
    public void irisDataTest() throws IOException, FileNotFoundException, UnsupportedEncodingException, InterruptedException {
        CommonFixture tf = new CommonFixture();
        Dataset data = new SampleDataset();
        DistanceMeasure distanceMeasure = new EuclideanDistance();
        data.attributeBuilder().create("sepal length", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("sepal width", BasicAttrType.NUMERICAL);
        FileHandler.loadDataset(tf.irisData(), data, 2, ",");

        int k = 5;
        KNN knn = new KNN(k);

        AdjMatrixGraph g = new AdjMatrixGraph(data.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(data, g);
        //System.out.println(g.graphVizExport(10));
        KernighanLinRecursive klr = new KernighanLinRecursive();
        klr.partition(6, g);
        AdjMatrixGraph outGraph = (AdjMatrixGraph) klr.removeUnusedEdges();
        //System.out.println(outGraph.graphVizExport(10));
        printGraph(outGraph.graphVizExport(10), "/home/tomas/Desktop", "output.png");
    }

}
