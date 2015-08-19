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

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.eval.utils.Matching;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class AdjustedRandCorrected<E extends Instance, C extends Cluster<E>> extends AbstractExternalEval<E, C> {

    private static final long serialVersionUID = 8408696944704938977L;
    private static final String name = "Adjusted Rand (corr)";
    private static final String unknownLabel = "unknown";

    @Override
    public String getName() {
        return name;
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
     *
     * @param c1 - clustering displayed in rows
     * @param c2 - clustering displayed in columns
     * @return matrix with numbers of instances in same clusters
     */
    public int[][] countMutual(Clustering<E, C> c1, Clustering<E, C> c2) {
        int[][] conf = new int[c1.size() + 1][c2.size() + 1];
        Cluster<E> curr;
        int s1 = c1.size();
        int s2 = c2.size();

        for (int i = 0; i < s1; i++) {
            curr = c1.get(i);
            for (Instance inst : curr) {
                for (int j = 0; j < s2; j++) {
                    if (c2.get(j).contains(inst.getIndex())) {
                        conf[i][j]++;
                    }
                }
            }
            conf[i][s2] = curr.size();
        }

        //update sum of columns
        for (int j = 0; j < s2; j++) {
            conf[s1][j] = c2.get(j).size();
        }

        //Dump.matrix(conf, "conf mat", 0);
        return conf;
    }

    /**
     * Count number of classes in each cluster when we don't know how many
     * classes we have.
     *
     *
     * @param clust
     * @return
     */
    public int[][] countMutual(Clustering<E, C> clust) {
        //SortedSet klasses = dataset.getClasses();
        //Table<String, String, Integer> table = counting.contingencyTable(clust);
        Table<String, String, Integer> table = contingencyTable(clust);
        //String[] klassLabels = (String[]) klasses.toArray(new String[klasses.size()]);
        Set<String> rows = table.rowKeySet();
        String[] rowLabels = rows.toArray(new String[rows.size()]);
        int[][] conf = new int[rowLabels.length + 1][clust.size() + 1];

        int k = 0;
        //Dump.array(rowLabels, "classes");
        for (Cluster c : clust) {
            Map<String, Integer> col = table.column(c.getName());
            for (int i = 0; i < rowLabels.length; i++) {
                if (col.containsKey(rowLabels[i])) {
                    conf[i][k] = col.get(rowLabels[i]);
                    conf[i][clust.size()] += conf[i][k];
                }
                conf[rows.size()][k] += conf[i][k];
            }
            k++;
        }
        //Dump.matrix(conf, "conf mat", 0);
        return conf;
    }

    public Table<String, String, Integer> newTable() {
        return Tables.newCustomTable(
                Maps.<String, Map<String, Integer>>newHashMap(),
                new Supplier<Map<String, Integer>>() {
                    @Override
                    public Map<String, Integer> get() {
                        return Maps.newHashMap();
                    }
                });
    }

    /**
     * Should count number of item with same assignment to <Cluster A, Class X>
     * Instances must have included information about class assignment. This
     * table is sometimes called contingency table
     *
     * Classes are in rows, Clusters are in columns
     *
     * @param clustering
     * @return table with counts of items for each pair cluster, class
     */
    public Table<String, String, Integer> contingencyTable(Clustering<E, C> clustering) {
        // a lookup table for storing correctly / incorrectly classified items
        Table<String, String, Integer> table = newTable();

        //Cluster current;
        Instance inst;
        String cluster, label;
        int cnt;
        for (Cluster<E> current : clustering) {
            for (int i = 0; i < current.size(); i++) {
                inst = current.instance(i);
                cluster = current.getName();
                Object klass = inst.classValue();
                if (klass != null) {
                    label = klass.toString();
                } else {
                    label = unknownLabel;
                }

                if (table.contains(label, cluster)) {
                    cnt = table.get(label, cluster);
                } else {
                    cnt = 0;
                }

                cnt++;
                table.put(label, cluster, cnt);
            }
        }
        return table;
    }

    /**
     * Count Adjusted Rand index
     *
     * @param table contingency table where last column/row sums values in the
     * column/row
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
    private long combinationOfTwo(int n) {
        if (n > 1) {
            //for n < k doesn't make sense
            return CombinatoricsUtils.binomialCoefficient(n, 2);
        }
        return 0;
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
    public double score(Clustering<E, C> clusters, Props params) {
        int[][] conf = countMutual(clusters);
        return countScore(conf);
    }

    @Override
    public double score(Clustering clusters) {
        return score(clusters, new Props());
    }

    @Override
    public double score(Clustering<E, C> clusters, Matrix proximity, Props params) {
        return score(clusters, params);
    }

    @Override
    public double score(Clustering<E, C> c1, Clustering<E, C> c2, Props params) {
        int[][] conf = countMutual(c1, c2);
        return countScore(conf);
    }

    @Override
    public boolean isMaximized() {
        return true;
    }

    @Override
    public double getMin() {
        return -1;
    }

    @Override
    public double getMax() {
        return 1;
    }
}
