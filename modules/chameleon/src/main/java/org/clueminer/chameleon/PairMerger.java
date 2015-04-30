package org.clueminer.chameleon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import org.clueminer.clustering.algorithm.HClustResult;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.hclust.DClusterLeaf;
import org.clueminer.hclust.DTreeNode;
import org.clueminer.hclust.DynamicClusterTreeData;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.utils.Props;

/**
 * This class merges two clusters in one merge. Two most similar clusters among
 * all pairs are merged at each step.
 *
 * @author Tomas Bruna
 */
public abstract class PairMerger extends Merger {

    DendroNode[] nodes;

    PriorityQueue<Element> pq;

    /**
     * Set of merged clusters which are ignored. They could also be deleted but
     * deleting them from cluster array, external properties matrix and priority
     * queue would be too expensive.
     */
    HashSet<Integer> blacklist = new HashSet<>();

    double height;

    public PairMerger(Graph g, Bisection bisection, double closenessPriority) {
        super(g, bisection, closenessPriority);
    }

    public HierarchicalResult getHierarchy(ArrayList<LinkedList<Node>> clusterList, Dataset<? extends Instance> dataset, Props pref) {
        createClusters(clusterList, bisection);
        computeExternalProperties();
        buildQueue();
        initiateTree(clusterList);
        HierarchicalResult result = new HClustResult(dataset);
        //GraphPrinter gp = new GraphPrinter(true);
        for (int i = 0; i < clusterList.size() - 1; i++) {
            singleMerge(clusterList);
//            if (i > clusterList.size() - 20) {
//                try {
//                    gp.printClusters(graph, 2, getResult(), "/home/tomas/Desktop/outputs", Integer.toString(i));
//                } catch (IOException | InterruptedException ex) {
//
//                }
//            }
        }
        DendroTreeData treeData = new DynamicClusterTreeData(nodes[2 * clusterList.size() - 2]);
        treeData.print();
        treeData.createMapping(dataset.size(), treeData.getRoot());

        result.setTreeData(treeData);
        return result;
    }

    /**
     * Creates tree leaves and fills them with nodes.
     *
     * @param clusterList Initial clusters
     */
    private void initiateTree(ArrayList<LinkedList<Node>> clusterList) {
        nodes = new DendroNode[(2 * clusterList.size() - 1)];
        clusterCount = clusterList.size();
        height = 1;
        for (int i = 0; i < clusterList.size(); i++) {
            nodes[i] = new DClusterLeaf(i, createInstanceList(clusterList.get(i)));
            nodes[i].setHeight(0);
        }
    }

    private LinkedList<Instance> createInstanceList(LinkedList<Node> nodes) {
        LinkedList<Instance> out = new LinkedList<>();
        for (Node node : nodes) {
            out.add(node.getInstance());
        }
        return out;
    }

    /**
     * Merges two most similar clusters
     *
     * @param clusterList
     */
    private void singleMerge(ArrayList<LinkedList<Node>> clusterList) {
        Element curr = pq.poll();
        while (blacklist.contains(curr.firstCluster) || blacklist.contains(curr.secondCluster)) {
            curr = pq.poll();
        }
        blacklist.add(curr.firstCluster);
        blacklist.add(curr.secondCluster);
        if (curr.firstCluster == curr.secondCluster) {
            throw new RuntimeException("Cannot merge two same clusters");
        }
        createNewCluster(curr.firstCluster, curr.secondCluster);
        updateExternalProperties(curr.firstCluster, curr.secondCluster);
        addIntoQueue();
    }

    /**
     * Computes similarities between the merged and other active clusters and
     * adds them into the priority queue. The merged cluster is the last one in
     * both cluster array and external properties matrix, therefore we use index
     * clusterCount -1.
     */
    private void addIntoQueue() {
        for (int i = 0; i < clusterCount - 1; i++) {
            if (blacklist.contains(i)) {
                continue;
            }
            pq.add(new Element(computeSimilarity(clusterCount - 1, i), i, clusterCount - 1));
        }
    }

    /**
     * Computes similarities between all clusters and adds them into the
     * priority queue.
     */
    private void buildQueue() {
        pq = new PriorityQueue<>();
        for (int i = 0; i < clusterCount; i++) {
            for (int j = 0; j < i; j++) {
                pq.add(new Element(computeSimilarity(i, j), i, j));
            }
        }
    }

    /**
     * Creates new cluster from the two and add it to the end of the cluster
     * array.
     *
     * @param clusterIndex1
     * @param clusterIndex2
     */
    protected abstract void createNewCluster(int clusterIndex1, int clusterIndex2);

    /**
     * Computes similarity between two clusters
     *
     * @param i index of the first cluster
     * @param j index of the second cluster
     * @return similarity degree
     */
    protected abstract double computeSimilarity(int i, int j);

    /**
     * Adds node representing new cluster (the one created by merging) to
     * dendroTree
     *
     * @param clusterIndex1
     * @param clusterIndex2
     */
    protected void addIntoTree(int clusterIndex1, int clusterIndex2) {

        DendroNode left = nodes[clusters.get(clusterIndex1).id];
        DendroNode right = nodes[clusters.get(clusterIndex2).id];
        DTreeNode newNode = new DTreeNode(clusterCount);
        newNode.setLeft(left);
        newNode.setRight(right);
        double sim = computeSimilarity(clusterIndex1, clusterIndex2);
        if (sim > 10) {
            sim = 10;
        }
        if (sim < 0.01) {
            sim = 0.01;
        }
        height += 1 / sim;
        newNode.setHeight(height);
        nodes[clusterCount] = newNode;
    }

    /**
     * Computes external properties of the merged cluster and adds them to the
     * end of the external properties matrix.
     *
     * @param firstCluster
     * @param secondCluster
     */
    private void updateExternalProperties(int firstCluster, int secondCluster) {
        clusterMatrix.add(new ArrayList<ExternalProperties>());
        for (int i = 0; i < clusterCount - 1; i++) {
            if (blacklist.contains(i)) {
                clusterMatrix.get(clusterCount - 1).add(null);
                continue;
            }
            int index1, index2;
            //Swap indices to make the first index bigger (externalProperties matrix is triangular)
            index1 = max(i, firstCluster);
            index2 = min(i, firstCluster);
            ExternalProperties firstClusterProperties = clusterMatrix.get(index1).get(index2);

            index1 = max(i, secondCluster);
            index2 = min(i, secondCluster);
            ExternalProperties secondClusterProperties = clusterMatrix.get(index1).get(index2);

            ExternalProperties mergedProperties = new ExternalProperties();
            mergedProperties.EIC = firstClusterProperties.EIC + secondClusterProperties.EIC;
            mergedProperties.counter = firstClusterProperties.counter + secondClusterProperties.counter;
            if (mergedProperties.counter != 0) {
                mergedProperties.ECL = mergedProperties.EIC / mergedProperties.counter;
            } else {
                mergedProperties.ECL = 0;
            }

            clusterMatrix.get(clusterCount - 1).add(mergedProperties);
        }
    }

    /**
     * Returns lists of nodes in each cluster. Used only for graph printing, the
     * real result is stored in the tree.
     *
     * @return
     */
    private ArrayList<LinkedList<Node>> getResult() {
        ArrayList<LinkedList<Node>> result = new ArrayList<>();
        for (int i = 0; i < clusterCount; i++) {
            if (blacklist.contains(i)) {
                continue;
            }
            result.add(clusters.get(i).getNodes());
        }
        return result;
    }

    private Clustering<org.clueminer.clustering.api.Cluster> getClusterResult() {
        Clustering output = new ClusterList(clusters.size());
        for (Cluster c : clusters) {
            BaseCluster cluster = new BaseCluster(c.getNodeCount());
            for (Node node : c.getNodes()) {
                cluster.add(node.getInstance());
            }
            output.add(cluster);
        }
        return output;
    }

    protected int max(int n1, int n2) {
        if (n1 > n2) {
            return n1;
        }
        return n2;
    }

    protected int min(int n1, int n2) {
        if (n1 < n2) {
            return n1;
        }
        return n2;
    }

}
