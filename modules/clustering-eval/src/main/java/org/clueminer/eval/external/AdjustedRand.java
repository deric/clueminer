package org.clueminer.eval.external;

import com.google.common.collect.BiMap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import java.util.Set;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

/**
 * Based on Adjusted Rand coefficient: Hubert, L. and Arabie, P. (1985)
 * Comparing partitions. Journal of Classification, 193– 218
 *
 * @see RandIndex
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class AdjustedRand extends AbstractExternalEval {

    private static final long serialVersionUID = -7408696944704938976L;
    private static final String name = "Adjusted Rand";

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
            //last column (sum over a row)
            b1 += combinationOfTwo(contingency[i][diamCol]);
            //last row (sum over a column)
            b2 += combinationOfTwo(contingency[diamRow][i]);
        }

        double all = combinationOfTwo(contingency[diamRow][diamCol]);
        double bProd = b1 * b2;
        score = (a - (bProd) / all) / ((b1 + b2) / 2.0 - (bProd) / all);

        return score;

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
    private long combinationOfTwo(int n) {
        if (n > 1) {
            //for n < k doesn't make sense
            return ArithmeticUtils.binomialCoefficient(n, 2);
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
    private int[][] extendedContingency(Table<String, String, Integer> table) {
        BiMap<String, String> matching = CountingPairs.findMatching(table);
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
                Set<String> unmatchedClusters = Sets.symmetricDifference(matching.values(), rows);
                for (String str : unmatchedClusters) {
                    rk[k++] = str;
                }
            } else {
                //more classes than actual clusters
                Set<String> unmatchedClasses = Sets.symmetricDifference(matching.keySet(), rows);
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

    @Override
    public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(clusters);
        return countScore(table);
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset, Matrix proximity) {
        return score(clusters, dataset);
    }

    @Override
    public double score(Clustering<Cluster> c1, Clustering<Cluster> c2) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(c1, c2);
        return countScore(table);
    }

    @Override
    public boolean isMaximized() {
        return true;
    }
}
