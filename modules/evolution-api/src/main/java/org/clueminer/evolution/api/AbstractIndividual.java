package org.clueminer.evolution.api;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Instance;

/**
 * @param <I>
 * @param <E>
 * @param <C>
 * @TODO eventually move to the API package, when mature enough
 *
 * @author Tomas Barton
 */
public abstract class AbstractIndividual<I extends Individual, E extends Instance, C extends Cluster<E>> implements Individual<I, E, C> {

    protected ClusteringAlgorithm algorithm;

    @Override
    public ClusteringAlgorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public void setAlgorithm(ClusteringAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
}
