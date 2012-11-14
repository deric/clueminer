package org.clueminer.clustering.api;

import java.io.Serializable;
import org.clueminer.dataset.Dataset;
import org.clueminer.instance.Instance;

/**
 *
 * @author Tomas Barton
 */
public interface ClusterDistance extends Serializable {

    /**
     * Calculates distance between two clusters
     *
     * @param cluster1
     * @param cluster2
     * @return
     */
    public double distance(Dataset<Instance> cluster1, Dataset<Instance> cluster2);
}
