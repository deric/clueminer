package org.clueminer.eval.hclust;

import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = CutoffStrategy.class)
public class FirstJump implements CutoffStrategy {

    public static final String name = "First jump cutoff";

    // Values with best average performance found by several experiments
    // Values 100 and 2 give much better results on specific datasets but not on average
    private int start = 340;
    private double factor = 1.91;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double findCutoff(HierarchicalResult hclust, Props params) {
        double res;

        res = tryJumps(hclust);

        return res;
    }

    /**
     * Look for first jump several times higher than the average. If no jump
     * exists, decrease the threshold and repeat.
     *
     * @param hclust
     * @return
     */
    public double tryJumps(HierarchicalResult hclust) {
        double average = computeAverageHeight(hclust);
        for (int i = start; i >= 0; i /= factor) {
            double result = findFirstJump(hclust, average * i);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    /**
     * Find first jump bigger than the given size in the upper half of the
     * dendrogram.
     *
     * @param hclust
     * @param jumpHeight size of the jump
     * @return height of the first jump or 0 if not found
     */
    private double findFirstJump(HierarchicalResult hclust, double jumpHeight) {
        double upper;
        double lower = hclust.getHeightByLevel(hclust.treeLevels() / 2 - 1);
        for (int i = hclust.treeLevels() / 2; i < hclust.treeLevels() + 1; i++) {
            upper = hclust.getHeightByLevel(i);

            if (upper - lower > jumpHeight) {
                return lower + (upper - lower) / 2;
            }

            lower = upper;
        }
        return 0;
    }

    /**
     * Find average distance in the first half of levels.
     *
     * @param hclust
     * @return
     */
    private double computeAverageHeight(HierarchicalResult hclust) {
        double sum = 0;
        double upper;
        double lower = hclust.getHeightByLevel(0);
        for (int i = 1; i <= hclust.treeLevels() / 2; i++) {
            upper = hclust.getHeightByLevel(i);
            sum += upper - lower;
            lower = upper;
        }
        if (sum == 0) {
            return 1;
        }
        return sum / (hclust.treeLevels() / 2);
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    @Override
    public void setEvaluator(InternalEvaluator evaluator) {
        //nothing to do
    }

}
