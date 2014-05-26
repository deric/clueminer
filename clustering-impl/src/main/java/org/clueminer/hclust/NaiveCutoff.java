package org.clueminer.hclust;

import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;

/**
 *
 * @author Tomas Barton
 */
public class NaiveCutoff implements CutoffStrategy {

    /**
     * Search for the highest distance between tree levels (might determine a
     * reasonable number of clusters - simple, but not very precise)
     *
     * @param hclust
     * @return
     */
    @Override
    public double findCutoff(HierarchicalResult hclust) {
        double lower = 0.0, upper, dist;
        double max = Double.MIN_VALUE;
        double max_l = 0.0;
        int idx;
        for (int i = 0; i < hclust.treeLevels(); i++) {
            idx = hclust.treeOrder(i);
            upper = hclust.treeHeightAt(idx);
            dist = upper - lower;
            if (dist > max) {
                max = dist;
                max_l = lower; //lower part of the branch
            }
            //upper becomes lower, moving to next level
            lower = upper;
        }
        return (max / 2 + max_l);
    }
}
