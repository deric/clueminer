package org.clueminer.partitioning.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.Partitioning;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = Partitioning.class)
public class RecursiveBisection implements Partitioning {

    private int maxNodesInCluster;
    private Graph graph;
    private boolean marked[];
    private ArrayList<LinkedList<Node>> clusters;
    private Bisection bisection;

    public RecursiveBisection() {
        this(new FiducciaMattheyses());
    }

    public RecursiveBisection(Bisection bisection) {
        this.bisection = bisection;
    }

    public void setBisection(Bisection bisection) {
        this.bisection = bisection;
    }

    @Override
    public String getName() {
        return "Recursive bisection";
    }

    @Override
    public ArrayList<LinkedList<Node>> partition(int max, Graph g) {
        maxNodesInCluster = max;
        graph = g;
        if (graph.getNodeCount() < maxNodesInCluster) {
            ArrayList<LinkedList<Node>> nodes = new ArrayList<>();
            nodes.add(new LinkedList<>(g.getNodes().toCollection()));
            return nodes;
        } else {
            clusters = recursivePartition(graph);
        }
        Graph clusteredGraph = removeUnusedEdges();
        FloodFill f = new FloodFill();
        return f.findSubgraphs(clusteredGraph);
    }

    public ArrayList<LinkedList<Node>> recursivePartition(Graph g) {
        ArrayList<LinkedList<Node>> result = bisection.bisect(g);
        ArrayList<LinkedList<Node>> output = new ArrayList<>();
        for (int i = 0; i <= 1; i++) {
            if (result.get(i).size() <= maxNodesInCluster) {
                output.add(result.get(i));
            } else {
                Graph newGraph = buildGraphFromCluster(result.get(i));
                output.addAll(recursivePartition(newGraph));
            }
        }
        return output;
    }

    private Graph buildGraphFromCluster(LinkedList<Node> n) {
        ArrayList<Node> nodes = new ArrayList<>(n);
        Graph newGraph = new AdjMatrixGraph(nodes.size());
        for (Node node : nodes) {
            newGraph.addNode(node);
        }
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                if (graph.isAdjacent(nodes.get(i), nodes.get(j))) {
                    newGraph.addEdge(graph.getEdge(nodes.get(i), nodes.get(j)));
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

        for (LinkedList<Node> cluster : clusters) {
            for (int i = 0; i < cluster.size(); i++) {
                for (int j = i + 1; j < cluster.size(); j++) {
                    if (graph.isAdjacent(cluster.get(i), cluster.get(j))) {
                        g.addEdge(graph.getEdge(cluster.get(i), cluster.get(j)));
                    }
                }
            }
        }
        return g;
    }

}
