/*
 * Copyright (C) 2011-2016 clueminer.org
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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.clueminer.chameleon.Chameleon;
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
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * multi-objective merger (heap sorting). Uses {@link FrontHeapQueue}
 *
 * @author deric
 */
@ServiceProvider(service = Merger.class)
public class PairMergerMOH<E extends Instance, C extends GraphCluster<E>, P extends MoPair<E, C>> extends PairMergerMO<E, C, P> implements Merger<E> {

    public static final String NAME = "MOM-HS";

    protected FrontHeapQueue<E, C, P> queue;

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Generate all possible (unique) combinations of pairs
     * - complexity O(n^2) = O(n * (n-1) / 2)
     *
     * @param queue
     * @param pref
     */
    protected void fillQueue(FrontHeapQueue<E, C, P> queue, Props pref) {
        C c1, c2;
        P pair;
        int skip = 0;
        //generate all pairs
        for (int i = 0; i < clusters.size(); i++) {
            c1 = (C) clusters.get(i);
            for (int j = 0; j < i; j++) {
                c2 = (C) clusters.get(j);
                pair = (P) createPair(c1, c2, pref);
                //eliminate pair that won't be merged (doesn't share NN)
                if (pair.getObjective(0) != 0.0 && pair.getObjective(1) != 0.0) {
                    queue.add(pair);
                } else {
                    //else: don't even add such point to queue
                    skip++;
                }
            }
        }
        int debug = pref.getInt("debug", 0);
        if (debug > 0) {
            System.out.println("skippid " + skip + " pairs during merging initialization");
        }
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
        eval = mef.getProvider(pref.get(Chameleon.SORT_OBJECTIVE, ShatovskaSimilarity.name));

        queue = new FrontHeapQueue<>(pref.getInt(Chameleon.NUM_FRONTS, 5), blacklist, objectives, pref);
        int debug = pref.getInt("debug", 0);
        //initialize queue
        fillQueue(queue, pref);
        height = 0;
        HierarchicalResult result = new HClustResult(dataset, pref);

        level = 1;
        int numClusters = clusters.size();
        if (debug > 0) {
            System.out.println("total " + numClusters + ", queue size " + queue.size());
            System.out.println(queue.stats());
        }
        for (int i = 0; i < numClusters - 1; i++) {

            if (debug > 0) {
                if (i % 10 == 0) {
                    printQueue(queue, i);
                }
            }
            singleMerge(queue.poll(), pref, debug);

            //System.out.println("queue size: " + queue.size());
            //queue.filterOut();
            //System.out.println(queue);
        }

        DendroTreeData treeData = new DynamicClusterTreeData(nodes[2 * numClusters - 2]);
        treeData.createMapping(dataset.size(), treeData.getRoot(), nodes[2 * numClusters - 1]);
        result.setTreeData(treeData);
        return result;
    }

    protected void printQueue(Iterable<P> pairs, int step) {
        String file = "pareto-front-" + step + ".csv";
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            System.out.println("writing to file: " + file);
            //header
            StringBuilder sb = new StringBuilder();
            for (MergeEvaluation<E> obj : objectives) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(obj.getName());
            }
            sb.append(",").append(eval.getName()).append(",clusterIDs");
            writer.append(sb.append("\n"));
            sb.setLength(0);
            //data
            int i = 0;
            for (P pair : pairs) {
                System.out.println("pair " + i);
                for (int j = 0; j < objectives.size(); j++) {

                    if (j > 0) {
                        sb.append(",");
                    }
                    sb.append(pair.getObjective(j));
                }
                sb.append(",").append(pair.getSortObjective());
                sb.append(",").append(pair.A.getClusterId()).append("+").append(pair.B.getClusterId());
                writer.append(sb.append("\n"));
                sb.setLength(0);
                System.out.println(pair.toString());
                i++;
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected void singleMerge(P curr, Props pref, int debug) {
        if (debug > 1) {
            System.out.println("merging: [" + curr.A.getClusterId() + ", " + curr.B.getClusterId() + "] " + curr.toString());
        }
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
        for (MergeEvaluation<E> me : objectives) {
            me.clusterCreated(curr, newCluster, pref);
        }
        //eval.clusterCreated(curr, newCluster, pref);
        addIntoTree((MoPair<E, GraphCluster<E>>) curr, pref);
        updateExternalProperties(newCluster, curr.A, curr.B);
        addIntoQueue((C) newCluster, pref);
        //remove any pair containing merged items from current fronts
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
