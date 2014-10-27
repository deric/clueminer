package org.clueminer.clustering.aggl;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
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
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class HACLWMS extends HACLW implements AgglomerativeClustering {

    private final static String name = "HAC-LW-MS";

    @Override
    public String getName() {
        return name;
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
    protected DendroTreeData computeLinkage(PriorityQueue<Element> pq, Matrix similarityMatrix, Dataset<? extends Instance> dataset, AgglParams params, int n) {
        //binary tree, however we store at most n nodes (then rewrite references)
        DendroNode[] nodes = new DendroNode[n];
        //each instance will form a cluster
        Map<Integer, Set<Integer>> assignments = initialAssignment(n, dataset, params, nodes);

        Element curr;
        DendroNode node = null;
        Set<Integer> left, right;
        int nodeId = n;
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
                //System.out.println("removing " + curr.getRow() + " from " + assignments.toString());
                left = assignments.remove(curr.getRow());
                //System.out.println("removing " + curr.getColumn() + " from " + assignments.toString());
                right = assignments.remove(curr.getColumn());
                //merge together and add as a new cluster
                left.addAll(right);
                //System.out.println("left: " + left.toString());
                updateDistances(left, similarityMatrix, assignments, pq, params.getLinkage(), curr.getRow(), curr.getColumn());
                //when assignment have size == 1, all clusters are merged into one
            }
        }
        //System.out.println("last node: " + node.toString());
        //last node is the root
        DendroTreeData treeData = new DynamicTreeData(node);
        return treeData;
    }

    /**
     * When we merge two items and create a new dendrogram node, we have to
     * update distances to all other nodes (clusters)
     *
     * @param mergedCluster
     * @param similarityMatrix
     * @param assignments
     * @param pq
     * @param linkage
     * @param leftId
     * @param rightId
     */
    protected void updateDistances(Set<Integer> mergedCluster,
            Matrix similarityMatrix, Map<Integer, Set<Integer>> assignments,
            PriorityQueue<Element> pq, ClusterLinkage linkage,
            int leftId, int rightId) {
        Element current;
        double distance;
        Set<Integer> clusterMembers;

        //similarityMatrix.print(5, 2);
        //choose first ID in cluster
        //System.out.println("merge [" + leftId + ", " + rightId + "] -> " + leftId);
        //System.out.println("assign: " + assignments.entrySet().toString());
        for (Map.Entry<Integer, Set<Integer>> cluster : assignments.entrySet()) {
            clusterMembers = cluster.getValue();
            //each item is at the begining cluster by itself
            for (Integer id : clusterMembers) {
                distance = updateProximity(leftId, id, leftId, rightId, similarityMatrix, linkage);
                current = new Element(distance, leftId, cluster.getKey());
                pq.add(current);
            }

        }
        //finaly add merged cluster
        assignments.put(leftId, mergedCluster);
        //System.out.println("assiga: " + assignments.entrySet().toString());
    }

    protected void createNode(int mergedId, int other, int key, Matrix similarityMatrix,
            PriorityQueue<Element> pq, ClusterLinkage linkage, int a, int b) {
        double distance = updateProximity(mergedId, other, a, b, similarityMatrix, linkage);
        Element current = new Element(distance, mergedId, key);
        pq.add(current);
    }

    /**
     * Lance-Williams update formula
     *
     * p(r,q) = alpha_a * p(a,q) + alpha_b * p(b,q) + beta * p(a,b) + gamma *
     * |p(a,q) - p(b,q)|
     *
     *
     * @param r       cluster R is created after merging A and B
     * @param q       existing cluster
     * @param a       a cluster that is being merged
     * @param b       a cluster that is being merged
     * @param sim     similarity matrix
     * @param linkage cluster linkage method
     * @return
     */
    public double updateProximity(int r, int q, int a, int b, Matrix sim, ClusterLinkage linkage) {
        double aq = sim.get(a, q);
        double bq = sim.get(b, q);

        //System.out.println("p(" + r + ", " + q + ") = 0.5 * p(" + a + ", " + q + ") + 0.5*p(" + b + ", " + q + ") - 0.5*| p(" + a + ", " + q + ") - p(" + b + ", " + q + ")|");
        double dist = linkage.alphaA() * aq + linkage.alphaB() * bq;
        if (linkage.beta() != 0) {
            dist += linkage.beta() * sim.get(a, b);
        }
        if (linkage.gamma() != 0) {
            dist += linkage.gamma() * Math.abs(aq - bq);
        }
        //System.out.println("        = " + String.format("%.2f", dist) + " => " + map(r, q));
        //update proximity matrix
        //we rewrite proximity matrix values in order to match new distances between items
        sim.set(a, q, dist);
        sim.set(b, q, dist);
        return dist;
    }

}
