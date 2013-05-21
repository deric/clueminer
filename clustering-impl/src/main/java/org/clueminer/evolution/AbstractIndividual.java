package org.clueminer.evolution;

import org.clueminer.clustering.api.evolution.Individual;
import org.clueminer.clustering.api.ClusteringAlgorithm;

/**
 * @TODO eventually move to the API package, when mature enough
 *
 * @author Tomas Barton
 */
public abstract class AbstractIndividual<T extends Individual> implements Individual<T> {

    protected ClusteringAlgorithm algorithm;
    protected AttrEvolution evolution;

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
        double otherFitness = another.getFitness();
        double thisFitness = this.getFitness();

        if (thisFitness > otherFitness) {
            return 1;
        } else if (thisFitness < otherFitness) {
            return -1;
        }
        return 0;
    }
}
