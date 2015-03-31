package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;

/**
 * This class merges multiple clusters in one merge. Every cluster is merged
 * with the most similar one at each step.
 *
 * @author Tomas Bruna
 */
public class MultipleMerger extends Merger {

    public MultipleMerger(Graph g) {
        super(g);
    }

    public MultipleMerger(Graph g, Bisection bisection) {
        super(g, bisection);
    }

    public MultipleMerger(Graph g, Bisection bisection, double closenessPriority) {
        super(g, bisection, closenessPriority);
    }

    @Override
    public ArrayList<LinkedList<Node>> merge(ArrayList<LinkedList<Node>> clusterList, int mergeCount) {
        ArrayList<LinkedList<Node>> result = clusterList;
        for (int i = 0; i < mergeCount; i++) {
            result = singleMerge(result);
        }
        return result;
    }

    private ArrayList<LinkedList<Node>> singleMerge(ArrayList<LinkedList<Node>> clusterList) {
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
            mergeTwoClusters(clusters.get(i), clusters.get(index));
        }
        return getNewClusters();
    }

    /**
     * Prepares clusters for merging
     */
    public void initiateClustersForMerging() {
        for (int i = 0; i < clusterCount; i++) {
            clusters.get(i).offsprings = new LinkedList<>();
            clusters.get(i).offsprings.add(clusters.get(i));
            clusters.get(i).parent = clusters.get(i);
        }
    }

    protected void mergeTwoClusters(Cluster cluster1, Cluster cluster2) {
        if (cluster1.parent.id == cluster2.parent.id) {
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
            if (clusters.get(i).offsprings != null) {
                LinkedList<Node> list = new LinkedList<>();
                for (Cluster cluster : clusters.get(i).offsprings) {
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

}
