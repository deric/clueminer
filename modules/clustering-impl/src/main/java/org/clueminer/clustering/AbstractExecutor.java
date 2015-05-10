package org.clueminer.clustering;

import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.factory.CutoffStrategyFactory;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractExecutor implements Executor {

    protected ClusteringAlgorithm algorithm;

    @Override
    public ClusteringAlgorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public void setAlgorithm(ClusteringAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    protected CutoffStrategy getCutoffStrategy(Props params) {
        CutoffStrategy strategy;
        String cutoffAlg = params.get(AgglParams.CUTOFF_STRATEGY, "hill-climb inc");

        if (cutoffAlg.equals("-- naive --")) {
            strategy = CutoffStrategyFactory.getInstance().getDefault();
        } else {
            strategy = CutoffStrategyFactory.getInstance().getProvider(cutoffAlg);
        }
        String evalAlg = params.get(AgglParams.CUTOFF_SCORE, "AIC");
        InternalEvaluator eval = InternalEvaluatorFactory.getInstance().getProvider(evalAlg);
        strategy.setEvaluator(eval);

        return strategy;
    }

}
