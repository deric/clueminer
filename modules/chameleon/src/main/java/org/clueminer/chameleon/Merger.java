package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.hclust.DClusterLeaf;
import org.clueminer.hclust.DTreeNode;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.utils.Props;

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

    /**
     * Set of merged clusters which are ignored. They could also be deleted but
     * deleting them from cluster array, external properties matrix and priority
     * queue would be too expensive.
     */
    protected HashSet<Integer> blacklist = new HashSet<>();

    protected MergeEvaluation evaluation;

    protected DendroNode[] nodes;

    /**
     * Tree height
     */
    protected double height;

    /**
     * Current tree level
     */
    protected int level;

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

    /**
     * Fetches graph from a GraphCluster instance
     *
     * @param clust
     * @return
     */
    public GraphPropertyStore getGraphPropertyStore(GraphCluster<E> clust) {
        Graph g = clust.getGraph();
        GraphPropertyStore gps = g.getLookup().lookup(GraphPropertyStore.class);
        if (gps == null) {
            throw new RuntimeException("graph property store was not found");
        }
        return gps;
    }

    /**
     * Computes external properties of the merged cluster and adds them to the
     * end of the external properties matrix.
     *
     * @param cluster new cluster
     * @param c1 clusters that are being merged
     * @param c2 clusters that are being merged
     */
    protected void updateExternalProperties(GraphCluster<E> cluster, GraphCluster<E> c1, GraphCluster<E> c2) {
        double eic1, eic2, cnt1, cnt2, eic, ecl, cnt;
        for (int i = 0; i < clusterCount - 1; i++) {
            if (blacklist.contains(i)) {
                continue;
            }
            GraphPropertyStore gps = getGraphPropertyStore(c1);

            eic1 = gps.getEIC(c1.getClusterId(), i);
            cnt1 = gps.getCnt(c1.getClusterId(), i);

            eic2 = gps.getEIC(c2.getClusterId(), i);
            cnt2 = gps.getCnt(c2.getClusterId(), i);

            ecl = 0;
            eic = eic1 + eic2;

            cnt = cnt1 + cnt2;
            if (cnt > 0) {
                ecl = eic / cnt;
            }
            gps.set(i, cluster.getClusterId(), eic, ecl, cnt);
        }
    }

    /**
     * Adds node representing new cluster (the one created by merging) to
     * dendroTree
     *
     * @param a
     * @param b
     * @param pref
     */
    protected void addIntoTree(GraphCluster<E> a, GraphCluster<E> b, Props pref) {
        DendroNode left = nodes[a.getClusterId()];
        DendroNode right = nodes[b.getClusterId()];
        DTreeNode newNode = new DTreeNode(clusterCount);
        newNode.setLeft(left);
        newNode.setRight(right);
        double sim = evaluation.score(a, b, pref);
        if (sim > 10) {
            sim = 10;
        }
        if (sim < 0.005) {
            sim = 0.005;
        }
        height += 1 / sim;
        newNode.setHeight(height);
        newNode.setLevel(level++);
        nodes[clusterCount] = newNode;
    }

    public void setMergeEvaluation(MergeEvaluation eval) {
        this.evaluation = eval;
    }


}
