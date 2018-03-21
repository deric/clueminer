/*
 * Copyright (C) 2011-2018 clueminer.org
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
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.eval.utils.Matching;
import org.clueminer.eval.utils.PairMatch;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Based on Adjusted Rand coefficient: Hubert, L. and Arabie, P. (1985)
 * Comparing partitions. Journal of Classification, 193– 218
 *
 * @param <E>
 * @param <C>
 * @see RandIndex
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class AdjustedRand<E extends Instance, C extends Cluster<E>> extends AbstractExternalEval<E, C> {

    private static final long serialVersionUID = -7408696944704938976L;
    private static final String NAME = "Adjusted_Rand";

    @Override
    public String getName() {
        return NAME;
    }

    public double countScore(int[][] contingency) {
        double score;
        //extra row/column is used for storing sums - the last one
        int diamRow = contingency.length - 1;
        int diamCol = contingency[0].length - 1;
        double a = 0;
        int b1 = 0;
        int b2 = 0;
        //for non-square matrix we count with diagonal because row and columns
        //are sorted (matching classes -> clusters form square matrix)
        int squareSize = Math.min(diamRow, diamCol);
        for (int i = 0; i < squareSize; i++) {
            //diagonal item
            a += combinationOfTwo(contingency[i][i]);
            //last column (sum over tp row)
            b1 += combinationOfTwo(contingency[i][diamCol]);
            //last row (sum over tp column)
            b2 += combinationOfTwo(contingency[diamRow][i]);
        }

        double all = combinationOfTwo(contingency[diamRow][diamCol]);
        double bProd = b1 * b2;
        score = (a - (bProd) / all) / ((b1 + b2) / 2.0 - (bProd) / all);

        return score;
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

    /**
     * Count Adjusted Rand index
     *
     * @param table contingency table where last column/row sums values in the
     *              column/row
     * @return
     */
    public double countScore(Table<String, String, Integer> table) {
        //WARNING the result is sensitive to matching rows/columns
        return countScore(extendedContingency(table));
    }

    /**
     * Counts combination number, where the k = 2
     *
     * @param n
     * @return
     */
    protected long combinationOfTwo(int n) {
        // a micooptimization, instead of computing factorial, in case of k=2
        // this is much faster
        return n * (n - 1) >>> 1; //equal to division by 2
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

    @Override
    public double score(Clustering<E, C> clusters, Props params) throws ScoreException {
        PairMatch pm = CountingPairs.getInstance().matchPairs(clusters);
        return score(pm);
    }

    @Override
    public double score(Clustering clusters) throws ScoreException {
        return score(clusters, new Props());
    }

    @Override
    public double score(Clustering<E, C> clusters, Matrix proximity, Props params) throws ScoreException {
        return score(clusters, params);
    }

    @Override
    public double score(Clustering<E, C> c1, Clustering<E, C> c2, Props params) {
        PairMatch pm = CountingPairs.getInstance().matchPairs(c1, c2);
        return score(pm);
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
