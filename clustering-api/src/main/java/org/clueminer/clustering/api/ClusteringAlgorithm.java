package org.clueminer.clustering.api;

import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;
import org.netbeans.api.progress.ProgressHandle;

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
    public Clustering<Cluster> cluster(Matrix matrix, Props props);

    /**
     *
     * @param dataset
     * @return
     */
    public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset);

    public DistanceMeasure getDistanceFunction();

    public void setDistanceFunction(DistanceMeasure dm);

    /**
     * Algorithm responsible for assigning colors to new clusters
     *
     * @param cg
     */
    public void setColorGenerator(ColorGenerator cg);

    /**
     *
     * @return
     */
    public ColorGenerator getColorGenerator();

    /**
     * API for displaying progress in UI, if not set algorithm should work
     * anyway
     *
     * @param ph
     */
    public void setProgressHandle(ProgressHandle ph);
}
