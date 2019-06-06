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
package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.JamaMatrix;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Akaike Information Criterion - A likelihood clustering quality estimator
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 *
 * @see AKAIKE, H. A new look at the statistical model identification. IEEE
 * Transactions on Automatic Control 19, 6 (December 1974), 716–723.
 *
 * @cite Bozdogan, H.; Sclove, S. L. Multi-sample cluster analysis using
 * Akaike’s information criterion. Annals of the Institute of Statistical
 * Mathematics, volume 36, no. 1, 1984: pp. 163–180.
 *
 *
 */
@ServiceProvider(service = InternalEvaluator.class)
public class AIC<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final String NAME = "AIC";
    private static final long serialVersionUID = -7805325971847590611L;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        Dataset<E> dataset = clusters.getLookup().lookup(Dataset.class);
        int n = dataset.size();
        int k = clusters.size();
        int d = dataset.attributeCount();
        double aic = n * d * Math.log(2 * Math.PI);

        aic += clusteringLoglikehood(clusters, d);
        aic += n * d + 2 * (k * d + k * d * (d + 1) / 2.0);
        return aic;
    }

    public double clusteringLoglikehood(Clustering<E, C> clusters, int d) {
        double loglike = 0.0;
        double ln;
        for (C clust : clusters) {
            //determinant can't be computed for singleton clusters
            if (clust.size() > 1) {
                ln = Math.log(likelihood(clust, d));
                /* if (Double.isInfinite(ln) || Double.isNaN(ln)) {
                    ln = 0;
                }*/
                loglike += clust.size() * ln;
            }
        }
        return loglike;
    }

    /**
     * Sum of loglikelihood of each attribute
     *
     * @param cluster
     * @param d data dimensionality
     * @return
     */
    public double likelihood(C cluster, int d) {
        Matrix A = new JamaMatrix(cluster.size(), d);
        E avg = cluster.getCentroid();

        for (int i = 0; i < cluster.size(); i++) {
            for (int j = 0; j < d; j++) {
                A.set(i, j, cluster.get(i, j) - avg.get(j));
            }
        }
        Matrix R = A.transpose().times(A);
        R.timesEquals(1.0 / (double) cluster.size());
        double det = R.det();
        if (det <= 0) {
            return 1;
        }
        return det;
    }

    /**
     * Compares the two scores AIC scores. Returns true if the first score is
     * 'better' than the second score.
     *
     *
     * don't use abs values
     *
     * @link http://stats.stackexchange.com/questions/84076/negative-values-for-aic-in-general-mixed-model
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public boolean isBetter(double score1, double score2) {
        return compare(score1, score2) > 0;
    }

    @Override
    public boolean isMaximized() {
        return false;
    }

    @Override
    public double getMin() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getMax() {
        return Double.NEGATIVE_INFINITY;
    }

}
