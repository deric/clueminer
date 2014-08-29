package org.clueminer.eval.hclust;

import java.util.Stack;
import org.clueminer.clustering.api.HierarchicalClusterEvaluator;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.dendrogram.DendroTreeDataOld;
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

    private static final String name = "Cophenetic Correlation";

    @Override
    public String getName() {
        return name;
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
        double[][] copheneticMatrix;

        if (treeData instanceof DendroTreeDataOld) {
            copheneticMatrix = getCopheneticMatrixOld((DendroTreeDataOld) treeData, proximity.rowsCount(), proximity.columnsCount());
        } else {
            copheneticMatrix = getCopheneticMatrix(treeData, proximity.rowsCount(), proximity.columnsCount());
        }

        return copheneticCoefficient(proximity.getArray(), copheneticMatrix);
    }

    /**
     * Creates matrix with distances between points
     *
     * @TODO should be triangular
     *
     * @param tree
     * @param m
     * @param n
     * @return
     */
    public double[][] getCopheneticMatrixOld(DendroTreeDataOld tree, int m, int n) {
        int i;
        int idx;
        int left, right;
        double height;
        double[][] cophenetic = new double[m][n];
        //System.out.println("matrix " + m + " x " + n);
        //System.out.println("tree level " + tree.treeLevels());

        for (i = 0; i < tree.treeLevels(); i++) {
            idx = tree.getOrder(i); //we're going through all tree levels
            left = tree.getLeft(idx);
            right = tree.getRight(idx);
            height = tree.getHeight(idx);
            //System.out.println("idx= " + idx + ", l=" + left + ", r=" + right + ", h=" + height);
            countDistance(tree, cophenetic, left, right, height);
        }
        return cophenetic;
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
        Stack<DendroNode> stack = new Stack<DendroNode>();
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
     * @param tree
     * @param cophenetic
     * @param left
     * @param right
     * @param height
     */
    private void countDistance(DendroTreeDataOld tree, double[][] cophenetic, int left, int right, double height) {
        //System.out.println("left= " + left + ", height= " + height + ", right= " + right);
        if (!tree.isLeaf(left)) {
            //set same level for its children
            countDistance(tree, cophenetic, tree.getLeft(left), right, height);
            countDistance(tree, cophenetic, tree.getRight(left), right, height);
            return;
        }

        if (!tree.isLeaf(right)) {
            //set same level for its children
            countDistance(tree, cophenetic, left, tree.getLeft(right), height);
            countDistance(tree, cophenetic, left, tree.getRight(right), height);
            return;
        }
        //symetric matrix
        cophenetic[left][right] = height;
        cophenetic[right][left] = height;
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
