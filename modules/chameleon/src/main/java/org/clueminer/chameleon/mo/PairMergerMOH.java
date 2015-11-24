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
import org.clueminer.chameleon.Chameleon;
import static org.clueminer.chameleon.Chameleon.SIM_MEASURE;
import org.clueminer.chameleon.GraphCluster;
import org.clueminer.chameleon.similarity.ShatovskaSimilarity;
import org.clueminer.clustering.algorithm.HClustResult;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.factory.MergeEvaluationFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Node;
import org.clueminer.hclust.DynamicClusterTreeData;
import org.clueminer.partitioning.api.Merger;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * multi-objective merger (heap sorting)
 *
 * @author deric
 */
@ServiceProvider(service = Merger.class)
public class PairMergerMOH<E extends Instance, C extends GraphCluster<E>, P extends MoPair<E, C>> extends PairMergerMO<E, C, P> implements Merger<E> {

    public static final String name = "MOM-HS";

    private FrontHeapQueue<E, C, P> queue;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public HierarchicalResult getHierarchy(Dataset<E> dataset, Props pref) {
        if (clusters.isEmpty()) {
            throw new RuntimeException("initialize() must be called first");
        }
        if (objectives.isEmpty()) {
            throw new RuntimeException("you must specify at least 2 objectives");
        }
        MergeEvaluationFactory mef = MergeEvaluationFactory.getInstance();
        eval = mef.getProvider(pref.get(SIM_MEASURE, ShatovskaSimilarity.name));
        ArrayList<P> pairs = createPairs(clusters.size(), pref);
        queue = new FrontHeapQueue<>(pref.getInt(Chameleon.NUM_FRONTS, 5), blacklist, objectives, pref);
        //initialize queue
        queue.addAll(pairs);
        height = 0;
        HierarchicalResult result = new HClustResult(dataset, pref);

        level = 1;
        int numClusters = clusters.size();
        System.out.println("total " + numClusters + ", queue size " + queue.size());
        System.out.println(queue.stats());
        for (int i = 0; i < numClusters - 1; i++) {
            singleMerge(queue.poll(), pref);
            //System.out.println("queue size: " + queue.size());
            //queue.filterOut();
            //System.out.println(queue);
        }

        DendroTreeData treeData = new DynamicClusterTreeData(nodes[2 * numClusters - 2]);
        treeData.createMapping(dataset.size(), treeData.getRoot(), nodes[2 * numClusters - 1]);
        result.setTreeData(treeData);
        return result;
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
        if (i == j) {
            throw new RuntimeException("Cannot merge two same clusters");
        }
        //System.out.println("merging: [" + curr.A.getClusterId() + ", " + curr.B.getClusterId() + "] " + curr.getValue());
        //System.out.println("   " + curr.toString());
        ArrayList<Node<E>> clusterNodes = (ArrayList<Node<E>>) curr.A.getNodes().clone();
        clusterNodes.addAll(curr.B.getNodes());

        GraphCluster<E> newCluster = new GraphCluster(clusterNodes, graph, clusters.size(), bisection, pref);
        clusters.add(newCluster);
        for (MergeEvaluation<E> eval : objectives) {
            eval.clusterCreated(curr, newCluster, pref);
        }
        //eval.clusterCreated(curr, newCluster, pref);
        addIntoTree((MoPair<E, GraphCluster<E>>) curr, pref);
        updateExternalProperties(newCluster, curr.A, curr.B);
        addIntoQueue((C) newCluster, pref);
        queue.filterOut();
    }

    private void addIntoQueue(C cluster, Props pref) {
        for (int i = 0; i < cluster.getClusterId(); i++) {
            if (!blacklist.contains(i)) {
                //System.out.println("adding pair [" + cluster.getClusterId() + ", " + clusters.get(i).getClusterId() + "]");
                queue.add((P) createPair((C) clusters.get(i), cluster, pref));
            }
        }
    }

}
