package org.clueminer.partitioning.impl;

import java.util.ArrayList;
import java.util.Arrays;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.JMatrix;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = Bisection.class)
public class SpectralBisection implements Bisection {

    private Graph g;

    private Node[] nodes;

    private double min, max;

    private int nodeToCluster[];

    @Override
    public String getName() {
        return "Spectral bisection";
    }

    @Override
    public ArrayList<ArrayList<Node>> bisect(Graph g, Props params) {
        this.g = g;
        Matrix laplacianMatrix = buildLaplacianMatrix();
        //laplacianMatrix.eig().getV().print(5, 2);
        return createClusters(laplacianMatrix.eig().getV());
    }

    public Matrix buildLaplacianMatrix() {
        nodes = g.getNodes().toArray();
        double[][] matrixArray = new double[g.getNodeCount()][g.getNodeCount()];
        for (int i = 0; i < g.getNodeCount(); i++) {
            for (int j = 0; j < g.getNodeCount(); j++) {
                if (i == j) {
                    matrixArray[i][j] = g.getDegree(nodes[i]);
                    continue;
                }
                if (g.isAdjacent(nodes[i], nodes[j])) {
                    matrixArray[i][j] = -1;
                } else {
                    matrixArray[i][j] = 0;
                }
            }
        }
        return new JMatrix(matrixArray);
    }

    @Override
    public ArrayList<ArrayList<Node>> bisect(Props params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private ArrayList<ArrayList<Node>> createClusters(Matrix eigenVectors) {
        nodeToCluster = new int[g.getNodeCount()];
        ArrayList<ArrayList<Node>> clusters = new ArrayList<>();
        clusters.add(new ArrayList<Node>());
        clusters.add(new ArrayList<Node>());
        double mid = findMedian(eigenVectors);
        //If all components of the Fiedler vector are the same, randomly split the graph into two equal groups
        if (min == max) {
            for (int i = 0; i < g.getNodeCount() / 2; i++) {
                clusters.get(0).add(nodes[i]);
                nodeToCluster[i] = 0;
            }
            for (int i = g.getNodeCount() / 2; i < g.getNodeCount(); i++) {
                clusters.get(1).add(nodes[i]);
                nodeToCluster[i] = 1;
            }
            return clusters;
        }
        //If min == mid, all nodes would end up in the second group, therefore change the mid value to average of minimal and maximal value
        if (min == mid) {
            mid = (min + max) / 2;
        }
        for (int i = 0; i < g.getNodeCount(); i++) {
            if (eigenVectors.get(i, 1) < mid) {
                clusters.get(0).add(nodes[i]);
                nodeToCluster[i] = 0;
            } else {
                clusters.get(1).add(nodes[i]);
                nodeToCluster[i] = 1;
            }
        }
        return clusters;
    }

    private void findMinMax(Matrix eigenVectors) {
        min = Double.POSITIVE_INFINITY;
        max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < g.getNodeCount(); i++) {
            if (eigenVectors.get(i, 1) > max) {
                max = eigenVectors.get(i, 1);
            }
            if (eigenVectors.get(i, 1) < min) {
                min = eigenVectors.get(i, 1);
            }
        }
    }

    private double findMedian(Matrix eigenVectors) {
        Matrix n = eigenVectors.getMatrix(0, eigenVectors.rowsCount() - 1, 1, 1);
        double fiedlerVector[] = n.getColumnPackedCopy();
        Arrays.sort(fiedlerVector);
        min = fiedlerVector[0];
        max = fiedlerVector[eigenVectors.rowsCount() - 1];
        return fiedlerVector[eigenVectors.rowsCount() / 2];
    }

    @Override
    public Graph removeUnusedEdges() {
        for (int i = 0; i < g.getNodeCount(); i++) {
            for (int j = 0; j < g.getNodeCount(); j++) {
                if (nodeToCluster[i] != nodeToCluster[j]) {
                    Edge e = g.getEdge(nodes[i], nodes[j]);
                    if (e != null) {
                        g.removeEdge(e);
                    }
                }
            }
        }
        return g; // deep copy or new graph needed
    }

}
