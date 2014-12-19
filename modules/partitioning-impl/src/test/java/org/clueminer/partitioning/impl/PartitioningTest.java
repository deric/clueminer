/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clueminer.partitioning.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.graph.api.Node;

/**
 *
 * @author tomas
 */
public class PartitioningTest {

    protected Dataset<? extends Instance> twoDistinctNeighbors() {
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

    protected Dataset<? extends Instance> threeDistinctNeighbors() {
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

    protected Dataset<? extends Instance> twoDistinctNeighbors2() {
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

    protected void printGraph(String graph, String path, String output) throws FileNotFoundException, UnsupportedEncodingException, IOException, InterruptedException {
        try (PrintWriter writer = new PrintWriter(path + "/" + "tempfile", "UTF-8")) {
            writer.print(graph);
            writer.close();
            Process p = Runtime.getRuntime().exec("neato -Tpng -o " + path + "/" + output + " -Gmode=KK " + path + "/" + "tempfile");
            p.waitFor();
            File file = new File(path + "/" + "tempfile");
            file.delete();
        }
    }

    protected void printResult(ArrayList<LinkedList<Node>> result) {
        for (int i = 0; i < result.size(); i++) {
            System.out.print("Cluster " + i + ": ");
            for (Node node : result.get(i)) {
                System.out.print(node.getId() + ", ");
            }
            System.out.println("");
        }
    }

}
