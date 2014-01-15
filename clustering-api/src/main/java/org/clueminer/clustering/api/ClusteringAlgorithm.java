package org.clueminer.clustering.api;

import java.util.prefs.Preferences;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
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

    /**
     *
     * @param dataset
     * @return
     */
    public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset);

    public DistanceMeasure getDistanceFunction();

    public void setDistanceFunction(DistanceMeasure dm);
}
