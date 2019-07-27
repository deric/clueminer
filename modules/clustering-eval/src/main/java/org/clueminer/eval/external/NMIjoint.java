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

import com.google.common.collect.Table;
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
 * NMI joint
 *
 * Yao, Y. Y. "Information-theoretic measures for knowledge discovery and data
 * mining." Entropy measures, maximum entropy principle and emerging
 * applications. Springer, Berlin, Heidelberg, 2003. 115-136.
 *
 * @author deric
 * @param <E>
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class NMIjoint<E extends Instance, C extends Cluster<E>> extends AbstractExternalEval<E, C> implements ClusterEvaluation<E, C> {

    private static final String NAME = "NMI-joint";

    @Override
    public String getName() {
        return NAME;
    }

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

        double mu = mutualInformation(table, N);
        double hck = -HCK(table, N);

        return mu / hck;
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
        int n = c1.instancesCount();

        if (n != c2.instancesCount()) {
            throw new RuntimeException("clusterings have different numbers of instances");
        }

        Table<String, String, Integer> table = CountingPairs.contingencyTable(c1, c2);

        double mu = mutualInformation(table, n);
        double hck = -HCK(table, n);

        return mu / hck;
    }

    private double HCK(Table<String, String, Integer> table, int n) {
        double hck = 0.0, ack;
        for (String clust : table.rowKeySet()) {
            for (String klass : table.columnKeySet()) {
                ack = value(table, clust, klass);
                if (ack == 0.0) {
                    continue;
                }
                hck += (ack / n) * Math.log(ack / n);
            }
        }
        return hck;
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
