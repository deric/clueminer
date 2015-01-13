package org.clueminer.clustering.api;

import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.utils.Props;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public interface ClusteringAlgorithm {

    String getName();

    /**
     *
     * @param dataset
     * @param props
     * @return
     */
    Clustering<Cluster> cluster(Dataset<? extends Instance> dataset, Props props);

    /**
     *
     * @param dataset
     * @return
     */
    Clustering<Cluster> cluster(Dataset<? extends Instance> dataset);

    DistanceMeasure getDistanceFunction();

    void setDistanceFunction(DistanceMeasure dm);

    /**
     * Algorithm responsible for assigning colors to new clusters
     *
     * @param cg
     */
    void setColorGenerator(ColorGenerator cg);

    /**
     *
     * @return
     */
    ColorGenerator getColorGenerator();

    /**
     * API for displaying progress in UI, if not set algorithm should work
     * anyway
     *
     * @param ph
     */
    void setProgressHandle(ProgressHandle ph);

    Param[] getParameters();
}
