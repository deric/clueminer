package org.clueminer.clustering.aggl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

/**
 * Hierarchical clustering - updating distances using Lance-Williams update
 * formula
 *
 * Lance, G. N. and Williams, W. T.. "A general theory of classificatory sorting
 * strategies 1. Hierarchical systems." The Computer Journal 9 , no. 4 (1967):
 * 373-380.
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class HACLW2 extends HACLW implements AgglomerativeClustering {

    private final static String name = "HAC-LW2";

    @Override
    public String getName() {
        return name;
    }

    /**
     * When we merge two items and create a new dendrogram node, we have to
     * update distances to all other nodes (clusters)
     *
     * @param mergedId
     * @param mergedCluster
     * @param similarityMatrix
     * @param assignments
     * @param pq
     * @param linkage
     * @param cache
     */
    @Override
    protected void updateDistances(int mergedId, Set<Integer> mergedCluster,
            Matrix similarityMatrix, Map<Integer, Set<Integer>> assignments,
            PriorityQueue<Element> pq, ClusterLinkage linkage, HashMap<Integer, Double> cache) {
        Element current;
        double distance;

        Iterator<Integer> it = mergedCluster.iterator();
        int a = it.next();
        int b = it.next();
        Set<Integer> clusterMembers;

        similarityMatrix.printLower(5, 2);
        System.out.println("merge [" + a + ", " + b + "] -> " + mergedId);
        System.out.println("assign: " + assignments.entrySet().toString());

        for (Map.Entry<Integer, Set<Integer>> cluster : assignments.entrySet()) {
            distance = updateProximity(mergedId, cluster.getKey(), a, b, similarityMatrix, linkage, cache);
            current = new Element(distance, mergedId, cluster.getKey());
            pq.add(current);
            clusterMembers = cluster.getValue();
            //each item is at the begining cluster by itself
            if (clusterMembers.size() > 1) {
                for (Integer id : clusterMembers) {
                    distance = updateProximity(mergedId, id, a, b, similarityMatrix, linkage, cache);
                    current = new Element(distance, mergedId, cluster.getKey());
                    pq.add(current);
                }
            }
        }
        //finaly add merged cluster
        assignments.put(mergedId, mergedCluster);

        for (int k : cache.keySet()) {
            System.out.println(k + " = " + String.format("%.2f", cache.get(k)));
        }
    }

    protected void createNode(int mergedId, int other, int key, Matrix similarityMatrix,
            PriorityQueue<Element> pq, ClusterLinkage linkage, HashMap<Integer, Double> cache, int a, int b) {
        double distance = updateProximity(mergedId, other, a, b, similarityMatrix, linkage, cache);
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
     * @param r       existing cluster
     * @param q       cluster R is created after merging A and B
     * @param a       a cluster that is being merged
     * @param b       a cluster that is being merged
     * @param sim     similarity matrix
     * @param linkage cluster linkage method
     * @param cache
     * @return
     */
    public double updateProximity(int r, int q, int a, int b, Matrix sim, ClusterLinkage linkage, HashMap<Integer, Double> cache) {
        double aq = fetchDist(a, q, sim, cache);
        double bq = fetchDist(b, q, sim, cache);

        System.out.println("p(" + r + ", " + q + ") = 0.5 * p(" + a + ", " + q + ") + 0.5*p(" + b + ", " + q + ") - 0.5*| p(" + a + ", " + q + ") - p(" + b + ", " + q + ")|");
        double dist = linkage.alphaA() * aq + linkage.alphaB() * bq;
        if (linkage.beta() != 0) {
            dist += linkage.beta() * sim.get(a, b);
        }
        if (linkage.gamma() != 0) {
            dist += linkage.gamma() * Math.abs(aq - bq);
        }
        System.out.println("        = " + String.format("%.2f", dist) + " => " + map(r, q));
        /*        if (r != q) {
         sim.set(r, q, dist);
         }*/

        cache.put(map(r, q), dist);
        return dist;
    }

    /**
     * Updating original proximity matrix would be too expensive, we rather use
     * a HashMap
     *
     * @param x
     * @param y
     * @param sim
     * @param cache
     * @return
     */
    private double fetchDist(int x, int y, Matrix sim, HashMap<Integer, Double> cache) {
        double res;
        if (!sim.has(x, y)) {
            int mapped = map(x, y);
            res = cache.get(mapped);
        } else {
            res = sim.get(x, y);
        }
        return res;
    }

}
