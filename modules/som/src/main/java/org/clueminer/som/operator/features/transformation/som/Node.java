package org.clueminer.som.operator.features.transformation.som;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Jan Motl
 */
public class Node {

    // vector of weights. This is the solution of SOM training

    public double[] weights;

    // vector of normalized weights to <0..1>. This is used for plotting
    public double[] normWeights;

    // Labels (internal storage to select the most frequent label, eventually used as a storage for histogram)
    public TreeMap<String, Integer> labels = new TreeMap<String, Integer>();

    // U-Matrix distances
    public double uDistance;			// own distance
    public double uDistanceOwn;			// own distance but normalized together with all border hexagons (hence less contrasting)
    public double uDistanceRight;		// distance to the tile on the right from this tile
    public double uDistanceBottomRight;	// distance to the tile on the bottom right from this tile
    public double uDistanceBottomLeft;	// distance to the tile on the bottom left from this tile

    // P-Matrix distance
    public double pDistance;

    // U*-Matrix distance
    public double uStarDistance;

	// Histogram (including the special attributes)
    //public ArrayList<Double> histogram = new ArrayList<Double>();
    public double[] histogram;

    // The most frequent label (all labels including the special attributes)
    public ArrayList<String> labelList = new ArrayList<String>();

    // Cluster
    public int cluster = 0;

    // Neighbors
    public Node nRight;
    public Node nBottomLeft;
    public Node nBottomRight;
    public Node nLeft;
    public Node nTopRight;
    public Node nTopLeft;

    // list of samples assigned to this node (for histogram,...) common attributes
    public ArrayList<double[]> samples = new ArrayList<double[]>();

    // list of samples assigned to this node (for histogram,...) special attributes
    public ArrayList<String[]> samplesTexts = new ArrayList<String[]>();

    // Constructor: initialize the weights to small random variables
    public Node(double[] weights) {
        this.weights = weights;
        normWeights = weights.clone();
    }

    // similarity distance of node to data
    public double getDistance(double[] InputVector) {
        double distance = 0;
        for (int i = 0; i < weights.length; ++i) {
            distance += (InputVector[i] - weights[i]) * (InputVector[i] - weights[i]);
        }
        return Math.sqrt(distance);
    }

    // add label to the content of the node
    public void addLabel(String key) {
        Integer hitCount = labels.get(key);
        if (hitCount == null) {
            labels.put(key, 1);
        } else {
            labels.put(key, hitCount++);
        }
    }

    // get the most frequent label of the node
    public String getLabel() {
        int hitCount = Integer.MIN_VALUE;
        String key = "";

        for (Map.Entry<String, Integer> entry : labels.entrySet()) {
            if (entry.getValue() > hitCount) {
                hitCount = entry.getValue();
                key = entry.getKey();
            }
        }

        return key;
    }

    // erase all the labels
    public void clearAllLabels() {
        labels.clear();
    }
}
