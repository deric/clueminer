package org.clueminer.graph.knn;

import java.util.ArrayList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphFactory;
import org.clueminer.graph.api.Node;
import org.clueminer.math.matrix.SymmetricMatrix;

/**
 *
 * @author Tomas Bruna
 *
 * TODO: create interface for graph builders
 * @param <E>
 */
public class KNNGraphBuilder<E extends Instance> {

    /**
     * Triangular distance matrix
     */
    private SymmetricMatrix distance;

    private Dataset<E> input;

    private Distance dm;

    public KNNGraphBuilder() {
        dm = new EuclideanDistance();
    }

    /**
     * Find k neighbors of all items in the dataset
     *
     * @param dataset input dataset
     * @return
     */
    private int[][] findNeighbors(Dataset<E> dataset, int k) {
        input = dataset;
        if (k >= input.size()) {
            throw new RuntimeException("Too many neighbours, not enough nodes in dataset");
        }
        buildDistanceMatrix();
        int[][] nearests = new int[input.size()][k];
        for (int i = 0; i < input.size(); i++) {
            //put first k neighbours into array and sort them
            int firsts = k;
            int index = 0;
            for (int j = 0; j < firsts; j++) {
                //skip self as neighbour
                if (i == j) {
                    firsts++;
                    continue;
                }
                nearests[i][index] = j;
                insert(nearests, index, i);
                index++;
            }
            //neighbour array full, find closer neighbours from the rest of the dataset
            for (int j = firsts; j < input.size(); j++) {
                //skip self as neighbour
                if (i == j) {
                    continue;
                }
                //if distance to central node is smaller then of the furthest current neighbour, add this node to neighbours
                if (distance.get(i, j) < distance.get(i, nearests[i][k - 1])) {
                    nearests[i][k - 1] = j;
                    insert(nearests, k - 1, i);
                }
            }
        }
        return nearests;
    }

    /**
     * Sort neighbors in ascending order by distance to central node
     *
     * @param pos Position of the last element in array with neighbors
     * @param i Number of central cluster to which neighbors are assigned
     */
    private void insert(int[][] nearests, int pos, int i) {
        while (pos > 0 && distance.get(i, nearests[i][pos]) < distance.get(i, nearests[i][pos - 1])) {
            int temp = nearests[i][pos];
            nearests[i][pos] = nearests[i][pos - 1];
            nearests[i][pos - 1] = temp;
            pos--;
        }
    }

    private void buildDistanceMatrix() {
        distance = new SymmetricMatrix(input.size(), input.size());
        for (int i = 0; i < input.size(); i++) {
            for (int j = i + 1; j < input.size(); j++) {
                distance.set(i, j, dm.measure(input.instance(i), input.instance(j)));
            }
        }
    }

    public int[][] getNeighborArray(Dataset<E> dataset, int k) {
        return findNeighbors(dataset, k);
    }

    /**
     * Create graph where connected nodes are neighbors
     *
     * @param dataset input dataset
     * @param g graph where output will be stored
     * @param k
     * @return neighbor graph
     */
    public Graph getNeighborGraph(Dataset<E> dataset, Graph g, int k) {
        int[][] nearests = findNeighbors(dataset, k);
        GraphFactory f = g.getFactory();
        ArrayList<Node> nodes = f.createNodesFromInput(dataset);
        g.addAllNodes(nodes);
        g.addEdgesFromNeigborArray(nearests, k);
        g.lookupAdd(dataset);
        return g;
    }

    public void setDistanceMeasure(Distance dm) {
        this.dm = dm;
    }

    public Distance getDistanceMeasure() {
        return this.dm;
    }

}
