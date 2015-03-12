package org.clueminer.eval.external;

import com.google.common.collect.Table;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.eval.utils.Matching;
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

    public double countAri(Clustering<? extends Cluster> c1, Clustering<? extends Cluster> c2) {
        //pairs that are in the same cluster in both clusterings
        int a = 0;
        //pairs that are in same the cluster in C1 but not in C2
        int b = 0;
        //pairs that are in the same cluster in C2 but not in C1
        int c = 0;
        //pairs that are in different community in both clusterings
        int d = 0;

        double ari, np = 0;

        Instance x, y;
        Cluster cx1, cx2, cy1, cy2;
        for (int i = 0; i < c1.instancesCount(); i++) {
            x = c1.instance(i);
            cx1 = c1.assignedCluster(x);
            cx2 = c2.assignedCluster(x);
            for (int j = 0; j < i; j++) {
                if (i != j) {
                    y = c1.instance(j);
                    cy1 = c1.assignedCluster(y);
                    cy2 = c2.assignedCluster(y);
                    //in C1 both are in the same cluster
                    if (cx1.getClusterId() == cy1.getClusterId()) {
                        if (cy1.getClusterId() == cy2.getClusterId()) {
                            a++;
                        } else {
                            b++;
                        }
                    } else {
                        if (cx2.getClusterId() == cy2.getClusterId()) {
                            c++;
                        } else {
                            d++;
                        }
                    }
                }
            }
        }
        double tmp = (a + b) * (a + c) + (c + d) * (b + d);
        ari = np * (a + d) - tmp;
        ari /= np * np - tmp;
        return ari;
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

        System.out.println("bimap: " + matching);
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
                System.out.println("unmatched rc: " + unmatchedClusters);
                for (String str : unmatchedClusters) {
                    rk[k++] = str;
                }
            } else {
                //more classes than actual clusters
                Set<String> unmatchedClasses = diff(matching.keySet(), cols);
                System.out.println("unmatched cc: " + unmatchedClasses);
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
     * Perform a-b operation with sets
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
    public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset) {
        // Table<String, String, Integer> table = CountingPairs.contingencyTable(clusters);
        //return countScore(table);
        //reference clustering made up from class labels
        Clustering<? extends Cluster> ref = CountingPairs.clusteringFromClasses(clusters);
        return countAri(clusters, ref);
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

    @Override
    public double getMin() {
        return -1;
    }

    @Override
    public double getMax() {
        return 1;
    }
}
