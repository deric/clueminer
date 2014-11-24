package org.clueminer.eval.hclust;

import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.openide.util.lookup.ServiceProvider;

/**
 * Incremental strategy starts from tree root (should be faster when we expect
 * much smaller number of clusters than instances in data)
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
        check();
        Clustering clust, prevClust = null;
        double cutoff;
        double score, prev = Double.NaN, oldcut = 0;
        int level = 1;
        boolean isClimbing = true;
        String evalName;
        int clustNum;
        do {
            cutoff = hclust.cutTreeByLevel(level);
            clust = hclust.getClustering();
            System.out.println("# level: " + level + ", clust = " + clust + ", cut = " + String.format("%.2f", cutoff));
            evalName = evaluator.getName();
            clustNum = clust.size();
            if (hclust.isScoreCached(evalName, clustNum)) {
                System.out.println("score cached");
                score = hclust.getScore(evalName, clustNum);
            } else {
                score = evaluator.score(clust, hclust.getDataset());
            }
            if (cutoff < 0) {
                System.out.println("negative cutoff " + cutoff + " stopping cutoff");
                isClimbing = false;
            }
            System.out.println("score = " + score + " prev= " + prev);
            hclust.setScores(evaluator.getName(), clust.size(), score);
            if (!Double.isNaN(prev)) {
                if (!evaluator.isBetter(score, prev)) {
                    System.out.println("function is not climbing anymore, reverting");
                    hclust.setCutoff(oldcut);
                    hclust.setClustering(prevClust);
                    return oldcut;
                }
            }
            prev = score;
            oldcut = cutoff;
            prevClust = clust;
            level++;

        } while (level < (hclust.treeLevels() - 1) && isClimbing && !Double.isNaN(score));
        return cutoff;
    }

}
