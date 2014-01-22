package org.clueminer.clustering.aggl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.prefs.Preferences;
import org.clueminer.clustering.algorithm.HClustResult;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.hclust.DTreeNode;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Dump;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class HC1 extends AbstractClusteringAlgorithm implements AgglomerativeClustering {

    private final static String name = "HC1";
    private DendroNode nodes[];
    private Dataset<? extends Instance> dataset;
    private ClusterLinkage linkage;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Clustering<Cluster> cluster(Matrix matrix, Preferences props) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HierarchicalResult hierarchy(Dataset<? extends Instance> dataset, Preferences params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HierarchicalResult hierarchy(Matrix input, Dataset<? extends Instance> dataset, Preferences pref) {
        this.dataset = dataset;
        HierarchicalResult result = new HClustResult(dataset);
        AgglParams param = new AgglParams(pref);
        distanceMeasure = param.getDistanceMeasure();
        linkage = param.getLinkage();
        int items = (dataset.size() - 1) * dataset.size() / 2;
        PriorityQueue<Element> pq = new PriorityQueue<Element>(items);
        Matrix similarityMatrix = AgglClustering.rowSimilarityMatrix(input, distanceMeasure, pq);
        result.setSimilarityMatrix(similarityMatrix);

        //each instance will form a cluster
        Map<Integer, Set<Integer>> assignments = initialAssignment(dataset.size());

        int i = 0;
        Element curr;
        HashSet<Integer> blacklist = new HashSet<Integer>();
        DendroNode node;
        Set<Integer> left, right;

        int nodeId = dataset.size();
        do {
            curr = pq.poll();
            if (!blacklist.contains(curr.getRow()) && !blacklist.contains(curr.getColumn())) {
                node = new DTreeNode(nodeId++);
                node.setLeft(nodes[curr.getRow()]);
                node.setRight(nodes[curr.getColumn()]);
                node.setHeight(curr.getValue());
                nodes[node.getId()] = node;

                blacklist.add(curr.getRow());
                blacklist.add(curr.getColumn());

                //remove old clusters
                left = assignments.remove(curr.getRow());
                right = assignments.remove(curr.getColumn());
                //merge together and add as a new cluster
                left.addAll(right);
                updateDistances(nodeId, left, similarityMatrix, assignments, pq);
                i++;
            }
        } while (i < dataset.size());

        similarityMatrix.printLower(5, 2);

        System.out.println("queue size: " + pq.size());
        System.out.println(pq.poll());

        System.out.println("largest n = " + Math.sqrt(Integer.MAX_VALUE));

        return result;
    }

    private void updateDistances(int mergedId, Set<Integer> mergedCluster, Matrix similarityMatrix, Map<Integer, Set<Integer>> assignments, PriorityQueue<Element> pq) {
        Element current;
        double distance;
        for (Map.Entry<Integer, Set<Integer>> cluster : assignments.entrySet()) {
            distance = linkage.similarity(similarityMatrix, mergedCluster, cluster.getValue());
            current = new Element(distance, mergedId, cluster.getKey());
            pq.add(current);
        }
        //finaly add merged cluster
        assignments.put(mergedId, mergedCluster);
    }

    /**
     * Each data point forms an individual cluster
     *
     * @param n the number of data points
     * @return
     */
    protected Map<Integer, Set<Integer>> initialAssignment(int n) {
        //binary tree, we know how many nodes we have
        nodes = new DendroNode[(2 * n - 1)];
        Map<Integer, Set<Integer>> clusterAssignment = new HashMap<Integer, Set<Integer>>(n);
        for (int i = 0; i < n; ++i) {
            HashSet<Integer> cluster = new HashSet<Integer>();
            cluster.add(i);
            clusterAssignment.put(i, cluster);
            //each cluster is also a dendrogram leaf
            nodes[i] = new DTreeNode(i);
            nodes[i].setInstance(dataset.get(i));
        }
        return clusterAssignment;
    }

    @Override
    public HierarchicalResult hierarchy(Matrix matrix, Preferences props) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
