package org.clueminer.evaluation.hclust;

import java.util.HashSet;
import java.util.Stack;
import org.clueminer.clustering.api.HierarchicalClusterEvaluator;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.hclust.TreeDataImpl;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.SymmetricMatrix;
import org.clueminer.utils.Dump;

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
    int expCnt = 0;
    int assign = 0;

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

        if (treeData instanceof TreeDataImpl) {
            copheneticMatrix = getCopheneticMatrix((TreeDataImpl) treeData, proximity.rowsCount(), proximity.columnsCount());
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
    public double[][] getCopheneticMatrix(TreeDataImpl tree, int m, int n) {
        int i;
        int idx;
        int left, right;
        double height;
        expCnt = 0;
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
        Dump.matrix(cophenetic, "cophenetic", 2);
        System.out.println("expanded " + expCnt + " nodes");
        return cophenetic;
    }

    public double[][] getCopheneticMatrix(DendroTreeData treeData, int rowsCount, int columnsCount) {
        Matrix treeMatrix = new SymmetricMatrix(rowsCount, columnsCount);
        expCnt = 0;
        DendroNode leave;
        treeData.print();
        HashSet<DendroNode> visited;
        for (int i = 0; i < rowsCount - 1; i++) {
            visited = new HashSet<DendroNode>();
            for (int j = 0; j < i; j++) {
                visited.add(treeData.getLeaf(i));
            }
            leave = treeData.getLeaf(i);
            treeDistance(leave, treeMatrix, visited);
        }

        treeMatrix.printLower(5, 2);
        System.out.println("expanded " + expCnt + " nodes");
        System.out.println("assigned " + assign + " nodes");
        return treeMatrix.getArray();
    }

    /**
     *
     * @param from
     * @param treeMatrix
     */
    private void treeDistance(DendroNode from, Matrix treeMatrix, HashSet<DendroNode> visited) {
        DendroNode node;
        Stack<DendroNode> stack = new Stack<DendroNode>();
        stack.push(from);

        int i, j;
        double maxHeight = Double.MIN_VALUE;
        while (!stack.isEmpty()) {
            node = stack.pop();
            expCnt++;
            visited.add(node);
            if (node.isLeaf()) {
                System.out.println("node: " + node.toString() + " - " + node.getInstance().getName());
                enqueue(node.getParent(), stack, visited);
                if (node != from) {
                    i = from.getInstance().getIndex();
                    j = node.getInstance().getIndex();
                    System.out.println("(" + i + ", " + j + ") = " + maxHeight);
                    assign++;
                    treeMatrix.set(i, j, maxHeight);
                }
            } else {
                //check current tree distance
                if (node.getHeight() > maxHeight) {
                    maxHeight = node.getHeight();
                }
                enqueue(node.getParent(), stack, visited);
                enqueue(node.getRight(), stack, visited);
                enqueue(node.getLeft(), stack, visited);
            }
            System.out.println("stack (" + stack.size() + "): " + stack.toString());
        }
    }

    private void enqueue(DendroNode node, Stack<DendroNode> stack, HashSet<DendroNode> visited) {
        if (node != null && !visited.contains(node)) {
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
    private void countDistance(TreeDataImpl tree, double[][] cophenetic, int left, int right, double height) {
        expCnt++;
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
