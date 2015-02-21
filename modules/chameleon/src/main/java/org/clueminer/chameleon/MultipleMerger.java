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

}
