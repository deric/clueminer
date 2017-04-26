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
package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import org.clueminer.clustering.algorithm.HClustResult;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Node;
import org.clueminer.hclust.DClusterLeaf;
import org.clueminer.hclust.DynamicClusterTreeData;
import org.clueminer.partitioning.api.Merger;
import org.clueminer.utils.PairValue;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Merger with built in noise detection
 *
 * @author deric
 * @param <E>
 */
@ServiceProvider(service = Merger.class)
public class NDMerger<E extends Instance> extends PairMerger<E> implements Merger<E> {

    protected PriorityQueue<PairValue<GraphCluster<E>>> pq;

    protected MergeEvaluation evaluation;
    private ArrayList<E> noise;
    private static final Logger LOG = LoggerFactory.getLogger(NDMerger.class);

    public static final String NAME = "ND merger";

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Merge clusters while creating a hierarchical structure (dendrogram)
     *
     * @param dataset
     * @param pref
     * @return
     */
    @Override
    public HierarchicalResult getHierarchy(Dataset<E> dataset, Props pref) {
        if (clusters.isEmpty()) {
            throw new RuntimeException("initialize() must be called first");
        }
        int numClusters = clusters.size();
        int clusterId = buildQueue(clusters.size(), pref);
        LOG.debug("built merging queue, last ID: {}", clusterId);
        height = 0;
        HierarchicalResult result = new HClustResult(dataset, pref);
        noise = new ArrayList<>();

        level = 1;
        int i = 0;
        //number of initial clusters
        //for (int i = 0; i < numClusters - 1; i++) {
        int ret;
        while (!pq.isEmpty() && i < numClusters - 1) {
            ret = singleMerge(pq.poll(), pref, clusterId);
            i += ret;
            clusterId += ret;
        }

        if (noise.size() > 0) {
            LOG.debug("curr dendrogram nodes: {}, clusters: {}, last cluster ID: {}", nodes.length, clusters.size(), clusterId);
            // remove empty tree nodes
            LOG.debug("last node in tree: {}", nodes[clusters.size() - 1]);
            DendroNode[] tmp = new DendroNode[clusters.size() + 1];
            LOG.debug("updated dendrogram nodes: {}", tmp.length);
            System.arraycopy(nodes, 0, tmp, 0, tmp.length);
            nodes = tmp;
            nodes[nodes.length - 1] = new DClusterLeaf(clusters.size(), noise);
            nodes[nodes.length - 1].setHeight(0);
            nodes[nodes.length - 1].setLevel(0);
            result.setNoise(noise);
            /* GraphCluster<E> cnoise = new GraphCluster(noise, graph, clusterId, bisection, pref);
             * cnoise.setName(Algorithm.OUTLIER_LABEL);
             * clusters.add(cnoise); */
        }
        finalize(clusters, pq, dataset);

        LOG.debug("creating tree with {} leaves", nodes.length);
        //LOG.debug("tree root: {}, root level: {}", nodes[nodes.length - 2], nodes[nodes.length - 2].level());

        //getGraphPropertyStore(clusters.get(0)).dump();
        DendroTreeData treeData = new DynamicClusterTreeData(nodes[nodes.length - 2], dataset.size());
        int[] mapping = treeData.createMapping(dataset.size(), treeData.getRoot(), nodes[nodes.length - 1]);
        LOG.debug("dataset size: {}, mapping size: {}, noise size: {}", dataset.size(), mapping.length, noise.size());

        treeData.updatePositions(treeData.getRoot());
        result.setTreeData(treeData);
        return result;
    }

    /**
     * Merges two most similar clusters
     *
     * @param curr
     * @param pref
     * @param newClusterId
     * @return 1 when merge is successful
     */
    @Override
    protected int singleMerge(PairValue<GraphCluster<E>> curr, Props pref, int newClusterId) {
        int i = curr.A.getClusterId();
        int j = curr.B.getClusterId();
        while (blacklist.contains(i) || blacklist.contains(j)) {
            if (!pq.isEmpty()) {
                curr = pq.poll();
            } else {
                LOG.warn("emptied queue earlier than expected");
                return 0;
            }
            i = curr.A.getClusterId();
            j = curr.B.getClusterId();
        }
        if (i == j) {
            throw new RuntimeException("Cannot merge two same clusters");
        }
        double x, y, tmp;
        GraphCluster<E> potentialNoise, notNoise;

        //double ec1 = curr.A.getEdgeCount();
        //double ec2 = curr.B.getEdgeCount();
        //double common = gps.getCnt(i, j);
        x = curr.A.getACL();
        y = curr.B.getACL();
        if (x > y) {
            potentialNoise = curr.A;
            notNoise = curr.B;
        } else {
            tmp = x;
            x = y;
            y = tmp;
            potentialNoise = curr.B;
            notNoise = curr.A;
        }
        if (y > 0) {
            double ratio = x / y;
            double noiseRatioThreshold = pref.getDouble(Chameleon.NOISE_RATIO, 10.0);
            LOG.debug("CL ratio {}", ratio);

            // check for noisy clusters
            if (ratio > noiseRatioThreshold) {
                LOG.debug(">> noise weight A: {} B: {}", x, y);
                LOG.debug(">> cluster {} ({}) marked as noise, not noise: {} ({})",
                        potentialNoise.getClusterId(), x, notNoise.getClusterId(), y);
                addToNoise(potentialNoise, potentialNoise.getClusterId());
                //addIntoQueue(notNoise, pref);
                //clusters.remove(noiseId);
                return 0;
            } else {
                merge(i, j, curr, pref, newClusterId);
                return 1; //
            }
        } else {
            merge(i, j, curr, pref, newClusterId);
            return 1;
        }
    }

    private void addToNoise(GraphCluster<E> potentialNoise, int noiseId) {
        // mark noisy cluster as processed
        blacklist.add(noiseId);
        for (E inst : potentialNoise) {
            noise.add(inst);
            //clusters.getNoise().add(inst);
        }
        LOG.debug(">> removing cluster {}, size: {}", noiseId, potentialNoise.size());
    }

    private void merge(int i, int j, PairValue<GraphCluster<E>> curr, Props pref, int newClusterId) {
        LOG.debug("merging A: {} B: {} -> {}", curr.A.getClusterId(), curr.B.getClusterId(), newClusterId);
        LOG.debug("avg weight A: {} B: {}", curr.A.getACL(), curr.B.getACL());
        blacklist.add(i);
        blacklist.add(j);
        //normal data
        //clonning won't be necessary if we don't wanna recompute RCL for clusters that were merged
        //LinkedList<Node> clusterNodes = (LinkedList<Node>) curr.A.getNodes().clone();
        //WARNING: we copy nodes from previous clusters (we save memory, but
        //it's not a good idea to work with merged clusters)
        ArrayList<Node<E>> clusterNodes = curr.A.getNodes();
        clusterNodes.addAll(curr.B.getNodes());
        merged(curr);
        GraphCluster<E> newCluster = new GraphCluster(clusterNodes, graph, newClusterId, bisection, pref);
        clusters.add(newCluster);
        evaluation.clusterCreated(curr, newCluster, pref);
        addIntoTree(curr, pref);
        updateExternalProperties(newCluster, curr.A, curr.B);
        addIntoQueue(newCluster, pref);
    }

    protected void merged(PairValue<GraphCluster<E>> curr) {
        //nothing to do
    }

    /**
     * Computes similarities between the merged and other active clusters and
     * adds them into the priority queue. The merged cluster is the last one in
     * both cluster array and external properties matrix, therefore we use index
     * clusterCount -1.
     *
     * @param cluster
     * @param pref
     */
    protected void addIntoQueue(GraphCluster<E> cluster, Props pref) {
        double sim;
        GraphCluster a;
        for (int i = 0; i < cluster.getClusterId(); i++) {
            if (!blacklist.contains(i)) {
                a = clusters.get(i);
                sim = evaluation.score(a, cluster, pref);
                pq.add(new PairValue<GraphCluster<E>>(a, cluster, sim));
            }
        }
    }

    /**
     * Computes similarities between all clusters and adds them into the
     * priority queue.
     *
     *
     * @param numClusters
     * @param pref
     * @return ID of next cluster
     */
    protected int buildQueue(int numClusters, Props pref) {
        int capacity = numClusters * numClusters;
        pq = initQueue(capacity);
        double sim;
        GraphCluster a, b;
        for (int i = 0; i < numClusters; i++) {
            a = clusters.get(i);
            for (int j = 0; j < i; j++) {
                b = clusters.get(j);
                sim = evaluation.score(a, b, pref);
                pq.add(new PairValue<GraphCluster<E>>(a, b, sim));
            }
        }
        return clusters.size();
    }

    protected PriorityQueue<PairValue<GraphCluster<E>>> initQueue(int capacity) {
        PriorityQueue<PairValue<GraphCluster<E>>> queue;
        if (evaluation.isMaximized()) {
            Comparator<PairValue<GraphCluster<E>>> comp = new Comparator<PairValue<GraphCluster<E>>>() {

                @Override
                public int compare(PairValue<GraphCluster<E>> o1, PairValue<GraphCluster<E>> o2) {
                    return o1.compareTo(o2);
                }

            };
            queue = new PriorityQueue<>(capacity, comp);
        } else {
            //inverse sorting - smallest values first
            Comparator<PairValue<GraphCluster<E>>> comp = new Comparator<PairValue<GraphCluster<E>>>() {

                @Override
                public int compare(PairValue<GraphCluster<E>> o1, PairValue<GraphCluster<E>> o2) {
                    return o2.compareTo(o1);
                }

            };
            queue = new PriorityQueue<>(capacity, comp);
        }
        return queue;
    }

    public void setMergeEvaluation(MergeEvaluation eval) {
        this.evaluation = eval;
    }

    @Override
    public PriorityQueue getQueue(Props pref) {
        buildQueue(clusters.size(), pref);
        return pq;
    }

    @Override
    public void prefilter(Clustering<E, GraphCluster<E>> clusters, ArrayList<E> noise, Props params) {
        if (params != null && params.getInt(Chameleon.NOISE_DETECTION, 0) == Chameleon.NOISE_INTERNAL_PROPERTIES) {
            noise = identifyNoise(clusters, params);
            renumberClusters(clusters, noise);
        }
    }

    private ArrayList<E> identifyNoise(Clustering<E, GraphCluster<E>> clusters, Props params) {
        double median = computeMedianCl(clusters);
        ArrayList<E> noise = null;
        for (int i = 0; i < clusters.size(); i++) {
            if (clusters.get(i).getACL() < median / params.getDouble(Chameleon.INTERNAL_NOISE_THRESHOLD, 2)) {
                if (noise == null) {
                    noise = new ArrayList<>();
                }
                for (Node<E> node : clusters.get(i).getNodes()) {
                    noise.add(node.getInstance());
                }
                clusters.remove(i);
                i--;
            }
        }
        return noise;
    }

    public void finalize(Clustering<E, GraphCluster<E>> clusters, PriorityQueue<PairValue<GraphCluster<E>>> pq, Dataset<E> dataset) {
        // when noise present
        renumberClusters(clusters, noise);
    }

    @Override
    public boolean isMultiObjective() {
        return false;
    }
}
