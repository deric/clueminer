/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.exec;

import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.factory.CutoffStrategyFactory;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public abstract class AbstractExecutor<E extends Instance, C extends Cluster<E>> implements Executor<E, C> {

    protected ClusteringAlgorithm<E, C> algorithm;

    @Override
    public ClusteringAlgorithm<E, C> getAlgorithm() {
        return algorithm;
    }

    @Override
    public void setAlgorithm(ClusteringAlgorithm<E, C> algorithm) {
        this.algorithm = algorithm;
    }

    public CutoffStrategy<E, C> getCutoffStrategy(Props params) {
        CutoffStrategy<E, C> strategy;
        String cutoffAlg = params.get(AlgParams.CUTOFF_STRATEGY, "hill-climb inc");

        if (cutoffAlg.equals("-- naive --")) {
            strategy = CutoffStrategyFactory.getInstance().getDefault();
        } else {
            strategy = CutoffStrategyFactory.getInstance().getProvider(cutoffAlg);
        }
        String evalAlg = params.get(AlgParams.CUTOFF_SCORE, "ALE");
        InternalEvaluatorFactory<E, C> ief = InternalEvaluatorFactory.getInstance();
        InternalEvaluator<E, C> eval = ief.getProvider(evalAlg);
        strategy.setEvaluator(eval);

        return strategy;
    }

}
