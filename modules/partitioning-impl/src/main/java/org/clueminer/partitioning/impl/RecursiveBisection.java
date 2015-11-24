package org.clueminer.partitioning.impl;

import java.util.ArrayList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.Partitioning;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * Basic recursive bisection algorithm, by default Fiduccia-Mattheyses bisection
 * is used.
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = Partitioning.class)
public class RecursiveBisection implements Partitioning {

    private int maxNodesInCluster;
    private Bisection bisection;

    public RecursiveBisection() {
        this(new FiducciaMattheyses());
    }

    public RecursiveBisection(Bisection bisection) {
        this.bisection = bisection;
    }

    @Override
    public void setBisection(Bisection bisection) {
        this.bisection = bisection;
    }

    @Override
    public String getName() {
        return "Recursive bisection";
    }

    @Override
    public ArrayList<ArrayList<Node>> partition(int max, Graph g, Props params) {
        maxNodesInCluster = max;
        int expectedSize = g.getNodeCount() / max;
        ArrayList<ArrayList<Node>> clusters;
        if (g.getNodeCount() < maxNodesInCluster) {
            ArrayList<ArrayList<Node>> nodes = new ArrayList<>(expectedSize);
            nodes.add(new ArrayList<>(g.getNodes().toCollection()));
            return nodes;
        } else {
            clusters = recursivePartition(g, params, expectedSize);
        }
        Graph clusteredGraph = new EdgeRemover().removeEdges(g, clusters);
        FloodFill f = new FloodFill();
        return f.findSubgraphs(clusteredGraph, max);
    }

    public ArrayList<ArrayList<Node>> recursivePartition(Graph g, Props params, int expectedSize) {
        ArrayList<ArrayList<Node>> output = new ArrayList<>(expectedSize);
        ArrayList<ArrayList<Node>> result = bisection.bisect(g, params);
        int i = 0;

        while (i < 2) {
            if (result.get(i).size() <= maxNodesInCluster) {
                output.add(result.get(i));
            } else {
                Graph newGraph = buildGraphFromCluster(g, result.get(i));
                output.addAll(recursivePartition(newGraph, params, expectedSize));
            }
            i++;
        }
        return output;
    }

    private Graph buildGraphFromCluster(Graph graph, ArrayList<Node> n) {
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
