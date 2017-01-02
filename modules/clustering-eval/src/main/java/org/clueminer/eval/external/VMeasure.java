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

import com.google.common.collect.Table;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * "Rosenberg, Andrew; Hirschberg, Julia; ",V-measure: A conditional entropy-based
 * external cluster evaluation measure,Proceedings of the 2007 Joint Conference
 * on Empirical Methods in Natural Language Processing and Computational Natural
 * Language Learning (EMNLP-CoNLL),,,410-420,2007,
 *
 * @param <E>
 * @param <C>
 * @see https://scholar.google.com/citations?view_op=view_citation&hl=en&user=40bq19cAAAAJ&citation_for_view=40bq19cAAAAJ:u5HHmVD_uO8C
 *
 * @author deric
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class VMeasure<E extends Instance, C extends Cluster<E>> extends AbstractExternalEval<E, C> implements ClusterEvaluation<E, C> {

    public final static String NAME = "V-measure";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters) {
        return score(clusters, new Props());
    }

    /**
     * Computes V-measure for given clustering and dataset with class labels.
     *
     * When beta is greater than 1.0, completeness is weighted more strongly in the
     * calculation. If beta is lower than 1.0, homogeneity is weighted more strongly.
     *
     * @param clusters
     * @param params
     * @return
     */
    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        CountingPairs<E, C> cp = CountingPairs.getInstance();
        Table<String, String, Integer> contTable = cp.contingencyTable(clusters);

        return vmeasure(clusters, contTable, params);
    }

    private double vmeasure(Clustering<E, C> clusters, Table<String, String, Integer> contTable, Props params) {
        double beta = params.getDouble("v-beta", 1.0);

        int n = clusters.instancesCount();
        //H(C)
        double h_c = HC(contTable, n);
        double homogeneity = h_c == 0.0 ? 1 : (1 - HCK(contTable, n) / h_c);
        //H(K)
        double h_k = HK(contTable, n);
        double completeness = h_k == 0.0 ? 1 : (1 - HKC(contTable, n) / h_k);

        return (1.0 + beta) * homogeneity * completeness / (beta * homogeneity + completeness);
    }

    private double HK(Table<String, String, Integer> contTable, int n) {
        double h_c = 0.0;
        double skall;
        for (String cluster : contTable.rowKeySet()) {
            skall = sumKlass(contTable, cluster) / (double) n;
            h_c += skall * Math.log(skall);
        }
        return h_c;
    }

    private double HC(Table<String, String, Integer> contTable, int n) {
        double h_c = 0.0;
        double scall;
        for (String klass : contTable.columnKeySet()) {
            scall = sumCluster(contTable, klass) / (double) n;
            h_c += scall * Math.log(scall);
        }
        return h_c;
    }

    private double HCK(Table<String, String, Integer> contTable, int n) {
        double h_c_k = 0.0, ack;
        for (String clust : contTable.rowKeySet()) {
            for (String klass : contTable.columnKeySet()) {
                ack = value(contTable, clust, klass);
                if (ack == 0.0) {
                    continue;
                }
                h_c_k += ack / n * Math.log(ack / sumKlass(contTable, clust));
            }
        }
        return h_c_k;
    }

    private double HKC(Table<String, String, Integer> contTable, int n) {
        double h_k_c = 0.0, ack;
        for (String klass : contTable.columnKeySet()) {
            for (String clust : contTable.rowKeySet()) {
                ack = value(contTable, clust, klass);
                if (ack == 0.0) {
                    continue;
                }
                h_k_c += ack / (double) n * Math.log(ack / sumCluster(contTable, klass));
            }
        }
        return h_k_c;
    }

    /**
     * Sum occurrences of given cluster in all classes
     *
     * @param contTable
     * @param klass
     * @return
     */
    private double sumKlass(Table<String, String, Integer> contTable, String clust) {
        double sum = 0.0;
        for (String klass : contTable.columnKeySet()) {
            sum += value(contTable, clust, klass);
        }
        return sum;
    }

    private double sumCluster(Table<String, String, Integer> contTable, String klass) {
        double sum = 0.0;
        for (String clust : contTable.rowKeySet()) {
            sum += value(contTable, clust, klass);
        }
        return sum;
    }

    /**
     * Retrieve value from contingency table as Double
     *
     * @param contTable
     * @param cluster
     * @param klass
     * @return
     */
    private double value(Table<String, String, Integer> contTable, String cluster, String klass) {
        Integer i = contTable.get(cluster, klass);
        if (i == null) {
            return 0.0;
        }
        return i.doubleValue();
    }

    @Override
    public double score(Clustering<E, C> clusters, Matrix proximity, Props params) {
        return score(clusters, params);
    }

    @Override
    public boolean isMaximized() {
        return true;
    }

    @Override
    public double getMin() {
        return 0.0;
    }

    @Override
    public double getMax() {
        return 1.0;
    }

    @Override
    public double score(Clustering<E, C> c1, Clustering<E, C> c2, Props params) {
        CountingPairs<E, C> cp = CountingPairs.getInstance();
        Table<String, String, Integer> contTable = cp.contingencyTable(c1, c2);

        return vmeasure(c1, contTable, params);
    }

}
