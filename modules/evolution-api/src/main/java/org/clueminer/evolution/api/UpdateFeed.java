package org.clueminer.evolution.api;

import java.util.EventListener;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public interface UpdateFeed extends EventListener {

    /**
     * Name should uniquely identify the type of storage/processing engine
     *
     * @return unique name of the provider
     */
    String getName();

    /**
     * Triggered when evolution starts
     *
     * @param evolution
     */
    void started(Evolution evolution);

    /**
     * Triggered when individual added to population
     *
     * @param dataset
     * @param individual
     */
    void individualCreated(Dataset<? extends Instance> dataset, Individual individual);

}
