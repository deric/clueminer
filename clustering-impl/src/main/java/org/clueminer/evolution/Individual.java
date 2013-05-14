package org.clueminer.evolution;

import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;

/**
 *
 * @author Tomas Barton
 */
public class Individual implements Comparable<Individual> {

    private ClusteringAlgorithm algorithm;
    private ClusterEvaluation evaluator;

    public double getFitness() {
        return 0.0;
    }

    public ClusteringAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(ClusteringAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void mutate() {
    }

    public ClusterEvaluation getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(ClusterEvaluation evaluator) {
        this.evaluator = evaluator;
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
