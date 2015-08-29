/*
 * Copyright (C) 2011-2015 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.chameleon.mo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.clueminer.chameleon.Merger;
import org.clueminer.clustering.algorithm.HClustResult;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.factory.Clusters;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.hclust.DynamicClusterTreeData;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 */
public class PairMergerMO extends Merger {

    protected DendroNode[] nodes;

    int level;

    protected double height;

    public PairMergerMO(Graph g, Bisection bisection, double closenessPriority) {
        super(g, bisection, closenessPriority);
    }

    public HierarchicalResult getHierarchy(ArrayList<LinkedList<Node>> clusterList, Dataset<? extends Instance> dataset, Props pref) {
        createClusters(clusterList, bisection);
        computeExternalProperties();
        Pair<Cluster>[] pairs = buildQueue(dataset);
        List<MergeEvaluation> objectives = new LinkedList<>();
        LinkedList<LinkedList<Pair<Cluster>>> fronts = NSGASort.sort(pairs, objectives);

        FrontQueue<Pair<Cluster>> queue = new FrontQueue<>(fronts);
        nodes = initiateTree(clusterList);
        height = 0;
        HierarchicalResult result = new HClustResult(dataset, pref);

        level = 1;
        for (int i = 0; i < clusterList.size() - 1; i++) {
            singleMerge(queue.poll());
        }

        DendroTreeData treeData = new DynamicClusterTreeData(nodes[2 * clusterList.size() - 2]);
        treeData.createMapping(dataset.size(), treeData.getRoot());
        result.setTreeData(treeData);
        return result;
    }

    /**
     * Compute size of triangular matrix (n x n) minus diagonal
     *
     * @param n
     * @return
     */
    protected int triangleSize(int n) {
        return ((n - 1) * n) >>> 1;
    }

    private Pair<Cluster>[] buildQueue(Dataset<? extends Instance> dataset) {
        Pair<Cluster>[] allPairs = new Pair[triangleSize(dataset.size())];
        Cluster c1, c2;
        int n = 0;
        //generate all pairs
        for (int i = 0; i < clusterCount; i++) {
            for (int j = 0; j < i; j++) {
                c1 = Clusters.newInst();
                c1.add(dataset.get(i));
                c2 = Clusters.newInst();
                c2.add(dataset.get(j));
                Pair p = new Pair(c1, c2);
                allPairs[n++] = p;
            }
        }
        return allPairs;
    }

    private void singleMerge(Pair<Cluster> pair) {

    }

}
