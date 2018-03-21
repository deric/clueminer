/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.clustering;

import org.clueminer.clustering.api.AlgParams;
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
            System.out.println("first - " + result.getParams().get(AlgParams.ALG));
            result.getTreeData().print();
            System.out.println("second - " + other.getParams().get(AlgParams.ALG));
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
