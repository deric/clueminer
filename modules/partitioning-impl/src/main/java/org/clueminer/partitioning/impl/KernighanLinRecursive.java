package org.clueminer.partitioning.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixFactory;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Partitioning;

/**
 *
 * @author Tomas Bruna
 */
public class KernighanLinRecursive implements Partitioning {

    int maxNodexInCluster;
    Graph graph;

    public KernighanLinRecursive(int max, Graph g) {
        maxNodexInCluster = max;
        graph = g;
    }

    @Override
    public ArrayList<LinkedList<Node>> partition() {
        KernighanLin kl = new KernighanLin(graph);
        if (graph.getNodeCount() < maxNodexInCluster) {
            return null; //create one list with nodes
        } else {
            return recursivePartition(graph);
        }
    }

    public ArrayList<LinkedList<Node>> recursivePartition(Graph g) {
        KernighanLin kl = new KernighanLin(g);
        ArrayList<LinkedList<Node>> result = kl.partition();
        ArrayList<LinkedList<Node>> output = new ArrayList<>();
        for (int i = 0; i <= 1; i++) {
            if (result.get(0).size() <= maxNodexInCluster) {
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
        ArrayList<LinkedList<Node>> result = partition();
        Graph  g = new AdjMatrixGraph(graph.getNodeCount());
        
        ArrayList<Node> nodes = (ArrayList<Node>) graph.getNodes().toCollection();
        
        for (Node node : nodes) {
            g.addNode(node);
        }
         
        for (int k = 0; k < result.size(); k++) {
            for (int i = 0; i < result.get(k).size(); i++) {
                for (int j = i + 1; j < result.get(k).size(); j++) {
                    if (graph.isAdjacent(result.get(k).get(i), result.get(k).get(j))) {
                        g.addEdge(graph.getEdge(result.get(k).get(i), result.get(k).get(j)));
                    }
                }
            }
        }
        return g; 
    }

}
