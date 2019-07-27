/*
 * Copyright (C) 2011-2019 clueminer.org
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
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 * Adjusted Mutual Information
 *
 * Vinh, Nguyen Xuan, Julien Epps, and James Bailey. "Information theoretic
 * measures for clusterings comparison: Variants, properties, normalization and
 * correction for chance." Journal of Machine Learning Research 11.Oct (2010):
 * 2837-2854.
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public abstract class AMIbase<E extends Instance, C extends Cluster<E>>
        extends AbstractExternalEval<E, C> implements ClusterEvaluation<E, C> {

    /**
     * Computes score against class labels (must be provided)
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

        int N = clusters.instancesCount();
        Table<String, String, Integer> table = CountingPairs.contingencyTable(clusters);

        double hc = -HC(table, N);
        double hk = -HK(table, N);

        double mi = mutualInformation(table, N);
        //TODO: emi is NaN
        double emi = expectedMutualInformation(table, N);

        return calculate(params, mi, emi, hc, hk, table.columnKeySet().size());
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
    public double score(Clustering<E, C> c1, Clustering<E, C> c2, Props params) throws ScoreException {
        double nmi = 0.0;
        if (c1.isEmpty() || c2.isEmpty()) {
            return nmi;
        }

        int N = c1.instancesCount();
        Table<String, String, Integer> table = CountingPairs.contingencyTable(c1, c2);

        double hc = -HC(table, N);
        double hk = -HK(table, N);

        double mi = mutualInformation(table, N);
        System.out.println("mi = " + mi);

        //TODO: emi is NaN
        double emi = expectedMutualInformation(table, N);
        System.out.println("emi = " + emi);

        return calculate(params, mi, emi, hc, hk, table.columnKeySet().size());
    }

    public abstract double calculate(Props params, double mi, double emi, double ent1, double ent2, int klassesSize) throws ScoreException;

    /**
     * Expected Mutual Information
     *
     * @param contTable
     * @param n
     * @return
     */
    protected double expectedMutualInformation(Table<String, String, Integer> contTable, int n) {
        double emi = 0.0, common;
        Map<String, Integer> res;
        Map<String, Integer> klassSizes = new HashMap<>(contTable.columnKeySet().size());
        int a;

        for (String klass : contTable.columnKeySet()) {
            for (String clust : contTable.rowKeySet()) {
                common = value(contTable, clust, klass);

                if (common > 0.0) {
                    res = CountingPairs.countAssignments(contTable, klass, clust);
                    if (klassSizes.containsKey(klass)) {
                        a = klassSizes.get(klass);
                    } else {
                        a = res.get("tp") + res.get("fp");
                        klassSizes.put(klass, a);
                    }
                    int b = res.get("tp") + res.get("fn");

                    int k = (int) Math.max(a + b - n, 0);
                    int to = Math.min(a, b);

                    for (int nij = k; nij < to; nij++) {
                        emi += (nij / n)
                                * Math.log((nij * n) / (double) (a * b))
                                * (factorial(a) * factorial(b) * factorial(n - a) * factorial(n - b))
                                / (factorial(n) * factorial(nij) * factorial(a - nij) * factorial(b - nij) * factorial(n - a - b + nij));
                    }
                }
            }
        }
        return emi;
    }

    private double factorial(int i) {
        return CombinatoricsUtils.factorialDouble(i);
    }

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

    @Override
    public double getMin() {
        return 0;
    }

    @Override
    public double getMax() {
        return 1;
    }

}
