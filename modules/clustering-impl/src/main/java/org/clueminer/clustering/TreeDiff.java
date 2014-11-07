package org.clueminer.clustering;

import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;

/**
 *
 * @author Tomas Barton
 */
public class TreeDiff {

    private static final double delta = 1e-9;

    public static boolean compare(HierarchicalResult result, HierarchicalResult other) {
        boolean same = true;
        if (result.getTreeData() == null || other.getTreeData() == null) {
            throw new RuntimeException("got null tree data. this = " + result + " other = " + other);
        }

        if (result.getTreeData().numLeaves() != other.getTreeData().numLeaves()) {
            System.out.println("different number of leaves! " + result.getTreeData().numLeaves() + " vs. " + other.getTreeData().numLeaves());
            return false;
        }

        DendroNode rootA = result.getTreeData().getRoot();
        DendroNode rootB = other.getTreeData().getRoot();

        try {
            same &= sameSubTree(rootA, rootB);
        } catch (TreeException e) {
            System.out.println("first - " + result.getParams().get(AgglParams.ALG));
            result.getTreeData().print();
            System.out.println("second - " + other.getParams().get(AgglParams.ALG));
            other.getTreeData().print();
            System.out.println("=============");
            System.out.println("problems:");

            System.out.println("first:");
            result.getTreeData().print(e.getNodeA());
            System.out.println("second:");
            result.getTreeData().print(e.getNodeB());
            return false;
        }

        return same;
    }

    public static boolean almostEqual(double a, double b, double eps) {
        return Math.abs(a - b) < eps;
    }

    private static boolean sameSubTree(DendroNode nodeA, DendroNode nodeB) {
        if (sameNodeId(nodeA, nodeB) && sameHeight(nodeA, nodeB)) {
            if (nodeA.isLeaf() && nodeB.isLeaf()) {
                return true;
            }
            if (sameNodeId(nodeA.getLeft(), nodeB.getLeft())) {
                return sameSubTree(nodeA.getLeft(), nodeB.getLeft());
            } else if (sameNodeId(nodeA.getLeft(), nodeB.getRight())) {
                return sameSubTree(nodeA.getLeft(), nodeB.getRight());
            } else {
                System.out.println("subtrees does not match:");
                throw new TreeException(nodeA, nodeB);
            }
        }
        return false;
    }

    private static boolean sameNodeId(DendroNode nodeA, DendroNode nodeB) {
        if (nodeA.getId() == nodeB.getId()) {
            return true;
        }
        System.out.println(": " + nodeA.getId() + " vs " + nodeB.getId());
        System.out.println("different node number:" + nodeA.getId() + " vs " + nodeB.getId());
        return false;
    }

    private static boolean sameHeight(DendroNode rootA, DendroNode rootB) {
        if (almostEqual(rootA.getHeight(), rootB.getHeight(), delta)) {
            return true;
        }
        System.out.println(": " + rootA.getHeight() + " vs " + rootB.getHeight());
        System.out.println("different node height " + rootA.getHeight() + "vs " + rootB.getHeight() + " first #" + rootA.getId() + " second #" + rootB.getId());
        return false;
    }

}
