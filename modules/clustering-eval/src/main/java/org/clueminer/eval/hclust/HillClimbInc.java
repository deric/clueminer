package org.clueminer.eval.hclust;

import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = CutoffStrategy.class)
public class HillClimbInc extends HillClimbCutoff implements CutoffStrategy {

    private static final String name = "hill-climb inc";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double findCutoff(HierarchicalResult hclust) {
        Clustering clust;
        double cutoff;
        double score, prev = Double.NaN, oldcut = 0;
        int level = 1;
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
                if (!evaluator.compareScore(score, prev)) {
                    isClimbing = false;
                    System.out.println("function is not climbing anymore");
                    hclust.updateCutoff(oldcut);
                }
            }
            hclust.setScores(evaluator.getName(), clust.size(), score);
            prev = score;
            oldcut = cutoff;
            level++;

        } while (level < (hclust.treeLevels() - 1) && isClimbing && !Double.isNaN(score));
        return cutoff;
    }

}
