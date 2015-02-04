package org.clueminer.evolution;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.eval.utils.HashEvaluationTable;
import org.clueminer.evolution.api.AbstractIndividual;
import org.clueminer.evolution.api.Individual;

/**
 *
 * @author Tomas Barton
 * @param <T>
 */
public abstract class BaseIndividual<T extends Individual> extends AbstractIndividual<T> {

    /**
     * Hash table with various evaluations scores (eliminates repeated
     * computations)
     *
     * @param clustering
     * @return
     */
    @Override
    public EvaluationTable evaluationTable(Clustering<? extends Cluster> clustering) {
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
