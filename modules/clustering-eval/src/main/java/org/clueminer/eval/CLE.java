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
 * Likelihood estimator based on centroid distance
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = InternalEvaluator.class)
public class CLE<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final String NAME = "CLE";
    private static final long serialVersionUID = 905325971847590600L;
    private static final LogLikelihoodFunction LLF = new LogLikelihoodFunction();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        LLF.setAlpha0(params.getDouble("likelihood.alpha", 0.1));
        LLF.setBeta0(params.getDouble("likelihood.beta", 0.1));
        LLF.setLambda0(params.getDouble("likelihood.lambda", 0.1));
        LLF.setMu0(params.getDouble("likelihood.mu", 0.0));
        // loglikelihood log(L)
        return LLF.centroidSum(clusters);
    }

    /**
     * Compares the two scores
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public boolean isBetter(double score1, double score2) {
        return compare(score1, score2) < 0;
    }

    @Override
    public boolean isMaximized() {
        return true;
    }

    @Override
    public double getMin() {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public double getMax() {
        return Double.POSITIVE_INFINITY;
    }

}
