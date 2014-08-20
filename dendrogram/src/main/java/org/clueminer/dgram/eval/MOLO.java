package org.clueminer.dgram.eval;

import java.util.Stack;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.dendrogram.OptimalTreeOrder;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Dump;

/**
 *
 * @author Tomas Barton
 */
public class MOLO implements OptimalTreeOrder {

    private int[] order;
    private double[][] opt;
    private Matrix similarity;

    @Override
    public void optimize(HierarchicalResult clustering) {
        System.out.println("tree order");
        System.out.println("similarity matrix");
        similarity = clustering.getProximityMatrix();
        similarity.printLower(2, 2);
        DendroTreeData tree = clustering.getTreeData();
        numberNodes(tree.getRoot(), 0, 0);
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
        sortSmallest(tree.getRoot());
        Dump.array(tree.getMapping(), "tree mapping");
        System.out.println("score after = " + score(tree, similarity));
        tree.print();
        optOrder(tree.first(), clustering.getProximityMatrix());
        System.out.println("in order " + inOrderScore(tree.getRoot()));
        tree.createMapping(n, tree.getRoot());
        tree.updatePositions(tree.getRoot());
    }

    public DendroNode sortSmallest(DendroNode d) {
        if (d.getLeft().isLeaf() && d.getRight().isLeaf()) {
            d.setMin(d.getHeight());
            return d;
        } else if (!d.getLeft().isLeaf() && d.getRight().isLeaf()) {
            sortSmallest(d.getLeft());
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

    /**
     * Number leaves from 0 to {numNodes}
     *
     * @param node
     * @return
     */
    public void numberNodes(DendroNode node, int label, int level) {
        if (!node.isLeaf()) {
            if (node.hasLeft()) {
                numberNodes(node.getLeft(), label << 1, level + 1);
            }
            if (node.hasRight()) {
                numberNodes(node.getRight(), (label << 1) + 1, level + 1);
            }
        }
        node.setLabel(label);
        node.setLevel(level);
    }

    public int optOrder(DendroNode node, Matrix similarity) {
        if (node.isLeaf()) {
            return optOrder(node.getParent(), similarity);
        }
        return 0;
    }

    public void treeOrder(DendroNode v, DendroNode u, DendroNode r) {

    }

    /**
     * won't work we would need complete binary tree
     *
     * @param pLeft
     * @param pRight
     */
    private void subTreeOrder(int pLeft, int pRight) {
        int m = pRight - pLeft;
        int pMid;
        System.out.println("m = " + m);
        if (m == 0) {
            return;
        } else if (m == 1) {
            order[pLeft] = pRight ^ 1; //xor
            return;
        }
        int iRight = order[pRight];

        for (int j = 0; j < m; j++) {
            if (pLeft == 0) {
                opt[iRight ^ j][0] = 0;
            } else {
                opt[iRight ^ j][0] = similarity.get(order[pLeft - 1], iRight ^ j);
            }
        }
        pMid = pLeft + (m - 1) / 2;
        System.out.println("pMid = " + pMid);

        Dump.array(order, "order");
    }

}
