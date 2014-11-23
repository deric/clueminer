package org.clueminer.clustering;

import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.factory.CutoffStrategyFactory;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractExecutor implements Executor {

    protected AgglomerativeClustering algorithm;

    @Override
    public AgglomerativeClustering getAlgorithm() {
        return algorithm;
    }

    @Override
    public void setAlgorithm(AgglomerativeClustering algorithm) {
        this.algorithm = algorithm;
    }

    protected CutoffStrategy getCutoffStrategy(Props params) {
        CutoffStrategy strategy;
        String cutoffAlg = params.get(AgglParams.CUTOFF_STRATEGY, "hill-climb inc");

        if (cutoffAlg.equals("-- naive --")) {
            String evalAlg = params.get(AgglParams.CUTOFF_SCORE, "AIC score");
            InternalEvaluator eval = InternalEvaluatorFactory.getInstance().getProvider(evalAlg);
            strategy = CutoffStrategyFactory.getInstance().getDefault();
            strategy.setEvaluator(eval);
        } else {
            strategy = CutoffStrategyFactory.getInstance().getProvider(cutoffAlg);
        }
        return strategy;
    }

}
