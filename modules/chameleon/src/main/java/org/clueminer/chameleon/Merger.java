package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.SymmetricMatrix;
import org.clueminer.partitioning.api.Bisection;

/**
 *
 * @author Tomas Bruna
 */
public abstract class Merger {

    /**
     * Original, not partitioned graph.
     */
    Graph graph;

    Bisection bisection;

    /**
     * Assigns each node to cluster.
     */
    int nodeToCluster[];

    /**
     * Number of clusters.
     */
    int clusterCount;

    /**
     * If bigger than 1, algorithm gives a higher importance to the relative
     * closeness, otherwise, if lesser than 1, to interconnectivity.
     */
    double closenessPriority;

    /**
     * Clusters to merge.
     */
    ArrayList<Cluster> clusters;

    SimilarityMeasure similarityMeasure;

    /**
     * Matrix containing external properties of every 2 clusters.
     */
    ArrayList<ArrayList<ExternalProperties>> clusterMatrix;

    public Merger(Graph g, Bisection bisection, double closenessPriority, SimilarityMeasure similarityMeasure) {
        this.graph = g;
        this.bisection = bisection;
        this.closenessPriority = closenessPriority;
        this.similarityMeasure = similarityMeasure;
    }

    /**
     * Creates clusters from lists of nodes
     *
     */
    protected void createClusters(ArrayList<LinkedList<Node>> clusterList, Bisection bisection) {
        clusterCount = clusterList.size();
        clusters = new ArrayList<>();
        int i = 0;
        for (LinkedList<Node> cluster : clusterList) {
            clusters.add(new Cluster(cluster, graph, i, bisection));
            i++;
        }
        assignNodesToClusters(clusterList);
    }

    /**
     * Creates empty structure of external properties between every two
     * clusters.
     */
    protected void inititateClusterMatrix() {
        clusterMatrix = new ArrayList<>();
        for (int i = 0; i < clusterCount; i++) {
            clusterMatrix.add(new ArrayList<ExternalProperties>());
            for (int j = 0; j < i; j++) {
                clusterMatrix.get(i).add(new ExternalProperties());
            }
        }
    }

    /**
     * Assigns clusters to nodes according to list of clusters in each node.
     * Having clusters assigned to nodes can be advantageous in some cases
     *
     */
    protected void assignNodesToClusters(ArrayList<LinkedList<Node>> clusterList) {
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
    protected void computeExternalProperties() {
        inititateClusterMatrix();
        Iterator<Edge> edges = graph.getEdges().iterator();
        while (edges.hasNext()) {
            Edge edge = edges.next();
            int firstClusterID = nodeToCluster[graph.getIndex(edge.getSource())];
            int secondClusterID = nodeToCluster[graph.getIndex(edge.getTarget())];
            if (firstClusterID != secondClusterID) {
                //Swap values if the first is bigger. Matrix is symmetric so only half is filled
                if (secondClusterID > firstClusterID) {
                    int temp = firstClusterID;
                    firstClusterID = secondClusterID;
                    secondClusterID = temp;
                }
                //Update the values
                ExternalProperties properties = clusterMatrix.get(firstClusterID).get(secondClusterID);
                properties.EIC += edge.getWeight();
                properties.counter++;
                properties.ECL = properties.EIC / properties.counter;
            }
        }
    }

    public void printExternalProperties() {
        for (int i = 0; i < clusterMatrix.size(); i++) {
            for (int j = 0; j < i + 1; j++) {
                System.out.print("    ");
            }
            for (int j = 0; j < i; j++) {
                //System.out.print(" EIC: " + clusterMatrix.get(i).get(j).EIC + " ECL: " + clusterMatrix.get(i).get(j).ECL);
                System.out.print("R: " + computeSimilarity(i, j));
            }
            System.out.println("");
        }
    }

    public double getEIC(int firstClusterID, int secondClusterID) {
        if (secondClusterID > firstClusterID) {
            int temp = firstClusterID;
            firstClusterID = secondClusterID;
            secondClusterID = temp;
        }
        return clusterMatrix.get(firstClusterID).get(secondClusterID).EIC;
    }

    public double getECL(int firstClusterID, int secondClusterID) {
        if (secondClusterID > firstClusterID) {
            int temp = firstClusterID;
            firstClusterID = secondClusterID;
            secondClusterID = temp;
        }
        return clusterMatrix.get(firstClusterID).get(secondClusterID).ECL;
    }

    protected class ExternalProperties {

        public double EIC, ECL;
        public int counter;

        public ExternalProperties() {
            EIC = ECL = counter = 0;
        }
    }

    /**
     * Merges clusters.
     *
     * @param clusterList List of clusters to merge
     *
     * @return Lists of nodes in each cluster
     */
    abstract ArrayList<LinkedList<Node>> merge(ArrayList<LinkedList<Node>> clusterList);

    /**
     * Computes relative interconnectivity and closeness and returns their
     * product
     *
     * @param i index of the first cluster
     * @param j index of the second cluster
     * @return sum of relative interconnectivity and closeness
     */
    protected double computeSimilarity(int i, int j) {
        if (j > i) {
            int temp = i;
            i = j;
            j = temp;
        }
        if (similarityMeasure == SimilarityMeasure.IMPROVED) {
            return improvedSimilarity(i, j);
        } else if (similarityMeasure == SimilarityMeasure.STANDARD) {
            return chameleonSimilarity(i, j);
        } else {
            throw new RuntimeException("Unknown similarity measure");
        }
    }

    //Improved similarity scheme which handles noise much better and usually provides better results
    protected double improvedSimilarity(int i, int j) {

        double ec1 = clusters.get(i).graph.getEdgeCount();
        double ec2 = clusters.get(j).graph.getEdgeCount();

        if (ec1 == 0 || ec2 == 0) {
            return clusterMatrix.get(i).get(j).ECL * 40;
        }

        double val = (clusterMatrix.get(i).get(j).counter / (min(ec1, ec2)))
                * Math.pow((clusterMatrix.get(i).get(j).ECL / ((clusters.get(i).getACL() * ec1) / (ec1 + ec2) + (clusters.get(j).getACL() * ec2) / (ec1 + ec2))), closenessPriority)
                * Math.pow((min(clusters.get(i).getACL(), clusters.get(j).getACL()) / max(clusters.get(i).getACL(), clusters.get(j).getACL())), 1);

        return val;
    }

    //Standard way to compute cluster similarity via relative interconnectivity and closeness
    protected double chameleonSimilarity(int i, int j) {
        double RIC = getRIC(i, j);
        double RCL = getRCL(i, j);
        //give higher similarity to pair of clusters where one cluster is formed by single item
        if (clusters.get(i).graph.getNodeCount() == 1 || clusters.get(j).graph.getNodeCount() == 1) {
            return RIC * Math.pow(RCL, closenessPriority) * 40;
        }

        return RIC * Math.pow(RCL, closenessPriority);
    }

    protected double getRIC(int i, int j) {
        if (j > i) {
            int temp = i;
            i = j;
            j = temp;
        }
        return clusterMatrix.get(i).get(j).EIC / ((clusters.get(i).getIIC() + clusters.get(j).getIIC()) / 2);
    }

    protected double getRCL(int i, int j) {
        if (j > i) {
            int temp = i;
            i = j;
            j = temp;
        }
        double nc1 = clusters.get(i).graph.getNodeCount();
        double nc2 = clusters.get(j).graph.getNodeCount();
        return clusterMatrix.get(i).get(j).ECL / ((nc1 / (nc1 + nc2)) * clusters.get(i).getICL() + (nc2 / (nc1 + nc2)) * clusters.get(j).getICL());
    }

    protected Matrix createMatrix() {

        Matrix m = new SymmetricMatrix(clusterMatrix.size(), clusterMatrix.size());

        for (int i = 0; i < clusterMatrix.size(); i++) {
            for (int j = 0; j < i; j++) {
                double similarity = computeSimilarity(i, j);
                m.set(j, i, similarity);
            }
        }

        return m;
    }

    private double min(double a, double b) {
        if (a < b) {
            return a;
        }
        return b;
    }

    private double max(double a, double b) {
        if (a > b) {
            return a;
        }
        return b;
    }
}
