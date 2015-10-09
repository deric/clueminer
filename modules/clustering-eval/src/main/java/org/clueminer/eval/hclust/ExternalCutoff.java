package org.clueminer.eval.hclust;

import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.eval.external.NMIsqrt;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Fake cutoff based on the external evaluation.
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = CutoffStrategy.class)
public class ExternalCutoff implements CutoffStrategy {

    private static final String name = "External_cutoff";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double findCutoff(HierarchicalResult hclust, Props params) {
        double bestScore = Double.MIN_VALUE;
        NMIsqrt evaluator = new NMIsqrt();
        double cutoff = 0;
        for (int i = 0; i <= hclust.treeLevels(); i++) {
            double height = hclust.cutTreeByLevel(i);
            double score = evaluator.score(hclust.getClustering());
            if (score > bestScore) {
                bestScore = score;
                cutoff = height;
            }
        }
        return cutoff;
    }

    @Override
    public void setEvaluator(InternalEvaluator evaluator) {
        // nothing to do
    }

}
