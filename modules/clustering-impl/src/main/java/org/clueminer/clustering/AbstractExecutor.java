package org.clueminer.clustering;

import org.clueminer.clustering.api.AgglParams;
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

    protected CutoffStrategy<E, C> getCutoffStrategy(Props params) {
        CutoffStrategy<E, C> strategy;
        String cutoffAlg = params.get(AgglParams.CUTOFF_STRATEGY, "hill-climb inc");

        if (cutoffAlg.equals("-- naive --")) {
            strategy = CutoffStrategyFactory.getInstance().getDefault();
        } else {
            strategy = CutoffStrategyFactory.getInstance().getProvider(cutoffAlg);
        }
        String evalAlg = params.get(AgglParams.CUTOFF_SCORE, "AIC");
        InternalEvaluatorFactory<E, C> ief = InternalEvaluatorFactory.getInstance();
        InternalEvaluator<E, C> eval = ief.getProvider(evalAlg);
        strategy.setEvaluator(eval);

        return strategy;
    }

}
