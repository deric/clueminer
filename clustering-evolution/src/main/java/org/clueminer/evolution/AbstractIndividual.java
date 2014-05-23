package org.clueminer.evolution;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.evolution.Individual;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.eval.utils.HashEvaluationTable;
import org.openide.util.Lookup;

/**
 * @param <T>
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
        double otherFitness = another.getFitness();
        double thisFitness = this.getFitness();

        if (thisFitness == otherFitness) {
            return 0;
        }

        if (evolution.getEvaluator().compareScore(thisFitness, otherFitness)) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Hash table with various evaluations scores (eliminates repeated
     * computations)
     *
     * @param clustering
     * @return
     */
    protected EvaluationTable evaluationTable(Clustering<Cluster> clustering) {
        EvaluationTable evalTable = clustering.getLookup().lookup(EvaluationTable.class);
        //we try to compute score just once, to eliminate delays
        if (evalTable == null) {
            evalTable = new HashEvaluationTable(clustering, clustering.getLookup().lookup(Dataset.class));
            //evalTable = Lookup.getDefault().lookup(EvaluationTable.class);
            //evalTable.setData(clustering, clustering.getLookup().lookup(Dataset.class));
            clustering.lookupAdd(evalTable);
        }
        return evalTable;
    }
}
