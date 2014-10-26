package org.clueminer.clustering.benchmark;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public abstract class Container implements Runnable {

    private HierarchicalResult result;
    private final AgglomerativeClustering algorithm;
    private final Dataset<? extends Instance> dataset;
    private static final double delta = 1e-9;
    private static final Logger logger = Logger.getLogger(Container.class.getName());

    public Container(AgglomerativeClustering algorithm, Dataset<? extends Instance> dataset) {
        this.algorithm = algorithm;
        this.dataset = dataset;
    }

    public abstract HierarchicalResult hierarchical(AgglomerativeClustering algorithm, Dataset<? extends Instance> dataset, Props params);

    @Override
    public void run() {
        Props params = new Props();
        this.result = hierarchical(algorithm, dataset, params);
    }

    public boolean equals(Container other) {
        if (this.result == null || other.result == null) {
            throw new RuntimeException("got null result. this = " + result + " other = " + other);
        }
        return treeDiff(this.result, other.result);
    }

    private boolean treeDiff(HierarchicalResult result, HierarchicalResult other) {
        boolean same = true;
        if (result.getTreeData() == null || other.getTreeData() == null) {
            throw new RuntimeException("got null tree data. this = " + result + " other = " + other);
        }

        if (result.getTreeData().numLeaves() != other.getTreeData().numLeaves()) {
            logger.log(Level.WARNING, "different number of leaves! {0} vs. {1}",
                       new Object[]{result.getTreeData().numLeaves(), other.getTreeData().numLeaves()});
            return false;
        }
        System.out.println("first:");
        result.getTreeData().print();
        System.out.println("other:");
        other.getTreeData().print();

        DendroNode rootA = result.getTreeData().getRoot();
        DendroNode rootB = other.getTreeData().getRoot();

        same &= expectHeight(rootA, rootB);
        /**
         * TODO: recursive tree check
         */

        return same;
    }

    public static boolean almostEqual(double a, double b, double eps) {
        return Math.abs(a - b) < eps;
    }

    private boolean expectHeight(DendroNode rootA, DendroNode rootB) {
        if (almostEqual(rootA.getHeight(), rootB.getHeight(), delta)) {
            return true;
        }
        System.out.println(": " + rootA.getHeight() + " vs " + rootB.getHeight());
        logger.log(Level.WARNING, "different node height {0} vs. {1} at nodes #{2} - #{3}",
                   new Object[]{rootA.getHeight(), rootB.getHeight(), rootA.getId(), rootB.getId()});
        return false;
    }

}
