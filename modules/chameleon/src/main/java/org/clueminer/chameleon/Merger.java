package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Tomas Bruna
 */
public class Merger {

    /**
     * Original, not partitioned graph.
     */
    Graph graph;

    /**
     * Assigns each node to cluster.
     */
    int nodeToCluster[];

    /**
     * Number of clusters.
     */
    int clusterCount;

    /**
     * Clusters to merge.
     */
    Cluster clusters[];

    /**
     * Matrix containing external properties of every 2 clusters.
     */
    ExternalProperties clusterMatrix[][];

    ArrayList<LinkedList<Node>> clusterList;

    public Merger(Graph g, ArrayList<LinkedList<Node>> clusterList) {
        this.graph = g;
        this.clusterList = clusterList;
        createClusters();
        assignNodesToClusters();
    }

    /**
     * Creates clusters from lists of nodes
     *
     */
    private void createClusters() {
        clusterCount = clusterList.size();
        clusters = new Cluster[clusterCount];
        int i = 0;
        for (LinkedList<Node> cluster : clusterList) {
            clusters[i] = new Cluster(cluster, graph);
            i++;
        }
    }

    /**
     * Creates empty structure of external properties between every two
     * clusters.
     */
    private void inititateClusterMatrix() {
        clusterMatrix = new ExternalProperties[clusterCount][clusterCount];
        for (int i = 0; i < clusterCount; i++) {
            for (int j = i + 1; j < clusterCount; j++) {
                clusterMatrix[i][j] = new ExternalProperties();
            }
        }
    }

    /**
     * Assigns clusters to nodes according to list of clusters in each node.
     * Having clusters assigned to nodes can be advantageous in some cases
     *
     */
    private void assignNodesToClusters() {
        nodeToCluster = new int[graph.getNodeCount()];
        int i = 0;
        for (LinkedList<Node> cluster : clusterList) {
            for (Node node : cluster) {
                nodeToCluster[graph.getIndex(node)] = i;
            }
            i++;
        }
    }

    /**
     * Computes external interconnectivity and closeness between every two
     * clusters. Computed values are store in the cluster matrix.
     *
     * Goes through all edges and if the edge connects different clusters, the
     * external values are updated
     *
     */
    public void computeExternalProperties() {
        inititateClusterMatrix();
        Iterator<Edge> edges = graph.getEdges().iterator();
        while (edges.hasNext()) {
            Edge edge = edges.next();
            int firstClusterID = nodeToCluster[graph.getIndex(edge.getSource())];
            int secondClusterID = nodeToCluster[graph.getIndex(edge.getTarget())];
            if (firstClusterID != secondClusterID) {
                //Swap values if the first is bigger. Matrix is symmetric so only half is filled
                if (firstClusterID > secondClusterID) {
                    int temp = firstClusterID;
                    firstClusterID = secondClusterID;
                    secondClusterID = temp;
                }
                //Update the values
                ExternalProperties properties = clusterMatrix[firstClusterID][secondClusterID];
                properties.EIC += edge.getWeight();
                properties.counter++;
                properties.ECL = properties.EIC / properties.counter;
            }
        }
    }

    public void printExternalProperties() {
        for (int i = 0; i < clusterCount; i++) {
            for (int j = 0; j < i + 1; j++) {
                System.out.print("    ");
            }
            for (int j = i + 1; j < clusterCount; j++) {
                System.out.print(" EIC: " + clusterMatrix[i][j].EIC + " ECL: " + clusterMatrix[i][j].ECL);
            }
            System.out.println("");
        }
    }

    public double getEIC(int firstClusterID, int secondClusterID) {
        if (firstClusterID > secondClusterID) {
            int temp = firstClusterID;
            firstClusterID = secondClusterID;
            secondClusterID = temp;
        }
        return clusterMatrix[firstClusterID][secondClusterID].EIC;
    }

    public double getECL(int firstClusterID, int secondClusterID) {
        if (firstClusterID > secondClusterID) {
            int temp = firstClusterID;
            firstClusterID = secondClusterID;
            secondClusterID = temp;
        }
        return clusterMatrix[firstClusterID][secondClusterID].ECL;
    }

    private class ExternalProperties {

        public double EIC, ECL;
        public int counter;

        public ExternalProperties() {
            EIC = ECL = counter = 0;
        }
    }

}
