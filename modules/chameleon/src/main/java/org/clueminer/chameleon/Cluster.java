package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;

/**
 *
 * @author Tomas Bruna
 */
public class Cluster {

    /**
     * Graph representing this cluster
     */
    Graph graph;

    /**
     * Internal interconnectivity of the cluster (sum of cut edges)
     */
    private double IIC = -1;

    /**
     * Internal closeness of the cluster (average of cut edges)
     */
    private double ICL = -1;

    /**
     * Average closeness of the cluster (average of all edges)
     */
    private double ACL = -1;

    private Bisection bisection;

    /**
     * Bisected halves of the cluster
     */
    Graph firstHalf, secondHalf;

    public Cluster parent;

    public LinkedList<Cluster> offsprings;

    public int id;

    public Cluster(LinkedList<Node> n, Graph g, int index, Bisection bisection) {
        buildGraphFromCluster(n, g);
        this.id = index;
        this.bisection = bisection;
    }

    public Cluster(Graph g, int index, Bisection bisection) {
        graph = g;
        this.id = index;
        this.bisection = bisection;
    }

    /**
     * Bisects the graph and computes IIC and ICL from the bisection
     *
     * @param bisection Bisection algorithm
     */
    private void computeBisectionProperties() {
        //If bisection cannot be done, set values to 1
        if (graph.getNodeCount() == 1) {
            ICL = IIC = 1;
            return;
        }
        ArrayList<LinkedList<Node>> result = bisection.bisect(graph);
        IIC = ICL = 0;
        int counter = 0;
        for (Node node1 : result.get(0)) {
            for (Node node2 : result.get(1)) {
                if (graph.isAdjacent(node1, node2)) {
                    IIC += graph.getEdge(node1, node2).getWeight();
                    counter++;
                }
            }
        }
        ICL = IIC / counter;
    }

    /**
     * Computes average weight of all edges in the graph
     */
    private void computeAverageCloseness() {
        double sum = 0;
        if (graph.getEdgeCount() == 0) {
            ACL = 0;
            return;
        }
        for (Edge e : graph.getEdges().toCollection()) {
            sum += e.getWeight();
        }
        ACL = sum / graph.getEdgeCount();
    }

    /**
     * Builds graph from list of nodes and parent graph
     *
     * @param n List of nodes
     * @param g Parent graph
     * @return Graph representing this cluster
     */
    private Graph buildGraphFromCluster(LinkedList<Node> n, Graph g) {
        ArrayList<Node> nodes = new ArrayList<>(n);
        graph = new AdjMatrixGraph(nodes.size());
        for (Node node : nodes) {
            graph.addNode(node);
        }
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                if (g.isAdjacent(nodes.get(i), nodes.get(j))) {
                    graph.addEdge(g.getEdge(nodes.get(i), nodes.get(j)));
                }
            }
        }
        return graph;
    }

    /**
     *
     * @return Internal interconnectivity computed by bisection
     */
    public double getIIC() {
        if (IIC == -1) {
            computeBisectionProperties();
        }
        return IIC;
    }

    /**
     *
     * @return Internal closeness computed by bisection
     */
    public double getICL() {
        if (ICL == -1) {
            computeBisectionProperties();
        }
        return ICL;
    }

    /**
     *
     * @return Average internal closeness
     */
    public double getACL() {
        if (ACL == -1) {
            computeAverageCloseness();
        }
        return ACL;
    }

}
