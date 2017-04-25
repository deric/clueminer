/*
 * Copyright (C) 2011-2017 clueminer.org
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
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import org.clueminer.chameleon.AbstractMerger;
import org.clueminer.chameleon.Chameleon;
import org.clueminer.chameleon.GraphCluster;
import org.clueminer.chameleon.similarity.ShatovskaSimilarity;
import org.clueminer.clustering.algorithm.HClustResult;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.factory.MergeEvaluationFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Node;
import org.clueminer.hclust.DTreeNode;
import org.clueminer.hclust.DynamicClusterTreeData;
import org.clueminer.partitioning.api.Merger;
import org.clueminer.utils.PairValue;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * Approximated merger, compares item to only best front's solution.
 *
 * @author deric
 */
@ServiceProvider(service = Merger.class)
public class PairMergerMO<E extends Instance, C extends GraphCluster<E>, P extends MoPair<E, C>> extends AbstractMerger<E> implements Merger<E> {

    protected List<MergeEvaluation<E>> objectives = new LinkedList<>();
    public static final String NAME = "multi-objective merger";

    protected AbstractQueue<E, C, P> queue;
    protected MergeEvaluation eval;

    @Override
    public String getName() {
        return NAME;
    }

    protected void initQueue(Props pref) {
        queue = new FhQueue<>(pref.getInt(Chameleon.NUM_FRONTS, 5), blacklist, objectives, pref);
    }

    protected void fillQueue(AbstractQueue<E, C, P> queue, Props pref) {
        ArrayList<P> pairs = createPairs(clusters.size(), pref);
        queue.addAll(pairs);
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
        eval = mef.getProvider(pref.get(Chameleon.SORT_OBJECTIVE, ShatovskaSimilarity.NAME));

        //initialize queue
        initQueue(pref);
        //add items into queue
        fillQueue(queue, pref);

        height = 0;
        HierarchicalResult result = new HClustResult(dataset, pref);

        level = 1;
        int numClusters = clusters.size();
        int debug = pref.getInt("debug", 0);
        if (debug > 0) {
            System.out.println("total " + numClusters + ", queue size " + queue.size());
            System.out.println(queue.stats());
        }
        for (int i = 0; i < numClusters - 1; i++) {
            if (debug > 1) {
                if (i % 10 == 0) {
                    printQueue(queue, i);
                }
            }
            singleMerge(queue.poll(), pref, debug);
        }

        DendroTreeData treeData = new DynamicClusterTreeData(nodes[2 * numClusters - 2]);
        treeData.createMapping(dataset.size(), treeData.getRoot(), nodes[2 * numClusters - 1]);
        result.setTreeData(treeData);
        return result;
    }

    protected ArrayList<P> createPairs(int numClusters, Props pref) {
        ArrayList<P> allPairs = new ArrayList<>(triangleSize(numClusters));
        C c1, c2;
        //generate all pairs
        for (int i = 0; i < numClusters; i++) {
            c1 = (C) clusters.get(i);
            for (int j = 0; j < i; j++) {
                c2 = (C) clusters.get(j);
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

    protected void addIntoQueue(C cluster, Props pref) {
        for (int i = 0; i < cluster.getClusterId(); i++) {
            if (!blacklist.contains(i)) {
                //System.out.println("adding pair [" + cluster.getClusterId() + ", " + clusters.get(i).getClusterId() + "]");
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
    protected MoPair<E, C> createPair(C a, C b, Props pref) {
        P pair = (P) new MoPair<>(a, b, objectives.size(), eval);
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
    protected void addIntoTree(MoPair<E, GraphCluster<E>> pair, Props pref) {
        DendroNode left = nodes[pair.A.getClusterId()];
        DendroNode right = nodes[pair.B.getClusterId()];
        DTreeNode newNode = new DTreeNode(clusters.size() - 1);
        newNode.setLeft(left);
        newNode.setRight(right);
        /* double sim = 0.0;
         * double val;
         * for (int i = 0; i < objectives.size(); i++) {
         * //TODO: we might multiply objectives or use another criteria for building tree
         * val = pair.getObjective(i);
         * if (!Double.isNaN(val)) {
         * sim += val; }
         * } */
        double sim = eval.score(pair.A, pair.B, pref);
        if (sim > 10) {
            sim = 10;
        }
        if (sim < 0.005) {
            sim = 0.005;
        }
        height += 1.0 / sim;
        newNode.setHeight(height);
        newNode.setLevel(level++);
        nodes[clusters.size() - 1] = newNode;
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

    public void clearObjectives() {
        this.objectives.clear();
    }

    public void setSortEvaluation(MergeEvaluation evaluation) {
        this.eval = evaluation;
    }

    @Override
    public PriorityQueue getQueue(Props pref) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void prefilter(Clustering<E, GraphCluster<E>> clusters, ArrayList<E> noise, Props params) {
        //
    }

    @Override
    public void finalize(Clustering<E, GraphCluster<E>> clusters, PriorityQueue<PairValue<GraphCluster<E>>> pq, Dataset<E> dataset) {
        //nothing to do
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
            for (P pair : pairs) {
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
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public boolean isMultiObjective() {
        return true;
    }

}
