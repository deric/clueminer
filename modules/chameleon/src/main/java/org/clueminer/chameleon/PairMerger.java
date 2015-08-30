package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import org.clueminer.clustering.algorithm.HClustResult;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.factory.Clusterings;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.hclust.DTreeNode;
import org.clueminer.hclust.DynamicClusterTreeData;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.utils.Props;

/**
 * This class merges two clusters in one merge. Two most similar clusters among
 * all pairs are merged at each step.
 *
 * @author Tomas Bruna
 * @param <E>
 */
public abstract class PairMerger<E extends Instance> extends Merger<E> {

    protected DendroNode[] nodes;

    protected PriorityQueue<PairValue<GraphCluster>> pq;

    int level;

    /**
     * Set of merged clusters which are ignored. They could also be deleted but
     * deleting them from cluster array, external properties matrix and priority
     * queue would be too expensive.
     */
    protected HashSet<Integer> blacklist = new HashSet<>();

    protected double height;

    public PairMerger() {

    }

    public PairMerger(Graph g, Bisection bisection) {
        super(g, bisection);
    }

    public HierarchicalResult getHierarchy(ArrayList<LinkedList<Node<E>>> clusterList, Dataset<? extends Instance> dataset, Props pref) {
        createClusters(clusterList, bisection);
        computeExternalProperties();
        buildQueue(pref);
        nodes = initiateTree(clusterList);
        height = 0;
        HierarchicalResult result = new HClustResult(dataset, pref);

        level = 1;
        for (int i = 0; i < clusterList.size() - 1; i++) {
            singleMerge(pq.poll(), pref);
        }

        DendroTreeData treeData = new DynamicClusterTreeData(nodes[2 * clusterList.size() - 2]);
        treeData.createMapping(dataset.size(), treeData.getRoot());
        result.setTreeData(treeData);
        return result;
    }

    /**
     * Merges two most similar clusters
     *
     * @param clusterList
     */
    private void singleMerge(Pair<GraphCluster> curr, Props pref) {
        int i = curr.A.getClusterId();
        int j = curr.B.getClusterId();
        while (blacklist.contains(i) || blacklist.contains(j)) {
            curr = pq.poll();
        }
        blacklist.add(i);
        blacklist.add(j);
        if (curr.A.getClusterId() == curr.B.getClusterId()) {
            throw new RuntimeException("Cannot merge two same clusters");
        }
        createNewCluster(curr.A, curr.B, pref);
        updateExternalProperties(curr.A, curr.B);
        addIntoQueue(pref);
    }

    /**
     * Computes similarities between the merged and other active clusters and
     * adds them into the priority queue. The merged cluster is the last one in
     * both cluster array and external properties matrix, therefore we use index
     * clusterCount -1.
     */
    private void addIntoQueue(Props pref) {
        double sim;
        GraphCluster a, b;
        for (int i = 0; i < clusterCount - 1; i++) {
            if (blacklist.contains(i)) {
                continue;
            }
            a = clusters.get(i);
            b = clusters.get(clusterCount - 1);
            sim = score(a, b, pref);
            pq.add(new PairValue<>(a, b, sim));
        }
    }

    /**
     * Computes similarities between all clusters and adds them into the
     * priority queue.
     */
    private void buildQueue(Props pref) {
        pq = new PriorityQueue<>();
        double sim;
        GraphCluster a, b;
        for (int i = 0; i < clusterCount; i++) {
            for (int j = 0; j < i; j++) {
                a = clusters.get(i);
                b = clusters.get(clusterCount - 1);
                sim = score(a, b, pref);
                pq.add(new PairValue<>(a, b, sim));
            }
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
        double sim = score(a, b, pref);
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

    /**
     * Computes external properties of the merged cluster and adds them to the
     * end of the external properties matrix.
     *
     * @param firstCluster
     * @param secondCluster
     */
    private void updateExternalProperties(GraphCluster<E> firstCluster, GraphCluster<E> secondCluster) {
        for (int i = 0; i < clusterCount - 1; i++) {
            if (blacklist.contains(i)) {
                continue;
            }
            int index1, index2;

            GraphPropertyStore gps = getGraphPropertyStore(firstCluster);
            //Swap indices to make the first index bigger (externalProperties matrix is triangular)
            index1 = Math.max(i, firstCluster.getClusterId());
            index2 = Math.min(i, firstCluster.getClusterId());

            double eic1 = gps.getEIC(index1, index2);
            double cnt1 = gps.getCnt(index1, index2);

            index1 = Math.max(i, secondCluster.getClusterId());
            index2 = Math.min(i, secondCluster.getClusterId());
            double eic2 = gps.getEIC(index1, index2);
            double cnt2 = gps.getCnt(index1, index2);

            double eic, ecl = 0, cnt;
            eic = eic1 + eic2;

            cnt = cnt1 + cnt2;
            if (cnt > 0) {
                ecl = eic / cnt;
            }
            gps.set(clusterCount - 1, i, eic, ecl, cnt);
        }
    }

    /**
     * Compute cost of merging cluster A and cluster B and thus forming larger
     * cluster C
     *
     * @param a
     * @param b
     * @param params optional parameters
     * @return
     */
    public abstract double score(Cluster<E> a, Cluster<E> b, Props params);

    /**
     * Method called by merger algorithm when cluster A and cluster B are merged
     * to form a new cluster
     *
     * @param a
     * @param b
     * @param params
     */
    public abstract void createNewCluster(Cluster<E> a, Cluster<E> b, Props params);

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
     * Returns lists of nodes in each cluster. Used only for graph printing, the
     * real result is stored in the tree.
     *
     * @return
     */
    private ArrayList<LinkedList<Node<E>>> getResult() {
        ArrayList<LinkedList<Node<E>>> result = new ArrayList<>();
        for (int i = 0; i < clusterCount; i++) {
            if (blacklist.contains(i)) {
                continue;
            }
            result.add(clusters.get(i).getNodes());
        }
        return result;
    }

    private Clustering getClusterResult() {
        Clustering output = Clusterings.newList(clusters.size());
        int i = 0;
        for (GraphCluster<E> g : clusters) {
            Cluster<E> cluster = output.createCluster(i++, g.getNodeCount());
            for (Node<E> node : g.getNodes()) {
                cluster.add(node.getInstance());
            }
            output.add(cluster);
        }
        return output;
    }

}
