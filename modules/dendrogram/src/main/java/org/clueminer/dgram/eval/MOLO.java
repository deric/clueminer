package org.clueminer.dgram.eval;

import java.util.Stack;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.dendrogram.OptimalTreeOrder;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Dump;
import org.openide.util.lookup.ServiceProvider;

/**
 * A heuristic algorithm for dendrogram leaf ordering with low computing
 * complexity (basically O(n/2))
 *
 * Sakai R, Winand R, Verbeiren T et al. dendsort: modular leaf ordering methods
 * for dendrogram representations in R [v1; ref status: awaiting peer review,
 * http://f1000r.es/3xw] F1000Research 2014, 3:177 (doi:
 * 10.12688/f1000research.4784.1)
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = OptimalTreeOrder.class)
public class MOLO implements OptimalTreeOrder {

    private Matrix similarity;
    public static final String name = "MOLO";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void optimize(HierarchicalResult clustering, boolean reverse) {
        similarity = clustering.getProximityMatrix();
        similarity.printLower(2, 2);
        DendroTreeData tree = clustering.getTreeData();
        //tree.print();
        Dump.array(tree.getMapping(), "tree mapping");
        tree.print();
        int n = tree.numLeaves();
        /* order = new int[n];
         opt = new double[n - 1][n - 1];
         //order[n - 1] = 0;
         subTreeOrder(0, n - 1);
         */
        System.out.println("score before = " + score(tree, similarity));
        System.out.println("in order " + inOrderScore(tree.getRoot()));
        //tree.swapChildren(tree.getRoot());
        if (reverse) {
            sortSmallestReverse(tree.getRoot());
        } else {
            sortSmallest(tree.getRoot());
        }

        Dump.array(tree.getMapping(), "tree mapping");
        System.out.println("score after = " + score(tree, similarity));
        tree.print();

        System.out.println("in order " + inOrderScore(tree.getRoot()));
        tree.createMapping(n, tree.getRoot());
        tree.updatePositions(tree.getRoot());
    }

    @Override
    public void optimize(HierarchicalResult clustering) {
        optimize(clustering, false);
    }

    public DendroNode sortSmallest(DendroNode d) {
        double min;
        if (d.getLeft().isLeaf() && d.getRight().isLeaf()) {
            d.setMin(d.getHeight());
            return d;
        } else if (!d.getLeft().isLeaf() && d.getRight().isLeaf()) {
            //right is leaf
            sortSmallest(d.getLeft());
            min = Math.min(d.getHeight(), d.getLeft().getHeight());
            //System.out.println("!L: " + d.getHeight() + " vs R: " + d.getLeft().getHeight());
            d.setMin(min);
            return d;
        } else if (d.getLeft().isLeaf() && !d.getRight().isLeaf()) {
            //left if leaf
            sortSmallest(d.getRight());
            min = Math.min(d.getHeight(), d.getRight().getHeight());
            d.swapChildren();
            //System.out.println("L: " + d.getHeight() + " vs !R: " + d.getRight().getHeight());
            d.setMin(min);
        } else {
            //both branches are subtrees
            sortSmallest(d.getLeft());
            sortSmallest(d.getRight());
            //System.out.println("L: " + d.getLeft().getHeight() + " vs R: " + d.getRight().getHeight());
            if (d.getLeft().getMin() >= d.getRight().getMin()) {
                d.swapChildren();
            }
            min = Math.min(d.getRight().getMin(), d.getLeft().getMin());
            d.setMin(min);
        }
        return d;
    }

    public DendroNode sortSmallestReverse(DendroNode d) {
        double min;
        if (d.getLeft().isLeaf() && d.getRight().isLeaf()) {
            d.setMin(d.getHeight());
            return d;
        } else if (!d.getLeft().isLeaf() && d.getRight().isLeaf()) {
            //right is leaf
            sortSmallest(d.getLeft());
            min = Math.min(d.getHeight(), d.getLeft().getHeight());
            d.swapChildren();
            System.out.println("!L: " + d.getHeight() + " vs R: " + d.getLeft().getHeight());
            d.setMin(min);
            return d;
        } else if (d.getLeft().isLeaf() && !d.getRight().isLeaf()) {
            //left if leaf
            sortSmallest(d.getRight());
            min = Math.min(d.getHeight(), d.getRight().getHeight());
            System.out.println("L: " + d.getHeight() + " vs !R: " + d.getRight().getHeight());
            d.setMin(min);
        } else {
            //both branches are subtrees
            sortSmallest(d.getLeft());
            sortSmallest(d.getRight());
            System.out.println("L: " + d.getLeft().getHeight() + " vs R: " + d.getRight().getHeight());
            if (d.getLeft().getMin() < d.getRight().getMin()) {
                d.swapChildren();
            }
            min = Math.min(d.getRight().getMin(), d.getLeft().getMin());
            d.setMin(min);
        }
        return d;
    }

    /**
     * Compute score as in-order walk of underlying nodes (after swapping any
     * nodes in the tree updating mapping would have same complexity)
     *
     * @param node
     * @return
     */
    public double inOrderScore(DendroNode node) {
        Stack<DendroNode> stack = new Stack<DendroNode>();
        DendroNode prev = null;
        double score = 0.0;
        while (!stack.isEmpty() || node != null) {
            if (node != null) {
                stack.push(node);
                node = node.getLeft();
            } else {
                node = stack.pop();
                if (node.isLeaf()) {
                    //node.setPosition(i);
                    if (prev != null) {
                        //System.out.println("dist (" + prev.getIndex() + ", " + node.getIndex() + " ) = " + similarity.get(prev.getIndex(), node.getIndex()));
                        score += similarity.get(prev.getIndex(), node.getIndex());
                    }
                    prev = node;
                }
                node = node.getRight();
            }

        }
        return score;
    }

    /**
     * We're trying to minimize distance between tree nodes that are next to
     * each other
     *
     * @param tree
     * @param similarity
     * @return
     */
    private double score(DendroTreeData tree, Matrix similarity) {
        double score = 0.0;
        int[] mapping = tree.getMapping();
        for (int i = 0; i < tree.numLeaves() - 1; i++) {
            score += similarity.get(mapping[i], mapping[i + 1]);
        }
        return score;
    }
}
