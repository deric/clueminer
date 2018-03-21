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
package org.clueminer.eval.hclust;

import java.util.Stack;
import org.clueminer.clustering.api.HierarchicalClusterEvaluator;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.SymmetricMatrix;

/**
 * It is a measure of how faithfully the tree represents the dissimilarities
 * among observations
 *
 * Sokal, R. R. and F. J. Rohlf. 1962. The comparison of dendrograms by
 * objective methods. Taxon, 11:33-40
 *
 * @link http://en.wikipedia.org/wiki/Cophenetic_correlation
 *
 * @author Tomas Barton
 */
public class CopheneticCorrelation implements HierarchicalClusterEvaluator {

    private static final String NAME = "Cophenetic Correlation";

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Counts Cophenetic correlation coefficient
     *
     * @param result
     * @return
     */
    @Override
    public double score(HierarchicalResult result) {
        Matrix proximity = result.getProximityMatrix();
        DendroTreeData treeData = result.getTreeData();

        return copheneticCoefficient(proximity.getArray(), copheneticMatrix(proximity, treeData));
    }

    /**
     * Computes Cophenetic matrix
     *
     * @param proximity matrix
     * @param treeData  dendrogram tree structure
     * @return
     */
    protected double[][] copheneticMatrix(Matrix proximity, DendroTreeData treeData) {
        return getCopheneticMatrix(treeData, proximity.rowsCount(), proximity.columnsCount());
    }

    /**
     * Recursive version of counting distances in dendrogram tree (probably most
     * efficient)
     *
     * @param treeData
     * @param rowsCount
     * @param columnsCount
     * @return
     */
    public double[][] getCopheneticMatrix(DendroTreeData treeData, int rowsCount, int columnsCount) {
        Matrix treeMatrix = new SymmetricMatrix(rowsCount, columnsCount);
        DendroNode node;
        Stack<DendroNode> stack = new Stack<>();
        stack.push(treeData.getRoot());
        DendroNode left, right;
        while (!stack.isEmpty()) {
            node = stack.pop();
            left = node.getLeft();
            right = node.getRight();
            countDistance(treeMatrix, left, right, node.getHeight());
            enqueue(left, stack);
            enqueue(right, stack);
        }
        return treeMatrix.getArray();
    }

    private void enqueue(DendroNode node, Stack<DendroNode> stack) {
        if (node != null && !node.isLeaf()) {
            stack.push(node);
        }
    }

    /**
     * Count distance between two given nodes, if left and right are directly
     * leaves, saves into matrix Cophenetic their distance, i.e. height of their
     * parent node
     *
     * We have to start from root node to make it work.
     *
     * @param tree
     * @param matrix
     * @param left
     * @param right
     * @param height
     */
    private void countDistance(Matrix matrix, DendroNode left, DendroNode right, double height) {
        if (!left.isLeaf()) {
            countDistance(matrix, left.getLeft(), right, height);
            countDistance(matrix, left.getRight(), right, height);
            return;
        }

        if (!right.isLeaf()) {
            countDistance(matrix, left, right.getLeft(), height);
            countDistance(matrix, left, right.getRight(), height);
            return;
        }

        matrix.set(left.getIndex(), right.getIndex(), height);
    }

    /**
     * Count correlation between two matrices
     *
     * @param X matrix of Euclidean distances
     * @param Y matrix of tree distances
     * @return
     */
    public double copheneticCoefficient(double[][] X, double[][] Y) {
        int i, j, k = 0;
        double avgX = 0, avgY = 0;
        for (i = 0; i < X.length; i++) {
            for (j = 0; j < i; j++) {
                avgX += X[j][i];
                avgY += Y[j][i];
                k++;
            }
        }
        avgX /= k;
        avgY /= k;

        double cov = 0, sigmaX = 0, sigmaY = 0;
        double diffX, diffY;
        for (i = 0; i < X.length; i++) {
            for (j = 0; j < i; j++) {
                diffX = X[j][i] - avgX;
                diffY = Y[j][i] - avgY;
                cov += diffX * diffY;
                sigmaX += Math.pow(diffX, 2);
                sigmaY += Math.pow(diffY, 2);
            }
        }
        return cov / Math.sqrt(sigmaX * sigmaY);
    }

}
