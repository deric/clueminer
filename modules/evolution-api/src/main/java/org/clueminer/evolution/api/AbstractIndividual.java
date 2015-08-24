package org.clueminer.evolution.api;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Instance;

/**
 * @param <I>
 * @param <E>
 * @param <C>
 *
 * @author Tomas Barton
 */
public abstract class AbstractIndividual<I extends Individual, E extends Instance, C extends Cluster<E>> implements Individual<I, E, C> {

    protected ClusteringAlgorithm<E, C> algorithm;

    @Override
    public ClusteringAlgorithm<E, C> getAlgorithm() {
        return algorithm;
    }

    @Override
    public void setAlgorithm(ClusteringAlgorithm<E, C> algorithm) {
        this.algorithm = algorithm;
    }
}
