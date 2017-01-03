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
package org.clueminer.eval.external;

import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 * Normalized Mutual Information
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public abstract class NMIbase<E extends Instance, C extends Cluster<E>>
        extends AbstractExternalEval<E, C> implements ClusterEvaluation<E, C> {

    private static final long serialVersionUID = -480979241137671097L;

    /**
     *
     * @param count    total number of elements N (in whole dataset)
     * @param elements
     * @return
     */
    //this is cool but hard to cast...
    // private double entropy(int count, Integer... elements)
    protected double entropy(int count, int[] elements) {
        double entropy = 0;
        double pk;
        for (int d : elements) {
            if (d != 0) {
                pk = d / (double) count;
                entropy += pk * Math.log(pk);
            }
        }
        return -entropy;
    }

    /**
     * Computes score against class label (must be provided)
     *
     * @param clusters
     * @param params
     * @return
     */
    @Override
    public double score(Clustering<E, C> clusters, Props params) throws ScoreException {
        double nmi = 0.0;
        if (clusters.isEmpty()) {
            return nmi;
        }

        int instancesCnt = clusters.instancesCount();
        Table<String, String, Integer> table = CountingPairs.contingencyTable(clusters);
        Map<String, Integer> res;
        double c1entropy = entropy(clusters.instancesCount(), clusters.clusterSizes());

        Map<String, Integer> klassSizes = new HashMap<>(table.columnKeySet().size());

        double mutualInformation = 0;
        int common;
        int klassSize;
        for (String klass : table.columnKeySet()) {
            for (String cluster : table.rowKeySet()) {
                //has some assignments of class to a given cluster
                if (table.get(cluster, klass) != null) {
                    res = CountingPairs.countAssignments(table, klass, cluster);
                    if (klassSizes.containsKey(klass)) {
                        klassSize = klassSizes.get(klass);
                    } else {
                        klassSize = res.get("tp") + res.get("fp");
                        klassSizes.put(klass, klassSize);
                    }
                    //System.out.println("klass size = " + klassSize);

                    int clusterSize = res.get("tp") + res.get("fn");
                    //itersection is number of true positives
                    common = res.get("tp");
                    //System.out.println("a = " + klass + ", b = " + cluster);
                    //System.out.println("common = " + common);

                    if (common > 0) {
                        mutualInformation += (common / (double) instancesCnt)
                                * Math.log(instancesCnt
                                        * common / (double) (klassSize * clusterSize));
                    }
                }

            }
        }
        int[] clusterSizes = new int[klassSizes.size()];
        int i = 0;
        for (String key : klassSizes.keySet()) {
            clusterSizes[i++] = klassSizes.get(key);
        }

        double classEntropy = entropy(clusters.instancesCount(), clusterSizes);

        return calculate(clusters, params, mutualInformation, c1entropy, classEntropy, klassSizes.size());
    }

    protected double calculate(Clustering<E, C> clusters, Props params,
            double mutualInformation, double c1entropy, double classEntropy, int klassesSize) throws ScoreException {
        return countNMI(mutualInformation, c1entropy, classEntropy);
    }

    public abstract double countNMI(double mi, double ent1, double ent2) throws ScoreException;

    @Override
    public double score(Clustering clusters, Matrix proximity, Props params) throws ScoreException {
        return score(clusters, params);
    }

    @Override
    public double score(Clustering clusters) throws ScoreException {
        return score(clusters, new Props());
    }

    /**
     * Should be maximized, maximum value is 1.0
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public boolean isBetter(double score1, double score2) {
        return score1 > score2;
    }

    @Override
    public boolean isMaximized() {
        return true;
    }

    /**
     * We want to compare two clusterings to evaluate how similar they are
     *
     * @param c1
     * @param c2
     * @param params
     * @return
     */
    @Override
    public double score(Clustering<E, C> c1, Clustering<E, C> c2, Props params) {
        double nmi = 0.0;
        if (c1.size() == 0 || c2.size() == 0) {
            return nmi;
        }
        int instancesCnt = c1.instancesCount();

        if (c1.instancesCount() != c2.instancesCount()) {
            throw new RuntimeException("clusterings have different numbers of instances");
        }

        double c1entropy = entropy(c1.instancesCount(), c1.clusterSizes());
        double c2entropy = entropy(c2.instancesCount(), c2.clusterSizes());

        double mutualInformation = 0;
        int common;
        for (Cluster<E> a : c1) {
            final int clusterSize = a.size();
            for (Cluster<E> b : c2) {
                Set<E> intersection = Sets.intersection(a, b);
                common = intersection.size();
                //System.out.println("a = " + a.getName() + ", b = " + b.getName());
                //System.out.println("common = " + common);

                if (common > 0) {
                    mutualInformation += (common / (double) instancesCnt)
                            * Math.log(instancesCnt
                                    * common / (double) (clusterSize * b.size()));
                }
            }
        }

        nmi = mutualInformation / ((c1entropy + c2entropy) / 2);

        return nmi;
    }

    @Override
    public double getMin() {
        return 0;
    }

    @Override
    public double getMax() {
        return 1;
    }
}
