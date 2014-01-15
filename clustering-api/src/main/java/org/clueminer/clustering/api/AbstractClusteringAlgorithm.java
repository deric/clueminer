package org.clueminer.clustering.api;

import org.clueminer.distance.api.DistanceMeasure;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractClusteringAlgorithm implements ClusteringAlgorithm {

    protected DistanceMeasure distanceMeasure;

    public static final String DISTANCE = "distanceMeasure";

    @Override
    public DistanceMeasure getDistanceFunction() {
        return distanceMeasure;
    }

    @Override
    public void setDistanceFunction(DistanceMeasure dm) {
        this.distanceMeasure = dm;
    }
}
