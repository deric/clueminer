package org.clueminer.meta.api;

import org.clueminer.clustering.api.evolution.EvolutionListener;
import org.clueminer.clustering.api.evolution.Individual;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public interface MetaFeed extends EvolutionListener {

    /**
     * Triggered when individual added to population
     *
     * @param dataset
     * @param individual
     */
    void individualCreated(Dataset<? extends Instance> dataset, Individual individual);

}
