package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.hclust.DynamicClusterTreeData;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.Merger;
import org.clueminer.utils.Props;

/**
 * This class merges two clusters in one merge. Two most similar clusters among
 * all pairs are merged at each step.
 *
 * @author Tomas Bruna
 * @param <E>
 */
public class PairMerger<E extends Instance> extends AbstractMerger<E> implements Merger<E> {

    protected PriorityQueue<PairValue<GraphCluster>> pq;

    protected MergeEvaluation evaluation;

    private static final String name = "pair merger";

    public PairMerger() {

    }

    public PairMerger(Graph g, Bisection bisection, MergeEvaluation eval) {
        super(g, bisection);
        this.evaluation = eval;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Merge clusters while creating a hierarchical structure (dendrogram)
     *
     * @param clusterList
     * @param dataset
     * @param pref
     * @return
     */
    @Override
    public HierarchicalResult getHierarchy(ArrayList<LinkedList<Node<E>>> clusterList, Dataset<E> dataset, Props pref) {
        blacklist = new HashSet<>();
        createClusters(clusterList, bisection);
        computeExternalProperties();
        buildQueue(clusterList, pref);
        nodes = initiateTree(clusterList);
        height = 0;
        HierarchicalResult result = new HClustResult(dataset, pref);

        level = 1;
        for (int i = 0; i < clusterList.size() - 1; i++) {
            singleMerge(pq.poll(), pref);
        }
        //getGraphPropertyStore(clusters.get(0)).dump();
        DendroTreeData treeData = new DynamicClusterTreeData(nodes[2 * clusterList.size() - 2]);
        treeData.createMapping(dataset.size(), treeData.getRoot());
        result.setTreeData(treeData);
        return result;
    }

    /**
     * Merges two most similar clusters
     *
     * @param clusterList
     */
    private void singleMerge(PairValue<GraphCluster> curr, Props pref) {
        int i = curr.A.getClusterId();
        int j = curr.B.getClusterId();
        while (!pq.isEmpty() && (blacklist.contains(i) || blacklist.contains(j))) {
            curr = pq.poll();
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
        addIntoTree(curr, pref);
        GraphCluster<E> newCluster = new GraphCluster(clusterNodes, graph, clusterCount++, bisection);
        evaluation.clusterCreated(curr.A, curr.B, newCluster);
        clusters.add(newCluster);
        updateExternalProperties(newCluster, curr.A, curr.B);
        addIntoQueue(newCluster, pref);
    }

    /**
     * Computes similarities between the merged and other active clusters and
     * adds them into the priority queue. The merged cluster is the last one in
     * both cluster array and external properties matrix, therefore we use index
     * clusterCount -1.
     */
    private void addIntoQueue(GraphCluster<E> cluster, Props pref) {
        double sim;
        GraphCluster a;
        for (int i = 0; i < cluster.getClusterId(); i++) {
            if (!blacklist.contains(i)) {
                a = clusters.get(i);
                sim = evaluation.score(a, cluster, pref);
                pq.add(new PairValue<>(a, cluster, sim));
            }
        }
    }

    /**
     * Computes similarities between all clusters and adds them into the
     * priority queue.
     */
    private void buildQueue(ArrayList<LinkedList<Node<E>>> clusterList, Props pref) {
        if (!evaluation.isMaximized()) {
            pq = new PriorityQueue<PairValue<GraphCluster>>();
        } else {
            //inverse sorting - biggest values first
            Comparator<PairValue<GraphCluster>> comp = new Comparator<PairValue<GraphCluster>>() {

                @Override
                public int compare(PairValue<GraphCluster> o1, PairValue<GraphCluster> o2) {
                    return o2.compareTo(o1);
                }

            };
            pq = new PriorityQueue<PairValue<GraphCluster>>(comp);
        }
        double sim;
        GraphCluster a, b;
        for (int i = 0; i < clusterList.size(); i++) {
            for (int j = 0; j < i; j++) {
                a = clusters.get(i);
                b = clusters.get(j);
                sim = evaluation.score(a, b, pref);
                pq.add(new PairValue<>(a, b, sim));
            }
        }
    }

    /**
     * Returns lists of nodes in each cluster. Used only for graph printing, the
     * real result is stored in the tree.
     *
     * @return
     */
    private ArrayList<LinkedList<Node<E>>> getResult() {
        ArrayList<LinkedList<Node<E>>> result = new ArrayList<>();
        for (int i = 0; i < clusterCount; i++) {
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

}
