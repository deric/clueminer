package org.clueminer.eval.hclust;

import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = CutoffStrategy.class)
public class NaiveCutoff implements CutoffStrategy {

    public static final String name = "naive cutoff";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double findCutoff(HierarchicalResult hclust) {
        double res;
        /**
         * TODO remove this in next version
         */
        try {
            res = findCutoffOld(hclust);
        } catch (Exception e) {
            res = findCutoffNg(hclust);
        }
        return res;
    }

    /**
     *
     * @param hclust
     * @return
     * @deprecated
     */
    public double findCutoffOld(HierarchicalResult hclust) {
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

    /**
     * Search for the highest distance between tree levels (might determine a
     * reasonable number of clusters - simple, but not very precise)
     *
     * @param hclust
     * @return
     */
    private double findCutoffNg(HierarchicalResult hclust) {
        DendroNode current = hclust.getTreeData().getRoot();
        double max = Double.MIN_VALUE;
        double distLeft = findCut(current.getLeft(), max);
        double distRight = findCut(current.getRight(), max);

        if (distLeft > distRight) {
            return distLeft;
        }

        return distRight;
    }

    private double findCut(DendroNode node, double max) {
        double dist = distance(node.getParent(), node);
        double distLeft, distRight;
        if (dist > max) {
            max = dist;
        }

        if (!node.isLeaf()) {
            distLeft = findCut(node.getLeft(), max);
            distRight = findCut(node.getRight(), max);
            if (distLeft > distRight) {
                if (distLeft > max) {
                    max = distLeft;
                }
            } else if (distRight > max) {
                max = distRight;
            }
        }
        return max;
    }

    private double distance(DendroNode parent, DendroNode child) {
        return (parent.getHeight() - child.getHeight()) / 2 + child.getHeight();
    }

    @Override
    public void setEvaluator(InternalEvaluator evaluator) {
        //nothing to do
    }

}
