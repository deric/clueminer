package org.clueminer.eval.hclust;

import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = CutoffStrategy.class)
public class NaiveCutoffByLevel implements CutoffStrategy {

    public static final String name = "Naive cutoff by level";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double findCutoff(HierarchicalResult hclust, Props params) {

        return findCutoffLevel(hclust);

    }

    public double findCutoffLevel(HierarchicalResult hclust) {
        double height = 0;
        double max = 0;
        double upper;
        double lower = hclust.cutTreeByLevel(0);
        for (int i = 1; i < hclust.treeLevels() + 1; i++) {
            upper = hclust.getHeightByLevel(i);
            if (upper - lower >= max) {
                max = upper - lower;
                height = (upper - lower) / 2.0 + lower;
            }
            lower = upper;
        }
        return height;
    }

    private double distance(DendroNode parent, DendroNode child) {
        return (parent.getHeight() - child.getHeight()) / 2 + child.getHeight();
    }

    @Override
    public void setEvaluator(InternalEvaluator evaluator) {
        //nothing to do
    }

}
