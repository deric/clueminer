package org.clueminer.clustering.aggl;

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

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected void updateDistances(int mergedId, Set<Integer> mergedCluster,
            Matrix similarityMatrix, Map<Integer, Set<Integer>> assignments,
            PriorityQueue<Element> pq, ClusterLinkage linkage) {
        Element current;
        double distance;
        similarityMatrix.printLower(2, 2);
        System.out.println("triangle size: " + triangleSize(similarityMatrix.rowsCount()));
        System.out.println("merging " + mergedCluster.toString() + " -> " + mergedId);
        System.out.println("to: " + assignments.entrySet().toString());
        Iterator<Integer> it = mergedCluster.iterator();
        int a = it.next();
        int b = it.next();
        for (Map.Entry<Integer, Set<Integer>> cluster : assignments.entrySet()) {
            //distance = linkage.similarity(similarityMatrix, cluster.getValue(), mergedCluster);
            System.out.println("cluster: " + cluster.getKey() + ", a = " + a + ", b = " + b);
            distance = updateProximity(mergedId, cluster.getKey(), a, b, similarityMatrix, linkage);
            current = new Element(distance, mergedId, cluster.getKey());
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
        System.out.println("aq(" + a + ", " + q + ") = ");
        System.out.println(sim.get(a, q));
        double dist = linkage.alphaA() * sim.get(a, q) + linkage.alphaB() * sim.get(b, q);
        if (linkage.beta() != 0) {
            dist += sim.get(a, b);
        }
        if (linkage.gamma() != 0) {
            dist += Math.abs(sim.get(a, q) - sim.get(b, q));
        }
        return dist;
    }

}
