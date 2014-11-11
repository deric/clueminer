package org.clueminer.eval.hclust;

import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = CutoffStrategy.class)
public class HillClimbCutoff implements CutoffStrategy {

    protected InternalEvaluator evaluator;
    private static final String name = "hill-climb cutoff";

    public HillClimbCutoff() {
        //evaluator must be set after calling constructor!
    }

    public HillClimbCutoff(InternalEvaluator eval) {
        this.evaluator = eval;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double findCutoff(HierarchicalResult hclust) {
        double cutoff;
        Clustering clust;
        double score, prev = Double.NaN, oldcut = 0;
        int level = hclust.treeLevels() - 1;
        boolean isClimbing = true;
        String evalName;
        int clustNum;
        do {
            hclust.cutTreeByLevel(level);
            clust = hclust.getClustering();
            System.out.println("clust = " + clust);
            cutoff = hclust.getCutoff();
            evalName = evaluator.getName();
            clustNum = clust.size();
            System.out.println("we have " + clust.size() + " clusters");
            if (hclust.isScoreCached(evalName, clustNum)) {
                System.out.println("score cached");
                score = hclust.getScore(evalName, clustNum);
            } else {
                score = evaluator.score(clust, hclust.getDataset());
            }
            System.out.println("score = " + score + " prev= " + prev);
            if (!Double.isNaN(prev)) {
                if (!evaluator.isBetter(score, prev)) {
                    isClimbing = false;
                    System.out.println("function is not climbing anymore");
                    hclust.updateCutoff(oldcut);
                }
            }
            hclust.setScores(evaluator.getName(), clust.size(), score);
            prev = score;
            oldcut = cutoff;
            level--;

        } while (isClimbing && !Double.isNaN(score));
        return cutoff;
    }

    public InternalEvaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(InternalEvaluator evaluator) {
        this.evaluator = evaluator;
    }
}
