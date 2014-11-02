package org.clueminer.partitioning.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Tomas Bruna
 */
public class KernighanLin {

    private ArrayList<LinkedList<Vertex>> cluster;
    private final Node[] nodes;
    private Vertex[] vertexes;
    private boolean[] used;
    private Vertex[] swapPair;
    private LinkedList<ArrayList<Vertex>> swapHistory;
    private LinkedList<Double> swapHistoryCost;
    private final int nodeCount;
    private double maxCost;
    private int usedNodes;
    private final Graph graph;

    public KernighanLin(Graph g) {
        graph = g;
        nodes = g.getNodes().toArray();
        nodeCount = g.getNodeCount();
        usedNodes = 0;
        createVertexes();
    }

    public ArrayList<LinkedList<Node>> partition() {
        createIntitalPartition();
        computeCosts();
        //nodeCount-1 for odd numbers of nodes
        while (usedNodes < nodeCount - 1) {
            findBestPair();
            swapPair();
            updateCosts();
        }
        swapUpToBestIndex();
        return createNodeClusters();
        //return null;
    }

    private ArrayList<LinkedList<Node>> createNodeClusters() {
        ArrayList<LinkedList<Node>> clusters = new ArrayList<>();
        clusters.add(new LinkedList<Node>());
        clusters.add(new LinkedList<Node>());
        for (Vertex vertex : vertexes) {
            if (vertex.cluster == 0) {
                clusters.get(0).add(nodes[vertex.index]);
            } else {
                clusters.get(1).add(nodes[vertex.index]);
            }
        }
        return clusters;
    }

    private void swapUpToBestIndex() {
        int index = findBestSwaps();
        for (int i = 0; i <= index; i++) {
            int temp = swapHistory.get(i).get(0).cluster;
            swapHistory.get(i).get(0).cluster = swapHistory.get(i).get(1).cluster;
            swapHistory.get(i).get(1).cluster = temp;
        }

    }

    private int findBestSwaps() {
        int maxDifference = 0;
        int differenceSum = 0;
        int maxDifferenceIndex = -1;
        for (int i = 0; i < swapHistoryCost.size(); i++) {
            differenceSum += swapHistoryCost.get(i);
            if (differenceSum > maxDifference) {
                maxDifference = differenceSum;
                maxDifferenceIndex = i;
            }
        }
        return maxDifferenceIndex;
    }

    private void swapPair() {
        // int tempCluster = swapPair[1].cluster;
        // swapPair[0].cluster = swapPair[2].cluster;
        // swapPair[1].cluster = tempCluster;
        swapPair[0].used = true;
        swapPair[1].used = true;
        usedNodes += 2;
        ArrayList a = new ArrayList<>();
        a.add(swapPair[0]);
        a.add(swapPair[1]);
        swapHistoryCost.add(maxCost);
        swapHistory.add(a);
    }

    private void updateCosts() {
        for (int i = 0; i <= 1; i++) {
            ArrayList<Node> neighbors = (ArrayList<Node>) graph.getNeighbors(nodes[swapPair[i].index]).toCollection();
            for (Node neighbor : neighbors) {
                if (vertexes[neighbor.getIndex()].cluster == swapPair[i].cluster) {
                    vertexes[neighbor.getIndex()].difference += 2 * graph.getEdge(nodes[swapPair[i].index], neighbor).getWeight();
                } else {
                    vertexes[neighbor.getIndex()].difference -= 2 * graph.getEdge(nodes[swapPair[i].index], neighbor).getWeight();
                }
            }
        }
    }

    private void computeCosts() {
        for (Node node : nodes) {
            ArrayList<Node> neighbors = (ArrayList<Node>) graph.getNeighbors(node).toCollection();
            for (Node neighbor : neighbors) {
                if (vertexes[node.getIndex()].cluster == vertexes[neighbor.getIndex()].cluster) {
                    vertexes[node.getIndex()].internalCost += graph.getEdge(node, neighbor).getWeight();
                } else {
                    vertexes[node.getIndex()].externalCost += graph.getEdge(node, neighbor).getWeight();
                }

            }
            vertexes[node.getIndex()].difference = vertexes[node.getIndex()].externalCost - vertexes[node.getIndex()].internalCost;
        }
    }

    private void findBestPair() {
        maxCost = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < nodeCount; i++) {
            for (int j = i + 1; j < nodeCount; j++) {
                //having vertexes in list is pointless - sum would be n^2/2 too
                if ((vertexes[i].used || vertexes[j].used) || (vertexes[i].cluster == vertexes[j].cluster) ) {
                    continue;
                }
                double edgeWeight;
                if (graph.getEdge(nodes[i], nodes[j]) != null) {
                    edgeWeight = graph.getEdge(nodes[i], nodes[j]).getWeight();
                } else {
                    edgeWeight = 0;
                }
                double cost = vertexes[i].difference + vertexes[j].difference - 2 * edgeWeight;
                if (cost > maxCost) {
                    maxCost = cost;
                    swapPair[0] = vertexes[i];
                    swapPair[1] = vertexes[j];
                }
            }
        }
    }

    private void createVertexes() {
        vertexes = new Vertex[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            vertexes[i] = new Vertex();
            vertexes[i].index = nodes[i].getIndex();
        }
        swapPair = new Vertex[2];
        swapHistory = new LinkedList<>();
        swapHistoryCost = new LinkedList<>();
    }

    private void createIntitalPartition() {
        cluster = new ArrayList<>(2);
        cluster.add(new LinkedList<Vertex>());
        cluster.add(new LinkedList<Vertex>());
        for (int i = 0; i < nodeCount / 2; i++) {
            //  cluster.get(0).add(vertexes[i]);
            vertexes[i].cluster = 0;
        }
        for (int i = nodeCount / 2; i < nodeCount; i++) {
            //  cluster.get(1).add(vertexes[i]);
            vertexes[i].cluster = 1;
        }
    }

    public void printClusters() {
        ArrayList<LinkedList<Node>> clusters = createNodeClusters();
        for (int i = 0; i <= 1; i++) {
            System.out.print("Cluster " + i + ": ");
            for (Node n : clusters.get(i)) {
                System.out.print(n.getIndex() + ", ");
            }
            System.out.println("");
        }
    }
    
    public Graph removeUnusedEdges() {
        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < nodeCount; j++) {
                if (vertexes[i].cluster != vertexes[j].cluster) {
                    Edge e = graph.getEdge(nodes[i], nodes[j]);
                    if (e!=null) {
                       graph.removeEdge(e);
                    }
                }
            }
        } 
        return graph; // deep copy or new graph needed
    }

    public class Vertex {

        public Vertex() {
            internalCost = externalCost = 0;
            used = false;
        }
        int index;
        int cluster;
        boolean used;
        double internalCost;
        double externalCost;
        double difference;
    }
}
