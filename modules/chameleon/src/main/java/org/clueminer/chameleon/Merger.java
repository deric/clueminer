package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.hclust.DClusterLeaf;
import org.clueminer.partitioning.api.Bisection;

/**
 *
 * @author Tomas Bruna
 * @param <E>
 */
public abstract class Merger<E extends Instance> {

    /**
     * Original, not partitioned graph.
     */
    protected Graph graph;

    protected Bisection bisection;

    /**
     * Assigns each node to cluster.
     */
    protected int nodeToCluster[];

    /**
     * Number of clusters.
     */
    protected int clusterCount;

    /**
     * Clusters to merge.
     */
    protected ArrayList<GraphCluster<E>> clusters;

    
    public Merger(Graph g, Bisection bisection) {
        this.graph = g;
        this.bisection = bisection;
    }

    public Merger() {

    }

    /**
     * Creates clusters from lists of nodes
     *
     * @param clusterList
     * @param bisection
     * @return list of clusters
     */
    protected ArrayList<GraphCluster<E>> createClusters(ArrayList<LinkedList<Node<E>>> clusterList, Bisection bisection) {
        clusterCount = clusterList.size();
        clusters = new ArrayList<>();
        int i = 0;
        for (LinkedList<Node<E>> cluster : clusterList) {
            clusters.add(new GraphCluster(cluster, graph, i, bisection));
            i++;
        }
        assignNodesToClusters(clusterList);
        return clusters;
    }

    /**
     * Assigns clusters to nodes according to list of clusters in each node.
     * Having clusters assigned to nodes can be advantageous in some cases
     *
     * @param clusterList
     */
    protected void assignNodesToClusters(ArrayList<LinkedList<Node<E>>> clusterList) {
        nodeToCluster = new int[graph.getNodeCount()];
        int i = 0;
        for (LinkedList<Node<E>> cluster : clusterList) {
            for (Node node : cluster) {
                nodeToCluster[graph.getIndex(node)] = i;
            }
            i++;
        }
    }

    /**
     * Computes external interconnectivity and closeness between every two
     * clusters. Computed values are stored in a triangular matrix.
     *
     * Goes through all edges and if the edge connects different clusters, the
     * external values are updated
     *
     */
    protected void computeExternalProperties() {
        GraphPropertyStore gps = new GraphPropertyStore(clusterCount);
        int firstClusterID, secondClusterID;
        for (Edge edge : graph.getEdges()) {
            firstClusterID = nodeToCluster[graph.getIndex(edge.getSource())];
            secondClusterID = nodeToCluster[graph.getIndex(edge.getTarget())];
            if (firstClusterID != secondClusterID) {
                gps.updateWeight(firstClusterID, secondClusterID, edge.getWeight());
            }
        }
        graph.lookupAdd(gps);
    }

    /**
     * Creates tree leaves and fills them with nodes.
     *
     * @param clusterList Initial clusters
     * @return
     */
    protected DendroNode[] initiateTree(ArrayList<LinkedList<Node<E>>> clusterList) {
        DendroNode[] nodes = new DendroNode[(2 * clusterList.size() - 1)];
        clusterCount = clusterList.size();
        for (int i = 0; i < clusterList.size(); i++) {
            nodes[i] = new DClusterLeaf(i, createInstanceList(clusterList.get(i)));
            nodes[i].setHeight(0);
            nodes[i].setLevel(0);
        }
        return nodes;
    }

    protected LinkedList<Instance> createInstanceList(LinkedList<Node<E>> nodes) {
        LinkedList<Instance> out = new LinkedList<>();
        for (Node node : nodes) {
            out.add(node.getInstance());
        }
        return out;
    }

    public void setGraph(Graph g) {
        this.graph = g;
    }

    public void setBisection(Bisection b) {
        this.bisection = b;
    }
}
