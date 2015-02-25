package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;

/**
 * This class merges two clusters in one merge. Two most similar clusters among
 * all pairs are merged at each step.
 *
 * @author Tomas Bruna
 */
public class PairMerger extends Merger {

    public PairMerger(Graph g) {
        super(g);
    }

    public PairMerger(Graph g, Bisection bisection) {
        super(g, bisection);
    }

    @Override
    ArrayList<LinkedList<Node>> merge(ArrayList<LinkedList<Node>> clusterList, int mergeCount) {
        createClusters(clusterList, bisection);
        computeExternalProperties();
        for (int i = 0; i < mergeCount; i++) {
            singleMerge(clusterList);
        }
        return getResult();
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
        clusters.set(clusterIndex1, new Cluster(mergedNodes, graph, clusterIndex1));
        clusters.get(clusterIndex1).computeProperties(bisection);
        clusters.remove(clusterIndex2);
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
            properties1.ECL = properties1.EIC / properties1.counter;
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
            LinkedList<Node> nodes = new LinkedList<>();
            nodes.addAll(nodesArr);
            result.add(nodes);
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
