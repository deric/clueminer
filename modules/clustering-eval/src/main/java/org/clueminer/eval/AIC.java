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
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.LogLikelihoodFunction;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Distance function is not used in this score.
 *
 * @author Andreas De Rijcke
 * @author Thomas Abeel
 * @param <E>
 * @param <C>
 *
 * @see
 * AKAIKE, H. A new look at the statistical model identification. IEEE
 * Transactions on Automatic Control 19, 6 (December 1974), 716–723.
 *
 */
@ServiceProvider(service = InternalEvaluator.class)
public class AIC<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final String NAME = "AIC";
    private static final long serialVersionUID = -8805325971847590600L;
    private static final LogLikelihoodFunction likelihood = new LogLikelihoodFunction();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        // number of free parameters K
        double k = clusters.size();
        likelihood.setAlpha0(params.getDouble("likelihood.alpha", 0.1));
        likelihood.setBeta0(params.getDouble("likelihood.beta", 0.1));
        likelihood.setLambda0(params.getDouble("likelihood.lambda", 0.1));
        likelihood.setMu0(params.getDouble("likelihood.mu", 0.0));
        // loglikelihood log(L)
        // AIC score
        return 2 * k - 2 * likelihood.sum(clusters);
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
        // should be minimalized
        return compare(score1, score2) < 0;
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
