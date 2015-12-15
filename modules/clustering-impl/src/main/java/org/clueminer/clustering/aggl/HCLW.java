package org.clueminer.clustering.aggl;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.AbstractQueue;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.clueminer.clustering.aggl.linkage.CompleteLinkageInv;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
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
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class HCLW<E extends Instance, C extends Cluster<E>> extends HC<E, C> implements AgglomerativeClustering<E, C> {

    private final static String name = "HC-LW";

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
     * @param leftId
     * @param rightId
     * @param ma
     * @param mb
     * @param centroids
     * @param dataset
     */
    @Override
    protected void updateDistances(int mergedId, Set<Integer> mergedCluster,
            Matrix similarityMatrix, Map<Integer, Set<Integer>> assignments,
            AbstractQueue<Element> pq, ClusterLinkage<E> linkage,
            HashMap<Integer, Double> cache, int leftId, int rightId, int ma, int mb,
            HashMap<Integer, E> centroids, Dataset<? extends E> dataset) {
        Element current;
        double distance;
        for (Map.Entry<Integer, Set<Integer>> cluster : assignments.entrySet()) {
            //update distance only to cluster keys (items contained in cluster
            //were already merged and we can't remerge them again)
            distance = updateProximity(mergedId, cluster.getKey(), leftId, rightId,
                    similarityMatrix, linkage, cache, ma, mb, cluster.getValue().size());
            current = new Element(distance, mergedId, cluster.getKey());
            pq.add(current);
        }
        //finaly add merged cluster
        assignments.put(mergedId, mergedCluster);
    }

    /**
     * Lance-Williams update formula
     *
     * p(r,q) = alpha_a * p(a,q) + alpha_b * p(b,q) + beta * p(a,b) + gamma *
     * |p(a,q) - p(b,q)|
     *
     *
     * @param r cluster R is created after merging A and B
     * @param q another existing cluster
     * @param a a cluster that is being merged
     * @param b a cluster that is being merged
     * @param sim similarity matrix
     * @param linkage cluster linkage method
     * @param cache
     * @param ma size of cluster A
     * @param mb size of cluster B
     * @param mq size of cluster Q
     * @return
     */
    public double updateProximity(int r, int q, int a, int b, Matrix sim,
            ClusterLinkage linkage, HashMap<Integer, Double> cache,
            int ma, int mb, int mq) {
        double aq = fetchDist(a, q, sim, cache);
        double bq = fetchDist(b, q, sim, cache);

        double dist = linkage.alphaA(ma, mb, mq) * aq + linkage.alphaB(ma, mb, mq) * bq;
        if (linkage.beta(ma, mb, mq) != 0) {
            dist += linkage.beta(ma, mb, mq) * fetchDist(a, b, sim, cache);
        }
        if (linkage.gamma() != 0) {
            dist += linkage.gamma() * Math.abs(aq - bq);
        }
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
    protected double fetchDist(int x, int y, Matrix sim, HashMap<Integer, Double> cache) {
        double res;
        if (!sim.has(x, y)) {
            int mapped = map(x, y);
            //we don't need diagonal items
            if (cache.containsKey(mapped) && (x != y)) {
                res = cache.get(mapped);
            } else {
                res = -1;
            }
        } else {
            res = sim.get(x, y);
        }
        return res;
    }

    /**
     * Mapping function to assign a unique number to each combination of
     * coordinates in matrix
     *
     * @param i
     * @param j
     * @return
     */
    protected int map(int i, int j) {
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

    /**
     * For debugging only
     *
     * @param n
     * @param sim
     * @param cache
     */
    protected void print(int n, Matrix sim, HashMap<Integer, Double> cache) {
        int d = 2;
        int w = 5;
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        print(new PrintWriter(System.out, true), format, w + 2, n, sim, cache);
    }

    /**
     * Print extended proximity matrix
     *
     * @param output
     * @param format
     * @param width
     * @param n
     * @param sim
     * @param cache
     */
    protected void print(PrintWriter output, NumberFormat format, int width, int n, Matrix sim, HashMap<Integer, Double> cache) {
        output.println();  // start on new line.
        int padding;
        String s;
        for (int i = 0; i < n; i++) {
            //print row label
            s = String.valueOf(i);
            padding = Math.max(1, width - s.length() - 1);
            for (int k = 0; k < padding; k++) {
                output.print(' ');
            }
            output.print(s);
            output.print(" |");
            for (int j = 0; j < n; j++) {
                s = format.format(fetchDist(i, j, sim, cache)); // format the number
                padding = Math.max(1, width - s.length()); // At _least_ 1 space
                for (int k = 0; k < padding; k++) {
                    output.print(' ');
                }
                output.print(s);
            }
            output.println();
        }
        //footer
        for (int i = 0; i < width * (n + 1); i++) {
            output.print('-');
        }
        output.println();
        for (int k = 0; k < width; k++) {
            output.print(' ');
        }
        for (int i = 0; i < n; i++) {
            s = String.valueOf(i); // format the number
            padding = Math.max(1, width - s.length()); // At _least_ 1 space
            for (int k = 0; k < padding; k++) {
                output.print(' ');
            }
            output.print(s);
        }
        output.println();
    }

    @Override
    public boolean isLinkageSupported(String linkage) {
        switch (linkage) {
            //case MedianLinkage.name:
            case CompleteLinkageInv.name:
                return false;
            default:
                return true;
        }
    }

}
