package org.clueminer.clustering.aggl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.math.Matrix;

/**
 * Hierarchical clustering - updating distances using Lance-Williams update
 * formula
 *
 * @author Tomas Barton
 */
public class HACLW extends HAC implements AgglomerativeClustering {

    private final static String name = "HAC-LW";
    private HashMap<Integer, Double> cache = new HashMap<>();

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected void updateDistances(int mergedId, Set<Integer> mergedCluster,
            Matrix similarityMatrix, Map<Integer, Set<Integer>> assignments,
            PriorityQueue<Element> pq, ClusterLinkage linkage) {
        Element current;
        double distance, expDist;
        similarityMatrix.printLower(2, 2);
        System.out.println("triangle size: " + triangleSize(similarityMatrix.rowsCount()));
        System.out.println("merging " + mergedCluster.toString() + " -> " + mergedId);
        System.out.println("other: " + assignments.entrySet().toString());
        Iterator<Integer> it = mergedCluster.iterator();
        int a = it.next();
        int b = it.next();
        for (Map.Entry<Integer, Set<Integer>> cluster : assignments.entrySet()) {
            //expDist = linkage.similarity(similarityMatrix, cluster.getValue(), mergedCluster);

            System.out.println("update(" + mergedId + ", " + cluster.getKey() + "): a = " + a + ", b = " + b);
            distance = updateProximity(mergedId, cluster.getKey(), a, b, similarityMatrix, linkage);
            //System.out.println("expDist:" + expDist);
            System.out.println("distanc:" + distance);
            //assert (distance == expDist);
            current = new Element(distance, mergedId, cluster.getKey());
            System.out.println("new node @" + map(mergedId, cluster.getKey()) + ": " + current.toString());
            pq.add(current);
        }
        //finaly add merged cluster
        assignments.put(mergedId, mergedCluster);
    }

    /**
     *
     * @param r       existing cluster
     * @param q       cluster R is created after merging A and B
     * @param a       a cluster that is being merged
     * @param b       a cluster that is being merged
     * @param sim     similarity matrix
     * @param linkage cluster linkage method
     * @return
     */
    public double updateProximity(int r, int q, int a, int b, Matrix sim, ClusterLinkage linkage) {
        double aq, bq;
        if (!sim.has(a, q)) {
            System.out.println("getting: a= " + a + ", q= " + q);
            aq = cache.get(map(a, q));
        } else {
            aq = sim.get(a, q);
        }
        if (!sim.has(b, q)) {
            System.out.println("cache: " + cache);
            System.out.println("getting: b= " + b + ", q= " + q + " -> " + map(b, q));
            bq = cache.get(map(b, q));
        } else {
            bq = sim.get(b, q);
        }

        System.out.println("aq(" + a + ", " + q + ") = " + String.format("%.2f", aq));
        double dist = linkage.alphaA() * aq + linkage.alphaB() * bq;
        //if (!sim.has(a, q)) {

        //}
        if (linkage.beta() != 0) {
            dist += sim.get(a, b);
        }
        if (linkage.gamma() != 0) {
            dist += Math.abs(aq - bq);
        }
        System.out.println(map(r, q) + " <- (" + r + ", " + q + ") =" + dist);
        cache.put(map(r, q), dist);
        return dist;
    }

    private int map(int i, int j) {
        if (i < j) {
            /**
             * swap variables, matrix is symmetrical, we work with lower
             * triangular matrix
             */
            int tmp = i;
            i = j;
            j = tmp;
        }
        /**
         * it's basically a sum of arithmetic row (we need to know how many
         * numbers could be allocated before given position [x,y])
         */
        return triangleSize(i) + j;
    }

}
