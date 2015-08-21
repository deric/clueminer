package org.clueminer.evolution;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.HashEvaluationTable;
import org.clueminer.evolution.api.AbstractIndividual;
import org.clueminer.evolution.api.EvolutionSO;
import org.clueminer.evolution.api.Individual;

/**
 *
 * @author Tomas Barton
 * @param <I>
 * @param <E>
 * @param <C>
 */
public abstract class BaseIndividual<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>>
        extends AbstractIndividual<I, E, C> {

    protected EvolutionSO<I, E, C> evolution;

    /**
     * Hash table with various evaluations scores (eliminates repeated
     * computations)
     *
     * @param clustering
     * @return
     */
    @Override
    public EvaluationTable<E, C> evaluationTable(Clustering<E, C> clustering) {
        EvaluationTable<E, C> evalTable = clustering.getEvaluationTable();
        //we try to compute score just once, to eliminate delays
        if (evalTable == null) {
            Dataset<E> dataset = clustering.getLookup().lookup(Dataset.class);
            if (dataset == null) {
                throw new RuntimeException("no dataset associated with clustering");
            }
            evalTable = new HashEvaluationTable<>(clustering, dataset);
            clustering.setEvaluationTable(evalTable);
        }
        return evalTable;
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
