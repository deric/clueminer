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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.clueminer.chameleon.AbstractMerger;
import org.clueminer.chameleon.GraphCluster;
import org.clueminer.clustering.algorithm.HClustResult;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Node;
import org.clueminer.hclust.DTreeNode;
import org.clueminer.hclust.DynamicClusterTreeData;
import org.clueminer.partitioning.api.Merger;
import org.clueminer.utils.Pair;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = Merger.class)
public class PairMergerMO<E extends Instance> extends AbstractMerger<E> implements Merger<E> {

    private List<MergeEvaluation<E>> objectives = new LinkedList<>();
    private static final String name = "multi-objective merger";

    private FrontQueue<Pair<GraphCluster>> queue;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public HierarchicalResult getHierarchy(ArrayList<LinkedList<Node<E>>> clusterList, Dataset<E> dataset, Props pref) {
        blacklist = new HashSet<>(clusterList.size() * clusterList.size() + clusterList.size());
        Pair<GraphCluster<E>>[] pairs = buildQueue(clusterList, dataset);
        NSGASort<E, GraphCluster<E>> sorter = new NSGASort<>();
        LinkedList fronts = sorter.sort(pairs, objectives, pref);

        queue = new FrontQueue<>(fronts);
        nodes = initiateTree(clusterList);
        height = 0;
        HierarchicalResult result = new HClustResult(dataset, pref);

        level = 1;
        for (int i = 0; i < clusterList.size() - 1; i++) {
            singleMerge(queue.poll(), pref);
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

    private Pair<GraphCluster<E>>[] buildQueue(ArrayList<LinkedList<Node<E>>> clusterList, Dataset<E> dataset) {
        Pair<GraphCluster<E>>[] allPairs = new Pair[triangleSize(clusterList.size())];
        GraphCluster<E> c1, c2;
        int n = 0;
        //generate all pairs
        for (int i = 0; i < clusterList.size(); i++) {
            c1 = new GraphCluster(clusterList.get(i), graph, clusterCount++, bisection);
            for (int j = 0; j < i; j++) {
                c2 = new GraphCluster(clusterList.get(j), graph, clusterCount++, bisection);
                Pair p = new Pair(c1, c2);
                allPairs[n++] = p;
            }
        }
        return allPairs;
    }

    private void singleMerge(Pair<GraphCluster> curr, Props pref) {
        int i = curr.A.getClusterId();
        int j = curr.B.getClusterId();
        while (blacklist.contains(i) || blacklist.contains(j)) {
            curr = queue.poll();
            i = curr.A.getClusterId();
            j = curr.B.getClusterId();
        }
        blacklist.add(i);
        blacklist.add(j);
        if (curr.A.getClusterId() == curr.B.getClusterId()) {
            throw new RuntimeException("Cannot merge two same clusters");
        }
        LinkedList<Node> clusterNodes = curr.A.getNodes();
        clusterNodes.addAll(curr.B.getNodes());

        GraphCluster<E> newCluster = new GraphCluster(clusterNodes, graph, clusterCount++, bisection);
        //evaluation.clusterCreated(curr, newCluster, pref);
        addIntoTree(curr, pref);
        clusters.add(newCluster);
        updateExternalProperties(newCluster, curr.A, curr.B);
        //addIntoQueue(newCluster, pref);
    }

    /**
     * Adds node representing new cluster (the one created by merging) to
     * dendroTree
     *
     * @param pair
     * @param pref
     */
    protected void addIntoTree(Pair<GraphCluster> pair, Props pref) {
        DendroNode left = nodes[pair.A.getClusterId()];
        DendroNode right = nodes[pair.B.getClusterId()];
        DTreeNode newNode = new DTreeNode(clusterCount - 1);
        newNode.setLeft(left);
        newNode.setRight(right);
        double sim = Double.NaN; //TODO: which objective do we use?
        if (sim > 10) {
            sim = 10;
        }
        if (sim < 0.005) {
            sim = 0.005;
        }
        height += 1 / sim;
        newNode.setHeight(height);
        newNode.setLevel(level++);
        nodes[clusterCount - 1] = newNode;
    }

    public void addObjective(MergeEvaluation eval) {
        this.objectives.add(eval);
    }

    public void setObjectives(List<MergeEvaluation<E>> list) {
        this.objectives = list;
    }

    public void removeObjective(MergeEvaluation eval) {
        this.objectives.remove(eval);
    }

}
