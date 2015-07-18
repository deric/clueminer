package org.clueminer.partitioning.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.Partitioning;
import org.openide.util.Exceptions;
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
        ArrayList<LinkedList<Node>> clusters;
        if (graph.getNodeCount() < maxNodesInCluster) {
            ArrayList<LinkedList<Node>> nodes = new ArrayList<>();
            nodes.add(new LinkedList<>(g.getNodes().toCollection()));
            return nodes;
        } else {
            clusters = recursivePartition(graph);
        }
        Graph clusteredGraph = new EdgeRemover().removeEdges(graph, clusters);
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
        Graph newGraph = null;
        try {
            ArrayList<Node> nodes = new ArrayList<>(n);
            newGraph = graph.getClass().newInstance();
            newGraph.ensureCapacity(nodes.size());

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
        } catch (InstantiationException | IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }
        return newGraph;
    }

}
