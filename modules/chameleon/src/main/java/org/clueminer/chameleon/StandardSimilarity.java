package org.clueminer.chameleon;

import java.util.LinkedList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;

/**
 * This class implements the Chameleon's standard similarity measure. Internal
 * properties of the newly created cluster cannot be determined because they are
 * computed via bisection.
 *
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
        addIntoTree(clusterIndex1, clusterIndex2);
        Cluster newCluster = new Cluster(clusterNodes, graph, clusterCount++, bisection);
        clusters.add(newCluster);
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
        //give higher similarity to pair of clusters where one cluster is formed by single item (we want to get rid of them)
        if (clusters.get(i).getNodeCount() == 1 || clusters.get(j).getNodeCount() == 1) {
            return RIC * Math.pow(RCL, closenessPriority) * 40;
        }

        return RIC * Math.pow(RCL, closenessPriority);
    }

}
