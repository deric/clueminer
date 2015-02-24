package org.clueminer.evolution.api;

import java.util.EventListener;

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
     * @return ID of this run
     */
    int started(Evolution evolution);

    /**
     * Triggered when individual added to population
     *
     * @param runId
     * @param individual
     */
    void individualCreated(int runId, Individual individual);

}
