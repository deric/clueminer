package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
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

/**
 * This class merges two clusters in one merge. Two most similar clusters among
 * all pairs are merged at each step.
 *
 * @author Tomas Bruna
 */
public class PairMerger extends Merger {

    DendroNode[] nodes;

    int idCounter;

    double height;

    public PairMerger(Graph g, Bisection bisection, double closenessPriority, SimilarityMeasure similarityMeasure) {
        super(g, bisection, closenessPriority, similarityMeasure);
    }

    ArrayList<LinkedList<Node>> merge(ArrayList<LinkedList<Node>> clusterList, int mergeCount) {
        createClusters(clusterList, bisection);
        computeExternalProperties();
        for (int i = 0; i < mergeCount; i++) {
            singleMerge(clusterList);
        }
        return getResult();
    }

    @Override
    ArrayList<LinkedList<Node>> merge(ArrayList<LinkedList<Node>> clusterList) {
        return null;
    }

    public HierarchicalResult getHierarchy(ArrayList<LinkedList<Node>> clusterList, Dataset<? extends Instance> dataset) {
        createClusters(clusterList, bisection);
        computeExternalProperties();
        initiateTree(clusterList);
        HierarchicalResult result = new HClustResult(dataset);

        for (int i = 0; i < clusterList.size() - 1; i++) {
            singleMerge(clusterList);
            //GraphPrinter gp = new GraphPrinter(true);
            // gp.printClusters(graph, 5, getResult(), FileUtils.LocalFolder(), Integer.toString(i));
        }

        DendroTreeData treeData = new DynamicClusterTreeData(nodes[2 * clusterList.size() - 2]);
        // treeData.printWithHeight();
        treeData.createMapping(dataset.size(), treeData.getRoot());

        result.setTreeData(treeData);
        return result;
    }

    private void initiateTree(ArrayList<LinkedList<Node>> clusterList) {
        nodes = new DendroNode[(2 * clusterList.size() - 1)];
        idCounter = clusterList.size();
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

    private void singleMerge(ArrayList<LinkedList<Node>> clusterList) {

        double max = Double.NEGATIVE_INFINITY;
        int index1 = 0;
        int index2 = 0;
        for (int i = 0; i < clusterCount; i++) {
            for (int j = 0; j < i; j++) {
                double value = computeSimilarity(i, j);
                if (value > max) {
                    max = value;
                    index1 = i;
                    index2 = j;
                }
            }
        }
        mergeTwoClusters(index2, index1);
    }

    private void mergeTwoClusters(int clusterIndex1, int clusterIndex2) {

        if (clusterIndex2 == clusterIndex1) {
            return;
        }

        //Swap the clusters if the bigger is second because the second one is merged into the first
        if (clusters.get(clusterIndex1).graph.getNodeCount() < clusters.get(clusterIndex2).graph.getNodeCount()) {
            int tempIndex = clusterIndex1;
            clusterIndex1 = clusterIndex2;
            clusterIndex2 = tempIndex;
        }

        //Create new cluster from the two by merging
        createNewCluster(clusterIndex1, clusterIndex2);

        //Update properties because of the new cluster
        updateExternalProperties(clusterIndex1, clusterIndex2);

        //Delete cluster which was merged into the first from the clusterMatrix
        deleteCluster(clusterIndex2);

        clusterCount--;

    }

    //Creates new cluster from the two input clusters and deletes the old ones
    private void createNewCluster(int clusterIndex1, int clusterIndex2) {
        Cluster cluster1 = clusters.get(clusterIndex1);
        Cluster cluster2 = clusters.get(clusterIndex2);
        ArrayList<Node> nodes1 = (ArrayList<Node>) cluster1.graph.getNodes().toCollection();
        ArrayList<Node> nodes2 = (ArrayList<Node>) cluster2.graph.getNodes().toCollection();
        LinkedList<Node> mergedNodes = new LinkedList<>();
        mergedNodes.addAll(nodes1);
        mergedNodes.addAll(nodes2);
        addIntoTree(clusterIndex1, clusterIndex2);
        clusters.set(clusterIndex1, new Cluster(mergedNodes, graph, idCounter++, bisection));
        clusters.remove(clusterIndex2);
    }

    //Adds node representing new cluster (the one created by merging) to dendroTree
    private void addIntoTree(int clusterIndex1, int clusterIndex2) {
        DendroNode left = nodes[clusters.get(clusterIndex1).id];
        DendroNode right = nodes[clusters.get(clusterIndex2).id];
        DTreeNode newNode = new DTreeNode(idCounter);
        newNode.setLeft(left);
        newNode.setRight(right);
        newNode.setHeight(height++);
        nodes[idCounter] = newNode;
    }

    //Updates ECL and EIC of all clusters adjacent to the merged one and thus the external properties of the newly created cluster
    private void updateExternalProperties(int clusterIndex1, int clusterIndex2) {
        for (int i = 0; i < clusterCount; i++) {
            //Do not update properties of the old clusters. They are used only to compute the new properties
            if (i == clusterIndex1 || i == clusterIndex2) {
                continue;
            }
            int index1, index2;
            //Swap indexes to make the first index always bigger (clusterMatrix is triangular)
            index1 = max(i, clusterIndex1);
            index2 = min(i, clusterIndex1);
            //External properties of the new cluster (the on which is created by the merge)
            ExternalProperties properties1 = clusterMatrix.get(index1).get(index2);
            //swap indexes to make the first index always bigger (clusterMatrix is triangular)
            index1 = max(i, clusterIndex2);
            index2 = min(i, clusterIndex2);
            //External properties of the cluster which is merged into the first
            ExternalProperties properties2 = clusterMatrix.get(index1).get(index2);

            properties1.EIC += properties2.EIC;
            properties1.counter += properties2.counter;
            if (properties1.counter != 0) {
                properties1.ECL = properties1.EIC / properties1.counter;
            }

        }
    }

    //Deletes cluster from the cluster matrix
    private void deleteCluster(int clusterIndex2) {
        for (ArrayList<ExternalProperties> clusterList : clusterMatrix) {
            if (clusterList.size() > clusterIndex2) {
                clusterList.remove(clusterIndex2);
            }

        }
        clusterMatrix.remove(clusterIndex2);
    }

    //Creates final output from the cluster array
    private ArrayList<LinkedList<Node>> getResult() {
        ArrayList<LinkedList<Node>> result = new ArrayList<>();
        for (Cluster cluster : clusters) {
            ArrayList<Node> nodesArr = (ArrayList<Node>) cluster.graph.getNodes().toCollection();
            LinkedList<Node> graphNodes = new LinkedList<>();
            graphNodes.addAll(nodesArr);
            result.add(graphNodes);
        }
        return result;
    }

    private Clustering<org.clueminer.clustering.api.Cluster> getClusterResult() {
        Clustering output = new ClusterList(clusters.size());
        for (Cluster c : clusters) {
            BaseCluster cluster = new BaseCluster(c.graph.getNodeCount());
            ArrayList<Node> nodesArr = (ArrayList<Node>) c.graph.getNodes().toCollection();
            Iterator<Node> graphNodes = c.graph.getNodes().iterator();
            while (graphNodes.hasNext()) {
                cluster.add(graphNodes.next().getInstance());
            }
            output.add(cluster);
        }
        return output;
    }

    private int max(int n1, int n2) {
        if (n1 > n2) {
            return n1;
        }
        return n2;
    }

    private int min(int n1, int n2) {
        if (n1 < n2) {
            return n1;
        }
        return n2;
    }

}
