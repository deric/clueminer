/*
 * Copyright (C) 2011-2015 clueminer.org
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

import com.google.common.collect.Table;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.eval.utils.Matching;
import org.clueminer.eval.utils.PairMatch;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Combination of NMI and ARI metrics
 *
 * @author deric
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class NMIARI extends NMIbase {

    private static final String name = "NMI+ARI";
    private static final long serialVersionUID = -5755991651852972241L;

    @Override
    public String getName() {
        return name;
    }

    /**
     * Computation inspired by approach in:
     *
     * Santos, Jorge M. and Embrechts, Mark (2009): On the Use of the Adjusted
     * Rand Index as tp Metric for Evaluating Supervised Classification
     *
     * @param pm
     * @return
     */
    public double score(PairMatch pm) {
        double ari, np = pm.sum();
        double tmp = (pm.tp + pm.fp) * (pm.tp + pm.fn) + (pm.fn + pm.tn) * (pm.fp + pm.tn);
        ari = np * (pm.tp + pm.tn) - tmp;
        ari /= np * np - tmp;
        return ari;
    }

    @Override
    public double countNMI(double mutualInformation, double ent1, double ent2) {
        return mutualInformation / Math.sqrt(ent1 * ent2);
    }

    @Override
    protected double calculate(Clustering<? extends Cluster> clusters, Props params,
            double mutualInformation, double c1entropy, double classEntropy, int klassesSize) {

        PairMatch pm = CountingPairs.matchPairs(clusters);

        return (countNMI(mutualInformation, c1entropy, classEntropy) + score(pm)) / 2.0;
    }

    /**
     * Based on "Details of the Adjusted Rand index and Clustering algorithms
     * Supplement to the paper “An empirical study on Principal Component
     * Analysis for clustering gene expression data” (to appear in
     * Bioinformatics)"
     *
     * @param table
     * @return
     */
    protected int[][] extendedContingency(Table<String, String, Integer> table) {
        Matching matching = CountingPairs.findMatching(table);
        Set<String> rows = table.rowKeySet();    //clusters
        Set<String> cols = table.columnKeySet(); //classes

        String[] rk = new String[rows.size()];
        String[] ck = new String[cols.size()];
        int k = 0;

        //we have to order items in set, so that on diagonal will be highest
        //numbers - for corresponding clusters
        for (String c : cols) {
            if (k < rk.length) {
                rk[k] = matching.get(c);
            }
            ck[k] = c;
            k++;
        }

        //number of rows could be different if we compare clusters to classes
        if (rows.size() != cols.size()) {
            //more clusters than classes
            if (rows.size() > cols.size()) {
                //CollectionUtils.disjunction();
                Set<String> unmatchedClusters = diff(matching.values(), rows);
                for (String str : unmatchedClusters) {
                    rk[k++] = str;
                }
            } else {
                //more classes than actual clusters
                Set<String> unmatchedClasses = diff(matching.keySet(), cols);
                k = rows.size();
                for (String str : unmatchedClasses) {
                    if (k < ck.length) {
                        ck[k++] = str;
                    }
                }
            }
        }

        //last row (column) will be sum of row's (column's) values
        int[][] contingency = new int[rows.size() + 1][cols.size() + 1];
        int i = 0, j;
        int value;
        for (String r : rk) {
            j = 0;
            for (String c : ck) {
                if (table.contains(r, c)) {
                    value = table.get(r, c);
                } else {
                    value = 0;
                }
                contingency[i][j] = value;
                //sum over columns -- result is in last (extra row)
                contingency[rows.size()][j] += contingency[i][j];
                //sum over rows
                contingency[i][cols.size()] += contingency[i][j];
                //sum over all numbers
                contingency[rows.size()][cols.size()] += contingency[i][j];
                j++;
            }
            i++;
        }

        return contingency;
    }

    /**
     * Perform tp-fp operation with sets
     *
     * @param a
     * @param b
     * @return
     */
    private Set<String> diff(Collection<String> a, Set<String> b) {
        Set<String> res = new HashSet<>();
        for (String s : a) {
            if (!b.contains(s)) {
                res.add(s);
            }
        }
        return res;
    }

}
