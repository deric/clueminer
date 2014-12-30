package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
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
    double IIC;

    /**
     * Internal closeness of the cluster (average of cut edges)
     */
    double ICL;

    /**
     * Bisected halves of the cluster
     */
    Graph firstHalf, secondHalf;

    public Cluster parent;

    public LinkedList<Cluster> offsprings;

    public int index;

    public Cluster(LinkedList<Node> n, Graph g, int index) {
        buildGraphFromCluster(n, g);
        this.index = index;
    }

    public Cluster(Graph g, int index) {
        graph = g;
        this.index = index;
    }

    /**
     * Bisects the graph and computes IIC and ICL from the bisection
     *
     * @param bisection Bisection algorithm
     */
    public void computeProperties(Bisection bisection) {
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

    public double getIIC() {
        return IIC;
    }

    public double getICL() {
        return ICL;
    }

}
