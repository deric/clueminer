package org.clueminer.chameleon;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;

/**
 *
 * @author Tomas Bruna
 */
public class KNN {

    /**
     * Number of neighbours for each item
     */
    private int numberOfNeighbours;

    /**
     * storage for neighbours of each node
     */
    int[][] nearests;

    Dataset<? extends Instance> input;

    private DistanceMeasure distanceMeasure;

    public KNN() {
        this(3);
    }

    public KNN(int k) {
        this(k, new EuclideanDistance());
    }

    public KNN(int k, DistanceMeasure dm) {
        numberOfNeighbours = k;
        distanceMeasure = dm;
    }

    /**
     * Find k neighbours of all items in the dataset
     * 
     * @param dataset input dataset
     * @return 
     */
    public int[][] findNeighbours(Dataset<? extends Instance> dataset) {
        input = dataset;
        if (numberOfNeighbours >= input.size()) {
            throw new RuntimeException("Too many neighbours, not enough nodes in dataset");
        }
        nearests = new int[input.size()][numberOfNeighbours];
        for (int i = 0; i < input.size(); i++) {
            //put first k neighbours into array and sort them
            int firsts = numberOfNeighbours;
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
            //neigbour array full, find closer neighbours from the rest of the dataset
            for (int j = index; j < input.size(); j++) {
                //skip self as neighbour
                if (i == j) {
                    continue;
                }
                //if distance to central node is smaller then of the furthest current neighbour, add this node to neighbours
                if (distanceMeasure.measure(input.instance(i), input.instance(j)) < distanceMeasure.measure(input.instance(i), input.instance(nearests[i][numberOfNeighbours - 1]))) {
                    nearests[i][numberOfNeighbours - 1] = j;
                    insert(numberOfNeighbours - 1, i);
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
        while (pos > 0 && distanceMeasure.measure(input.instance(i), input.instance(nearests[i][pos])) < distanceMeasure.measure(input.instance(i), input.instance(nearests[i][pos - 1]))) {
            int temp = nearests[i][pos];
            nearests[i][pos] = nearests[i][pos - 1];
            nearests[i][pos - 1] = temp;
            pos--;
        }
    }

}
