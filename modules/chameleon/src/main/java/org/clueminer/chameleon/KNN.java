package org.clueminer.chameleon;

import java.util.ArrayList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphFactory;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Tomas Bruna
 */
public class KNN {

    /**
     * storage for neighbours of each node
     */
    private int[][] nearests;

    /**
     * Triangular distance matrix
     */
    private double distance[][];

    private Dataset<? extends Instance> input;

    private DistanceMeasure dm;

    public KNN() {
        dm = new EuclideanDistance();
    }

    public void setDistanceMeasure(DistanceMeasure dm) {
        this.dm = dm;
    }

    /**
     * Find k neighbours of all items in the dataset
     *
     * @param dataset input dataset
     * @return
     */
    private int[][] findNeighbors(Dataset<? extends Instance> dataset, int k) {
        input = dataset;
        if (k >= input.size()) {
            throw new RuntimeException("Too many neighbours, not enough nodes in dataset");
        }
        buildDistanceMatrix();
        nearests = new int[input.size()][k];
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
                insert(index, i);
                index++;
            }
            //neighbour array full, find closer neighbours from the rest of the dataset
            for (int j = firsts; j < input.size(); j++) {
                //skip self as neighbour
                if (i == j) {
                    continue;
                }
                //if distance to central node is smaller then of the furthest current neighbour, add this node to neighbours
                if (distance(i, j) < distance(i, nearests[i][k - 1])) {
                    nearests[i][k - 1] = j;
                    insert(k - 1, i);
                }
            }
        }
        return nearests;
    }

    /**
     * Sort neighbours in ascending order by distance to central node
     *
     * @param pos Position of the last element in array with neighbours
     * @param i Number of central cluster to which neighbours are assigned
     */
    private void insert(int pos, int i) {
        while (pos > 0 && distance(i, nearests[i][pos]) < distance(i, nearests[i][pos - 1])) {
            int temp = nearests[i][pos];
            nearests[i][pos] = nearests[i][pos - 1];
            nearests[i][pos - 1] = temp;
            pos--;
        }
    }

    private void buildDistanceMatrix() {
        distance = new double[input.size()][input.size()];
        for (int i = 0; i < input.size(); i++) {
            for (int j = i + 1; j < input.size(); j++) {
                distance[i][j] = dm.measure(input.instance(i), input.instance(j));
            }
        }
    }

    private double distance(int i, int j) {
        if (i > j) {
            int temp = i;
            i = j;
            j = temp;
        }
        return distance[i][j];
    }

    public int[][] getNeighborArray(Dataset<? extends Instance> dataset, int k) {
        return findNeighbors(dataset, k);
    }

    /**
     * Create graph where connected nodes are neighbors
     *
     * @param dataset input dataset
     * @param g graph where output is be stored
     * @return neighbor graph
     */
    public Graph getNeighborGraph(Dataset<? extends Instance> dataset, Graph g, int k) {
        findNeighbors(dataset, k);
        GraphFactory f = g.getFactory();
        ArrayList<Node> nodes = f.createNodesFromInput(dataset);
        g.addAllNodes(nodes);
        g.addEdgesFromNeigborArray(nearests, k);
        return g;
    }

}
