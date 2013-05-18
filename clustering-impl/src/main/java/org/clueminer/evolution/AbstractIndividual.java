package org.clueminer.evolution;

import org.clueminer.clustering.api.ClusteringAlgorithm;

/**
 * @TODO eventually move to the API package, when mature enough
 *
 * @author Tomas Barton
 */
public abstract class AbstractIndividual<T extends Individual> implements Individual<T> {

    protected ClusteringAlgorithm algorithm;
    protected Evolution evolution;

    @Override
    public ClusteringAlgorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public void setAlgorithm(ClusteringAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public int compareTo(Individual another) {
        if (this.getFitness() > another.getFitness()) {
            return -1;
        }
        if (this.getFitness() < another.getFitness()) {
            return 1;
        }
        return 0;
    }
}
