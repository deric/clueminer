/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.chameleon;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.colors.ColorBrewer;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Tomas Bruna
 */
public class GraphPrinter {

    /**
     * Whether the outputs are bitmap or vector images
     *
     * @param bitmap
     */
    private boolean bitmap;

    public GraphPrinter() {
        this(true);
    }

    public GraphPrinter(boolean bitmap) {
        this.bitmap = bitmap;
    }

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
     * @param clusterCount Number of clusters
     * @param path Path to output folder
     * @param output Output file name
     *
     * @throws java.io.UnsupportedEncodingException
     * @throws java.io.FileNotFoundException
     * @throws java.lang.InterruptedException
     */
    public void printClusters(Graph graph, double scale, int nodeToCluster[], int clusterCount, String path, String output) throws UnsupportedEncodingException, IOException, FileNotFoundException, InterruptedException {
        generateImage(graphVizClusterExport(scale, graph, nodeToCluster, clusterCount), path, output);
    }

    /**
     *
     * @param graph Graph to export
     * @param scale Scale of the graph
     * @param clusters Lists of nodes in each cluster
     * @param path Path to output folder
     * @param output Output file name
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws FileNotFoundException
     * @throws InterruptedException
     */
    public void printClusters(Graph graph, double scale, ArrayList<LinkedList<Node>> clusters, String path, String output) throws UnsupportedEncodingException, IOException, FileNotFoundException, InterruptedException {
        generateImage(graphVizClusterExport(scale, graph, clusters), path, output);
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
        for (Node node : graph.getNodes()) {
            result += "    " + graph.getIndex(node) + "[fontsize=11 pos=\"" + node.getInstance().get(0) * scale + ","
                    + node.getInstance().get(1) * scale + "!\" width=0.1 height=0.1 shape=point]\n";
        }
        return result;
    }

    private String exportEdges(Graph graph) {
        String result = "";
        for (Edge edge : graph.getEdges()) {
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
    private String graphVizClusterExport(double scale, Graph graph, int nodeToCluster[], int clusterCount) {
        String result = "Graph G {\n";
        Color[] scheme = generateColorScheme(clusterCount);
        result += exportNodes(scale, graph, nodeToCluster, scheme);
        result += "}\n";
        return result;
    }

    /**
     * Exports clusters to Graphviz
     *
     * @param scale Scale of the graph
     * @param graph Graph to export
     * @param clusters Lists of nodes in each cluster
     * @return Graphviz source code describing graph's clusters
     */
    private String graphVizClusterExport(double scale, Graph graph, ArrayList<LinkedList<Node>> clusters) {
        String result = "Graph G {\n";
        Color[] scheme = generateColorScheme(clusters.size());
        result += exportNodes(scale, graph, clusters, scheme);
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
        for (Node node : graph.getNodes()) {
            result += "    " + graph.getIndex(node) + "[fontsize=11 pos=\"" + node.getInstance().get(0) * scale + ","
                    + node.getInstance().get(1) * scale + "!\" width=0.1 height=0.1 shape=point color=\"#"
                    + String.format("%02x%02x%02x", scheme[nodeToCluster[graph.getIndex(node)]].getRed(),
                            scheme[nodeToCluster[graph.getIndex(node)]].getGreen(),
                            scheme[nodeToCluster[graph.getIndex(node)]].getBlue())
                    + "\"]\n";
        }
        return result;
    }

    private String exportNodes(double scale, Graph graph, ArrayList<LinkedList<Node>> clusters, Color[] scheme) {
        String result = "";
        for (int i = 0; i < clusters.size(); i++) {
            for (Node node : clusters.get(i)) {
                result += "    " + graph.getIndex(node) + "[fontsize=11 pos=\"" + node.getInstance().get(0) * scale + ","
                        + node.getInstance().get(1) * scale + "!\" width=0.1 height=0.1 shape=point color=\"#"
                        + String.format("%02x%02x%02x", scheme[i].getRed(),
                                scheme[i].getGreen(),
                                scheme[i].getBlue())
                        + "\"]\n";
            }
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
            Process p;
            if (bitmap) {
                p = Runtime.getRuntime().exec("neato -Tpng -o " + path + "/" + output + ".png -Gmode=KK " + path + "/" + "tempfile");
            } else {
                p = Runtime.getRuntime().exec("neato -Tps -o " + path + "/" + output + ".eps -Gmode=KK " + path + "/" + "tempfile");
            }
            p.waitFor();
            File file = new File(path + "/" + "tempfile");
            file.delete();
        }
    }
}
