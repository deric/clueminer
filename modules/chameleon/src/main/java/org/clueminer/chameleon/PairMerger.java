package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.LinkedList;
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

        double max = Double.NEGATIVE_INFINITY;
        int index1 = 0;
        int index2 = 0;
        for (int i = 0; i < clusterCount; i++) {

            for (int j = 0; j < clusterCount; j++) {
                if (i == j) {
                    continue;
                }
                double value = computeSimilarity(i, j);
                if (value > max) {
                    max = value;
                    index1 = j;
                    index2 = i;
                }
            }
        }
        mergeTwoClusters(clusters.get(index2), clusters.get(index1));
        return getNewClusters();
    }

}
