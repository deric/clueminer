package org.clueminer.chameleon;

import java.util.LinkedList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;

/**
 *
 * @author Tomas Bruna
 */
public class StandardSimilarity extends PairMerger {

    public StandardSimilarity(Graph g, Bisection bisection, double closenessPriority) {
        super(g, bisection, closenessPriority);
    }

    @Override
    protected void createNewCluster(int clusterIndex1, int clusterIndex2) {
        Cluster cluster1 = clusters.get(clusterIndex1);
        Cluster cluster2 = clusters.get(clusterIndex2);
        LinkedList<Node> clusterNodes = cluster1.getNodes();
        clusterNodes.addAll(cluster2.getNodes());
        // addIntoTree(clusterIndex1, clusterIndex2);
        Cluster newCluster = new Cluster(clusterNodes, graph, idCounter++, bisection);
        clusters.set(clusterIndex1, newCluster);
        clusters.remove(clusterIndex2);
    }

    @Override
    protected double computeSimilarity(int i, int j) {
        if (j > i) {
            int temp = i;
            i = j;
            j = temp;
        }
        double RIC = getRIC(i, j);
        double RCL = getRCL(i, j);
        //give higher similarity to pair of clusters where one cluster is formed by single item
        if (clusters.get(i).graph.getNodeCount() == 1 || clusters.get(j).graph.getNodeCount() == 1) {
            return RIC * Math.pow(RCL, closenessPriority) * 40;
        }

        return RIC * Math.pow(RCL, closenessPriority);
    }

}
