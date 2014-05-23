package org.clueminer.eval.external;

import org.clueminer.eval.utils.CountingPairs;
import com.google.common.collect.BiMap;
import com.google.common.collect.Table;
import java.util.Set;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;

/**
 * Based on Adjusted Rand coefficient: Hubert, L. and Arabie, P. (1985)
 * Comparing partitions. Journal of Classification, 193– 218
 *
 * @see RandIndex
 * @author Tomas Barton
 */
public class AdjustedRand extends ExternalEvaluator {

    private static final long serialVersionUID = -7408696944704938976L;
    private static String name = "Adjusted Rand";

    @Override
    public String getName() {
        return name;
    }

    public double countScore(Table<String, String, Integer> table) {
        double score;
        int[][] contingency = extendedContingency(table);
        //extra row/column is used for storing sums - the last one
        int diameter = contingency.length - 1;

        double a = 0;
        int b1 = 0;
        int b2 = 0;
        for (int i = 0; i < diameter; i++) {
            //diagonal item
            a += combinationOfTwo(contingency[i][i]);
            //last column (sum over a row)
            b1 += combinationOfTwo(contingency[i][diameter]);
            //last row (sum over a column)
            b2 += combinationOfTwo(contingency[diameter][i]);
        }

        double all = combinationOfTwo(contingency[diameter][diameter]);
        double bProd = b1 * b2;
        score = (a - (bProd) / all) / ((b1 + b2) / 2.0 - (bProd) / all);

        return score;
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
            return ArithmeticUtils.binomialCoefficient(n, 2);
        }
        return 0;
    }

    private int[][] extendedContingency(Table<String, String, Integer> table) {
        BiMap<String, String> matching = CountingPairs.findMatching(table);
        Set<String> rows = table.rowKeySet();
        Set<String> cols = table.columnKeySet();

        if (rows.size() != cols.size()) {
            throw new RuntimeException("expected same number of rows and columns, but got rows = " + rows.size() + " and colums = " + cols.size());
        }
        String[] rk = new String[rows.size()];
        String[] ck = new String[cols.size()];

        int k = 0;
        //we have to order items in set, so that on diagonal will be highest 
        //numbers - for corresponding clusters
        for (String c : cols) {
            rk[k] = matching.get(c);
            ck[k] = c;
            k++;
        }

        //last row (column) will be sum of row's (column's) values
        int[][] contingency = new int[rows.size() + 1][cols.size() + 1];
        int i = 0, j;
        int value;
        for (String c : ck) {
            j = 0;
            for (String r : rk) {
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

    @Override
    public double score(Clustering<Cluster> clusters, Dataset<? extends Instance> dataset) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(clusters);
        return countScore(table);
    }

    @Override
    public double score(Clustering<Cluster> clusters, Dataset<? extends Instance> dataset, Matrix proximity) {
        return score(clusters, dataset);
    }

    /**
     * Should be maximized
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public boolean compareScore(double score1, double score2) {
        return score1 > score2;
    }

    @Override
    public double score(Clustering<Cluster> c1, Clustering<Cluster> c2) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(c1, c2);
        return countScore(table);
    }
}
