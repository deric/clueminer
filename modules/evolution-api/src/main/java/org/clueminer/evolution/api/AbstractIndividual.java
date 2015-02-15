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
    protected EvolutionSO evolution;

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

        if (thisFitness == otherFitness) {
            return 0;
        }

        if (evolution.getEvaluator().isBetter(thisFitness, otherFitness)) {
            return 1;
        } else {
            return -1;
        }
    }
}
