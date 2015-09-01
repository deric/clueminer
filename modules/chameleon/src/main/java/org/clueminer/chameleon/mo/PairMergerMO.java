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
import org.clueminer.chameleon.AbstractMerger;
import org.clueminer.chameleon.Pair;
import org.clueminer.clustering.algorithm.HClustResult;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.factory.Clusters;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Node;
import org.clueminer.hclust.DynamicClusterTreeData;
import org.clueminer.partitioning.api.Merger;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = Merger.class)
public class PairMergerMO<E extends Instance> extends AbstractMerger<E> implements Merger<E> {

    private List<MergeEvaluation> objectives = new LinkedList<>();
    private static final String name = "multi-objective merger";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public HierarchicalResult getHierarchy(ArrayList<LinkedList<Node<E>>> clusterList, Dataset<E> dataset, Props pref) {
        Pair<Cluster>[] pairs = buildQueue(dataset);
        LinkedList<LinkedList<Pair<Cluster>>> fronts = NSGASort.sort(pairs, objectives, pref);

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

    public void addObjective(MergeEvaluation eval) {
        this.objectives.add(eval);
    }

    public void setObjectives(List<MergeEvaluation> list) {
        this.objectives = list;
    }

    public void removeObjective(MergeEvaluation eval) {
        this.objectives.remove(eval);
    }

}
