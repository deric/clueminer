package org.clueminer.clustering.aggl;

import java.util.AbstractQueue;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.hclust.DTreeNode;
import org.clueminer.hclust.DynamicTreeData;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

/**
 * Hierarchical clustering - updating distances using Lance-Williams update
 * formula
 *
 * memory saving version:
 * <li>
 * <ul>double array (n - 1) * n / 2 - for storing similarity matrix</ul>
 * <ul>tree structure O(n)</ul>
 * <ul>priority queue O(n^2)</ul>
 * </li>
 * time complexity - O(n^2)
 *
 * Lance, G. N. and Williams, W. T.. "A general theory of classificatory sorting
 * strategies 1. Hierarchical systems." The Computer Journal 9 , no. 4 (1967):
 * 373-380.
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class HCLWMS<E extends Instance, C extends Cluster<E>> extends HC<E, C> implements AgglomerativeClustering<E, C> {

    private final static String NAME = "HAC-LW-MS";
    private static final Logger logger = Logger.getLogger(HCLWMS.class.getName());

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Find most closest items and merges them into one cluster (subtree)
     *
     * @param pq
     * @param similarityMatrix
     * @param dataset
     * @param params
     * @return
     */
    @Override
    protected DendroTreeData computeLinkage(AbstractQueue<Element> pq, Matrix similarityMatrix, Dataset<E> dataset, AgglParams params, int n) {
        //binary tree, however we store at most n nodes (then rewrite references)
        DendroNode[] nodes = new DendroNode[n];
        //each instance will form a cluster
        Map<Integer, Set<Integer>> assignments = initialAssignment(n, dataset, params, nodes);

        Element curr;
        DendroNode node = null;
        Set<Integer> left, right;
        int nodeId = n;
        int ma, mb;
        /**
         * queue of distances, each time join 2 items together, we should remove
         * (n-1) items from queue (but removing is too expensive)
         */
        while (!pq.isEmpty() && assignments.size() > 1) {
            curr = pq.poll();
            //System.out.println(curr.toString() + " remain: " + pq.size() + ", height: " + String.format("%.2f", curr.getValue()));
            if (curr.getValue() == similarityMatrix.get(curr.getRow(), curr.getColumn())
                    && assignments.containsKey(curr.getRow()) && assignments.containsKey(curr.getColumn())) {
                node = new DTreeNode(nodeId++);
                node.setLeft(nodes[curr.getRow()]);
                node.setRight(nodes[curr.getColumn()]);
                node.setHeight(curr.getValue());

                //replace ref to merged nodes by its parent (merged node)
                nodes[curr.getRow()] = node;
                nodes[curr.getColumn()] = node;

                //remove old clusters
                left = assignments.remove(curr.getRow());
                ma = left.size();
                right = assignments.remove(curr.getColumn());
                mb = right.size();
                //merge together and add as a new cluster
                left.addAll(right);
                updateDistances(similarityMatrix, assignments, pq, params.getLinkage(), curr.getRow(), curr.getColumn(), ma, mb);
                //when assignment have size == 1, all clusters are merged into one
                //finaly add merged cluster
                assignments.put(curr.getRow(), left);
            }
        }
        logger.log(Level.INFO, "{0} pq size: {1}", new Object[]{getName(), pq.size()});
        //last node is the root
        DendroTreeData treeData = new DynamicTreeData(node);
        return treeData;
    }

    /**
     * When we merge two items and create a new dendrogram node, we have to
     * update distances to all other nodes (clusters)
     *
     * @param similarityMatrix
     * @param assignments
     * @param pq
     * @param linkage
     * @param leftId
     * @param rightId
     * @param ma
     * @param mb
     */
    protected void updateDistances(Matrix similarityMatrix, Map<Integer, Set<Integer>> assignments,
            AbstractQueue<Element> pq, ClusterLinkage linkage, int leftId, int rightId, int ma, int mb) {
        Element current;
        double distance;
        Set<Integer> clusterMembers;

        //similarityMatrix.print(5, 2);
        //choose first ID in cluster
        for (Map.Entry<Integer, Set<Integer>> cluster : assignments.entrySet()) {
            clusterMembers = cluster.getValue();
            //each item is at the begining cluster by itself
            for (Integer id : clusterMembers) {
                distance = updateProximity(id, leftId, rightId, similarityMatrix,
                        linkage, ma, mb, clusterMembers.size());
                current = new Element(distance, leftId, cluster.getKey());
                pq.add(current);
            }
        }
    }

    /**
     * Lance-Williams update formula
     *
     * p(r,q) = alpha_a * p(a,q) + alpha_b * p(b,q) + beta * p(a,b) + gamma *
     * |p(a,q) - p(b,q)|
     *
     *
     * @param q existing cluster
     * @param a a cluster that is being merged
     * @param b a cluster that is being merged
     * @param sim similarity matrix
     * @param linkage cluster linkage method
     * @param ma
     * @param mb
     * @param mq
     * @return
     */
    public double updateProximity(int q, int a, int b, Matrix sim, ClusterLinkage linkage, int ma, int mb, int mq) {
        double aq = sim.get(a, q);
        double bq = sim.get(b, q);

        double dist = linkage.alphaA(ma, mb, mq) * aq + linkage.alphaB(ma, mb, mq) * bq;
        if (linkage.beta(ma, mb, mq) != 0) {
            dist += linkage.beta(ma, mb, mq) * sim.get(a, b);
        }
        if (linkage.gamma() != 0) {
            dist += linkage.gamma() * Math.abs(aq - bq);
        }
        //update proximity matrix
        //we rewrite proximity matrix values in order to match new distances between items
        // R is equal A and B in this case
        sim.set(a, q, dist);
        sim.set(b, q, dist);
        return dist;
    }

}
