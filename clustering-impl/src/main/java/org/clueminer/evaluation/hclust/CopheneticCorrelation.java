package org.clueminer.evaluation.hclust;

import org.clueminer.clustering.algorithm.HCLResult;
import org.clueminer.clustering.api.HierarchicalClusterEvaluator;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.hclust.TreeDataImpl;
import org.clueminer.math.Matrix;

/**
 * It is a measure of how faithfully the tree represents the dissimilarities
 * among observations
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
        HCLResult r = (HCLResult) result;
        double[][] copheneticMatrix = getCopheneticMatrix(r.getTreeData(), proximity.rowsCount(), proximity.columnsCount());
        return copheneticCoefficient(proximity.getArray(), copheneticMatrix);
    }

    /**
     * Creates matrix with distances between points @TODO should be triangular
     *
     * @param tree
     * @param m
     * @param n
     * @return
     */
    public double[][] getCopheneticMatrix(TreeDataImpl tree, int m, int n) {
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
        //Dump.matrix(cophenetic, "cophenetic", 2);
        return cophenetic;
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
    private void countDistance(TreeDataImpl tree, double[][] cophenetic, int left, int right, double height) {
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
     * Count correlation between two matrices
     *
     * @param X
     * @param Y
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
