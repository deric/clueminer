package org.clueminer.eval.hclust;

import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalClusterEvaluator;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.InternalEvaluator;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = CutoffStrategy.class)
public class CophCutoff implements CutoffStrategy {

    private static final String name = "coph cutoff";
    private final HierarchicalClusterEvaluator eval = new CopheneticCorrelation();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double findCutoff(HierarchicalResult hclust) {
        Clustering clust, prevClust = null;
        double cutoff;
        double score, prev = Double.NaN, oldcut = 0;
        int level = 1;
        boolean isClimbing = true;
        do {
            cutoff = hclust.cutTreeByLevel(level);
            clust = hclust.getClustering();
            System.out.println("# level: " + level + ", clust = " + clust + ", cut = " + String.format("%.2f", cutoff));
            score = eval.score(hclust);
            if (cutoff < 0) {
                System.out.println("negative cutoff " + cutoff + " stopping cutoff");
                isClimbing = false;
            }
            System.out.println("score = " + score + " prev= " + prev);
            if (!Double.isNaN(prev)) {
                if (score <= prev) {
                    System.out.println("function is not climbing anymore, reverting to " + oldcut);
                    hclust.setCutoff(oldcut);
                    hclust.setClustering(prevClust);
                    return oldcut;
                }
            }

            prev = score;
            prevClust = clust;
            oldcut = cutoff;
            level++;
            System.out.println("res clustering size: " + hclust.getClustering().size());

        } while (level < (hclust.treeLevels() - 1) && isClimbing && !Double.isNaN(score));
        return cutoff;
    }

    @Override
    public void setEvaluator(InternalEvaluator evaluator) {
        //nothing to do
    }

}
