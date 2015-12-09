package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import org.clueminer.clustering.algorithm.HClustResult;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.factory.Clusterings;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Node;
import org.clueminer.hclust.DynamicClusterTreeData;
import org.clueminer.partitioning.api.Merger;
import org.clueminer.utils.PairValue;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * This class merges two clusters in one merge. Two most similar clusters among
 * all pairs are merged at each step.
 *
 * @author Tomas Bruna
 * @param <E>
 */
@ServiceProvider(service = Merger.class)
public class PairMerger<E extends Instance> extends AbstractMerger<E> implements Merger<E> {

    protected PriorityQueue<PairValue<GraphCluster<E>>> pq;

    protected MergeEvaluation evaluation;

    public static final String name = "pair merger";

    @Override
    public String getName() {
        return name;
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
        height = 0;
        HierarchicalResult result = new HClustResult(dataset, pref);

        level = 1;
        int i = 0;
        //number of initial clusters
        //for (int i = 0; i < numClusters - 1; i++) {
        while (!pq.isEmpty() && i < numClusters - 1) {
            singleMerge(pq.poll(), pref, clusterId);
            clusterId++;
            i++;
        }

        finalize(clusters, pq);

        //getGraphPropertyStore(clusters.get(0)).dump();
        DendroTreeData treeData = new DynamicClusterTreeData(nodes[nodes.length - 2]);
        treeData.createMapping(dataset.size(), treeData.getRoot(), nodes[nodes.length - 1]);
        result.setTreeData(treeData);
        return result;
    }

    /**
     * Merges two most similar clusters
     *
     * @param curr
     * @param pref
     * @param newClusterId
     */
    protected void singleMerge(PairValue<GraphCluster<E>> curr, Props pref, int newClusterId) {
        int i = curr.A.getClusterId();
        int j = curr.B.getClusterId();
        while (!pq.isEmpty() && (blacklist.contains(i) || blacklist.contains(j))) {
            curr = pq.poll();
            i = curr.A.getClusterId();
            j = curr.B.getClusterId();
        }
        blacklist.add(i);
        blacklist.add(j);
        if (i == j) {
            throw new RuntimeException("Cannot merge two same clusters");
        }
        //System.out.println("merging: " + curr.getValue() + " A: " + curr.A.getClusterId() + " B: " + curr.B.getClusterId());
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

    /**
     * Returns lists of nodes in each cluster. Used only for graph printing, the
     * real result is stored in the tree.
     *
     * @return
     */
    private ArrayList<ArrayList<Node<E>>> getResult() {
        ArrayList<ArrayList<Node<E>>> result = new ArrayList<>();
        for (int i = 0; i < clusters.size(); i++) {
            if (blacklist.contains(i)) {
                continue;
            }
            result.add(clusters.get(i).getNodes());
        }
        return result;
    }

    private Clustering getClusterResult() {
        Clustering output = Clusterings.newList(clusters.size());
        int i = 0;
        for (GraphCluster<E> g : clusters) {
            Cluster<E> cluster = output.createCluster(i++, g.getNodeCount());
            for (Node<E> node : g.getNodes()) {
                cluster.add(node.getInstance());
            }
            output.add(cluster);
        }
        return output;
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

    @Override
    public void finalize(Clustering<E, GraphCluster<E>> clusters, PriorityQueue<PairValue<GraphCluster<E>>> pq) {
        //nothing to do
    }

}
