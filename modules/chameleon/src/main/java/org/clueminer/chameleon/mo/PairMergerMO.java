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
public class PairMergerMO<E extends Instance, C extends GraphCluster<E>, P extends MoPair<C>> extends AbstractMerger<E> implements Merger<E> {

    private List<MergeEvaluation<E>> objectives = new LinkedList<>();
    private static final String name = "multi-objective merger";

    private FrontQueue<E, C, P> queue;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public HierarchicalResult getHierarchy(ArrayList<LinkedList<Node<E>>> clusterList, Dataset<E> dataset, Props pref) {
        blacklist = new HashSet<>(clusterList.size() * clusterList.size() + clusterList.size());
        ArrayList<P> pairs = buildQueue(clusterList, pref);

        queue = new FrontQueue<>(pairs, objectives, pref);
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

    private ArrayList<P> buildQueue(ArrayList<LinkedList<Node<E>>> clusterList, Props pref) {
        ArrayList<P> allPairs = new ArrayList<>(triangleSize(clusterList.size()));
        C c1, c2;
        //generate all pairs
        for (int i = 0; i < clusterList.size(); i++) {
            c1 = (C) new GraphCluster(clusterList.get(i), graph, clusterCount++, bisection);
            for (int j = 0; j < i; j++) {
                c2 = (C) new GraphCluster(clusterList.get(j), graph, clusterCount++, bisection);
                allPairs.add((P) createPair(c1, c2, pref));
            }
        }
        return allPairs;
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

    private void singleMerge(P curr, Props pref) {
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
        LinkedList<Node<E>> clusterNodes = curr.A.getNodes();
        clusterNodes.addAll(curr.B.getNodes());

        GraphCluster<E> newCluster = new GraphCluster(clusterNodes, graph, clusterCount++, bisection);
        //evaluation.clusterCreated(curr, newCluster, pref);
        addIntoTree((Pair<GraphCluster<E>>) curr, pref);
        clusters.add(newCluster);
        updateExternalProperties(newCluster, curr.A, curr.B);
        addIntoQueue((C) newCluster, pref);
    }

    private void addIntoQueue(C cluster, Props pref) {
        for (int i = 0; i < cluster.getClusterId(); i++) {
            if (!blacklist.contains(i)) {
                queue.add((P) createPair((C) clusters.get(i), cluster, pref));
            }
        }
    }

    /**
     * Create a pair of clusters and pre-computes all objectives
     *
     * @param a
     * @param b
     * @param pref
     * @return
     */
    private MoPair createPair(C a, C b, Props pref) {
        P pair = (P) new MoPair<>(a, b, objectives.size());
        double sim;
        for (int j = 0; j < objectives.size(); j++) {
            sim = objectives.get(j).score(a, b, pref);
            pair.setObjective(j, sim);
        }
        return pair;
    }

    /**
     * Adds node representing new cluster (the one created by merging) to
     * dendroTree
     *
     * @param pair
     * @param pref
     */
    protected void addIntoTree(Pair<GraphCluster<E>> pair, Props pref) {
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
