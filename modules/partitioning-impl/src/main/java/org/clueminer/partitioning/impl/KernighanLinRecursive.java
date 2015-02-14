package org.clueminer.partitioning.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Partitioning;

/**
 *
 * @author Tomas Bruna
 */
public class KernighanLinRecursive implements Partitioning {

    int maxNodesInCluster;
    Graph graph;
    boolean marked[];
    ArrayList<LinkedList<Node>> clusters;

    public KernighanLinRecursive() {

    }

    @Override
    public ArrayList<LinkedList<Node>> partition(int max, Graph g) {
        maxNodesInCluster = max;
        graph = g;
        if (graph.getNodeCount() < maxNodesInCluster) {
            return null; //create one list with nodes
        } else {
            clusters = recursivePartition(graph);
        }
        Graph clusteredGraph = removeUnusedEdges();
        FloodFill f = new FloodFill();
        return f.findSubgraphs(clusteredGraph);
    }

    public ArrayList<LinkedList<Node>> recursivePartition(Graph g) {
        KernighanLin kl = new KernighanLin(g, false);
        ArrayList<LinkedList<Node>> result = kl.bisect(g);
        ArrayList<LinkedList<Node>> output = new ArrayList<>();
        for (int i = 0; i <= 1; i++) {
            if (result.get(i).size() <= maxNodesInCluster) {
                output.add(result.get(i));
            } else {
                Graph newGraph = buildGraphFromCluster(result.get(i), g);
                output.addAll(recursivePartition(newGraph));
            }
        }
        return output;
    }

    private Graph buildGraphFromCluster(LinkedList<Node> n, Graph g) {
        ArrayList<Node> nodes = new ArrayList<>(n);
        Graph newGraph = new AdjMatrixGraph(nodes.size());
        for (Node node : nodes) {
            newGraph.addNode(node);
        }
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                if (g.isAdjacent(nodes.get(i), nodes.get(j))) {
                    newGraph.addEdge(g.getEdge(nodes.get(i), nodes.get(j)));
                }
            }
        }
        return newGraph;
    }

    @Override
    public Graph removeUnusedEdges() {
        Graph g = new AdjMatrixGraph(graph.getNodeCount());

        ArrayList<Node> nodes = (ArrayList<Node>) graph.getNodes().toCollection();

        for (Node node : nodes) {
            g.addNode(node);
        }

        for (int k = 0; k < clusters.size(); k++) {
            for (int i = 0; i < clusters.get(k).size(); i++) {
                for (int j = i + 1; j < clusters.get(k).size(); j++) {
                    if (graph.isAdjacent(clusters.get(k).get(i), clusters.get(k).get(j))) {
                        g.addEdge(graph.getEdge(clusters.get(k).get(i), clusters.get(k).get(j)));
                    }
                }
            }
        }
        return g;
    }

}
