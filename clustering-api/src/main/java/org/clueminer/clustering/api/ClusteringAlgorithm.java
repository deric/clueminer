package org.clueminer.clustering.api;

import java.util.prefs.Preferences;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;

/**
 *
 * @author Tomas Barton
 */
public interface ClusteringAlgorithm {

    public String getName();

    /**
     *
     * @param matrix
     * @param props
     * @return
     */
    public Clustering<Cluster> cluster(Matrix matrix, Preferences props);

    public DistanceMeasure getDistanceFunction();

    public void setDistanceFunction(DistanceMeasure dm);
}
