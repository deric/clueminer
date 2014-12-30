package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.impl.KernighanLin;

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

    public Merger(Graph g) {
        this.graph = g;
    }

    /**
     * Creates clusters from lists of nodes
     *
     */
    private void createClusters(ArrayList<LinkedList<Node>> clusterList, Bisection bisection) {
        clusterCount = clusterList.size();
        clusters = new Cluster[clusterCount];
        int i = 0;
        for (LinkedList<Node> cluster : clusterList) {
            clusters[i] = new Cluster(cluster, graph, i);
            clusters[i].computeProperties(bisection);
            i++;
        }
        assignNodesToClusters(clusterList);
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
    private void assignNodesToClusters(ArrayList<LinkedList<Node>> clusterList) {
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
    private void computeExternalProperties() {
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

    /**
     * Merges clusters. Each cluster is merged with the most similar one
     *
     * @param clusterList List of clusters to merge
     *
     * @return Lists of nodes in each cluster
     */
    public ArrayList<LinkedList<Node>> merge(ArrayList<LinkedList<Node>> clusterList) {
        return merge(clusterList, new KernighanLin());
    }

    /**
     * Merges clusters. Each cluster is merged with the most similar one
     *
     * @param clusterList List of clusters to merge
     * @param bisection Bisection algorithm used for computing internal cluster
     * properties
     *
     * @return Lists of nodes in each cluster
     */
    public ArrayList<LinkedList<Node>> merge(ArrayList<LinkedList<Node>> clusterList, Bisection bisection) {
        createClusters(clusterList, bisection);
        computeExternalProperties();
        initiateClustersForMerging();

        for (int i = 0; i < clusterCount; i++) {
            double max = Double.NEGATIVE_INFINITY;
            int index = 0;
            for (int j = 0; j < clusterCount; j++) {
                if (i == j) {
                    continue;
                }
                double value = computeSimilarity(i, j);
                if (value > max) {
                    max = value;
                    index = j;
                }
            }
            mergeTwoClusters(clusters[i], clusters[index]);
        }
        return getNewClusters();
    }

    /**
     * Prepares clusters for merging
     */
    public void initiateClustersForMerging() {
        for (int i = 0; i < clusterCount; i++) {
            clusters[i].offsprings = new LinkedList<>();
            clusters[i].offsprings.add(clusters[i]);
            clusters[i].parent = clusters[i];
        }
    }

    /**
     * Computes relative interconnectivity and closeness and returns their sum
     *
     * @param i index of the first cluster
     * @param j index of the second cluster
     * @return sum of relative interconnectivity and closeness
     */
    private double computeSimilarity(int i, int j) {
        if (i > j) {
            int temp = i;
            i = j;
            j = temp;
        }
        double RIC = clusterMatrix[i][j].EIC / ((clusters[i].IIC + clusters[j].IIC) / 2);
        double nc1 = clusters[i].graph.getNodeCount();
        double nc2 = clusters[j].graph.getNodeCount();
        double RCL = clusterMatrix[i][j].ECL / ((nc1 / (nc1 + nc2)) * clusters[i].ICL + (nc2 / (nc1 + nc2)) * clusters[j].ICL);
        return RCL + RIC;
    }

    private void mergeTwoClusters(Cluster cluster1, Cluster cluster2) {
        if (cluster1.parent.index == cluster2.parent.index) {
            return;
        }
        if (cluster1.parent.offsprings.size() < cluster2.parent.offsprings.size()) {
            Cluster temp = cluster1;
            cluster1 = cluster2;
            cluster2 = temp;
        }
        cluster1.parent.offsprings.addAll(cluster2.parent.offsprings);
        Cluster parent = cluster2.parent;
        for (Cluster cluster : parent.offsprings) {
            cluster.parent = cluster1.parent;
        }
        parent.offsprings = null;
    }

    /**
     * Creates lists of nodes according to new clusters
     *
     * @return lists of nodes in clusters
     */
    public ArrayList<LinkedList<Node>> getNewClusters() {
        ArrayList<LinkedList<Node>> result = new ArrayList<>();
        for (int i = 0; i < clusterCount; i++) {
            if (clusters[i].offsprings != null) {
                LinkedList<Node> list = new LinkedList<>();
                for (Cluster cluster : clusters[i].offsprings) {
                    ArrayList<Node> nodes = (ArrayList<Node>) cluster.graph.getNodes().toCollection();
                    for (Node node : nodes) {
                        list.add(node);
                    }
                }
                result.add(list);
            }
        }
        return result;
    }

    /**
     * Returns node to cluster assignment after merging
     *
     * @return node to cluster assignment
     */
    public int[] getNodeToCluster() {
        int ntc[] = new int[graph.getNodeCount()];
        int index = 0;
        for (int i = 0; i < clusterCount; i++) {
            if (clusters[i].offsprings != null) {
                for (Cluster cluster : clusters[i].offsprings) {
                    ArrayList<Node> nodes = (ArrayList<Node>) cluster.graph.getNodes().toCollection();
                    for (Node node : nodes) {
                        ntc[graph.getIndex(node)] = index;
                    }
                }
                index++;
            }
        }
        return ntc;
    }

}
