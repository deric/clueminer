package org.clueminer.evolution.api;

import org.clueminer.clustering.api.ClusteringAlgorithm;

/**
 * @param <T>
 * @TODO eventually move to the API package, when mature enough
 *
 * @author Tomas Barton
 */
public abstract class AbstractIndividual<T extends Individual> implements Individual<T> {

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
