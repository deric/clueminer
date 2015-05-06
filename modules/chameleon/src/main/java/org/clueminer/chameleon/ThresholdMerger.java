package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;

/**
 * This class merges pairs of clusters exceeding given thresholds for relative
 * interconnectivity and closeness. Merging stops when there is no pair of
 * clusters exceeding the thresholds.
 *
 *
 * @author Tomas Bruna
 */
public class ThresholdMerger extends Merger {

    private final double RICThreshold;
    private final double RCLThreshold;
    private boolean merged;

    public ThresholdMerger(Graph g, Bisection bisection, double closenessPriority, double RICThreshold, double RCLThreshold) {
        super(g, bisection, closenessPriority);
        this.RICThreshold = RICThreshold;
        this.RCLThreshold = RCLThreshold;
    }

    public ArrayList<LinkedList<Node>> merge(ArrayList<LinkedList<Node>> clusterList) {
        ArrayList<LinkedList<Node>> result = clusterList;
        merged = true;
        while (merged) {
            merged = false;
            result = singleMerge(result);
        }
        return result;
    }

    private ArrayList<LinkedList<Node>> singleMerge(ArrayList<LinkedList<Node>> clusterList) {
        createClusters(clusterList, bisection);
        computeExternalProperties();
        initiateClustersForMerging();

        for (int i = 0; i < clusterCount; i++) {
            double maxRIC = Double.NEGATIVE_INFINITY;
            int index = -1;
            for (int j = 0; j < clusterCount; j++) {
                if (i == j) {
                    continue;
                }
                double RIC = getRIC(i, j);
                double RCL = getRCL(i, j);
                if (RIC > RICThreshold && RCL > RCLThreshold && RIC > maxRIC) {
                    maxRIC = RIC;
                    index = j;
                }
            }
            if (index != -1) {
                merged = true;
                mergeTwoClusters(clusters.get(i), clusters.get(index));
            }
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
            clusters.get(i).setParent(clusters.get(i));
        }
    }

    protected void mergeTwoClusters(Cluster cluster1, Cluster cluster2) {
        if (cluster1.getParent().getId() == cluster2.getParent().getId()) {
            return;
        }
        if (cluster1.getParent().offsprings.size() < cluster2.getParent().offsprings.size()) {
            Cluster temp = cluster1;
            cluster1 = cluster2;
            cluster2 = temp;
        }
        cluster1.getParent().offsprings.addAll(cluster2.getParent().offsprings);
        Cluster parent = cluster2.getParent();
        for (Cluster cluster : parent.offsprings) {
            cluster.setParent(cluster1.getParent());
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
                    for (Node node : cluster.getNodes()) {
                        list.add(node);
                    }
                }
                result.add(list);
            }
        }
        return result;
    }

}
