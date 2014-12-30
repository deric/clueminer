/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clueminer.chameleon;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.clueminer.colors.ColorBrewer;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;

/**
 *
 * @author tomas
 */
public class GraphPrinter {

    /**
     * Generates image of the graph
     *
     * @param scale Scale of the graph
     * @param graph Graph to export
     * @param path Path to output folder
     * @param output Output file name
     *
     * @throws java.io.UnsupportedEncodingException
     * @throws java.io.FileNotFoundException
     * @throws java.lang.InterruptedException
     */
    public void printGraph(Graph graph, double scale, String path, String output) throws UnsupportedEncodingException, IOException, FileNotFoundException, InterruptedException {
        generateImage(graphVizExport(scale, graph), path, output);
    }

    /**
     * Generates image of graph's clusters
     *
     * @param scale Scale of the graph
     * @param graph Graph to export
     * @param nodeToCluster Node to cluster assignment
     * @param path Path to output folder
     * @param output Output file name
     *
     * @throws java.io.UnsupportedEncodingException
     * @throws java.io.FileNotFoundException
     * @throws java.lang.InterruptedException
     */
    public void printClusters(Graph graph, double scale, int nodeToCluster[], String path, String output) throws UnsupportedEncodingException, IOException, FileNotFoundException, InterruptedException {
        generateImage(graphVizClusterExport(scale, graph, nodeToCluster), path, output);
    }

    /**
     * Exports graph to Graphviz * To build graph run "neato -Tpng -o out.png
     * -Gmode=KK input" where input is the output of this function
     *
     * @param scale Scale of the graph
     * @param graph Graph to export
     *
     * @return Graphviz source code describing this graph
     */
    private String graphVizExport(double scale, Graph graph) {
        String result = "Graph G {\n";
        result += exportNodes(scale, graph);
        result += exportEdges(graph);
        result += "}\n";
        return result;
    }

    private String exportNodes(double scale, Graph graph) {
        String result = "";
        ArrayList<Node> nodes = (ArrayList<Node>) graph.getNodes().toCollection();
        for (Node node : nodes) {
            result += "    " + graph.getIndex(node) + "[fontsize=11 pos=\"" + node.getInstance().get(0) * scale + ","
                    + node.getInstance().get(1) * scale + "!\" width=0.1 height=0.1 shape=point]\n";
        }
        return result;
    }

    private String exportEdges(Graph graph) {
        String result = "";
        ArrayList<Edge> edges = (ArrayList<Edge>) graph.getEdges().toCollection();
        for (Edge edge : edges) {
            result += "    " + graph.getIndex(edge.getSource()) + " -- " + graph.getIndex(edge.getTarget()) + ";\n";
        }
        return result;
    }

    /**
     * Exports clusters to Graphviz
     *
     * @param scale Scale of the graph
     * @param graph Graph to export
     * @param nodeToCluster Node to cluster assignment
     *
     * @return Graphviz source code describing graph's clusters
     */
    private String graphVizClusterExport(double scale, Graph graph, int nodeToCluster[]) {
        String result = "Graph G {\n";
        Color[] scheme = generateColorScheme(nodeToCluster.length);
        result += exportNodes(scale, graph, nodeToCluster, scheme);
        result += "}\n";
        return result;
    }

    /**
     * Generates colors for clusters
     *
     * @param size Number of colors to generate
     * @return Array of colors
     */
    private Color[] generateColorScheme(int size) {
        ColorGenerator gen = new ColorBrewer();
        Color[] scheme = new Color[size];
        for (int i = 0; i < size; i++) {
            scheme[i] = gen.next();
        }
        return scheme;
    }

    private String exportNodes(double scale, Graph graph, int nodeToCluster[], Color[] scheme) {
        String result = "";
        ArrayList<Node> nodes = (ArrayList<Node>) graph.getNodes().toCollection();
        for (Node node : nodes) {
            result += "    " + graph.getIndex(node) + "[fontsize=11 pos=\"" + node.getInstance().get(0) * scale + ","
                    + node.getInstance().get(1) * scale + "!\" width=0.1 height=0.1 shape=point color=\"#"
                    + String.format("%02x%02x%02x", scheme[nodeToCluster[graph.getIndex(node)]].getRed(),
                            scheme[nodeToCluster[graph.getIndex(node)]].getGreen(),
                            scheme[nodeToCluster[graph.getIndex(node)]].getBlue())
                    + "\"]\n";
        }
        return result;
    }

    /**
     * Prints graph into png file
     *
     * @param graph Graph source code
     * @param path Path to output folder
     * @param output Output file name
     *
     * @throws java.io.FileNotFoundException
     * @throws java.io.UnsupportedEncodingException
     * @throws java.lang.InterruptedException
     */
    private void generateImage(String graph, String path, String output) throws FileNotFoundException, UnsupportedEncodingException, IOException, InterruptedException {
        try (PrintWriter writer = new PrintWriter(path + "/" + "tempfile", "UTF-8")) {
            writer.print(graph);
            writer.close();
            Process p = Runtime.getRuntime().exec("neato -Tpng -o " + path + "/" + output + " -Gmode=KK " + path + "/" + "tempfile");
            p.waitFor();
            File file = new File(path + "/" + "tempfile");
            file.delete();
        }
    }
}
