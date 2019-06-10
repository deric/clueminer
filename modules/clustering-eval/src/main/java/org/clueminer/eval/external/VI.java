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
import java.util.Set;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Variation of Information
 *
 * Meilă, M.Comparing clusterings—an information based distance.Journal of
 * multi- variate analysis, , no. 5, 2007: pp. 873–895.
 *
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class VI<E extends Instance, C extends Cluster<E>> extends AbstractExternalEval<E, C> implements ClusterEvaluation<E, C> {

    private static final String NAME = "VI";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) throws ScoreException {
        CountingPairs<E, C> cp = CountingPairs.getInstance();
        Table<String, String, Integer> contTable = cp.contingencyTable(clusters);

        return vi(clusters, contTable);
    }

    @Override
    public double score(Clustering<E, C> c1, Clustering<E, C> c2, Props params) {
        CountingPairs<E, C> cp = CountingPairs.getInstance();
        Table<String, String, Integer> contTable = cp.contingencyTable(c1, c2);

        return vi(c1, contTable);
    }

    private double vi(Clustering<E, C> clusters, Table<String, String, Integer> contTable) {
        int n = clusters.instancesCount();

        return 2 * HCC(contTable, n) - HK(contTable, n) - HC(contTable, n);
    }

    /**
     * H(C, C')
     *
     * @param contTable
     * @param n
     * @return
     */
    private double HCC(Table<String, String, Integer> contTable, int n) {
        double hcc = 0.0, ack;
        double N = n;
        for (String clust : contTable.rowKeySet()) {
            for (String klass : contTable.columnKeySet()) {
                ack = value(contTable, clust, klass);
                if (ack == 0.0) {
                    continue;
                }
                hcc += ack / N * Math.log(ack / N);
            }
        }
        return hcc;
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
     * Should be minimized, minimum value is 0.0
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
        return Double.MIN_VALUE;
    }

    @Override
    public double getMax() {
        return 0.0;
    }

}
