package org.clueminer.approximation.api;

import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Timeseries;

/**
 *
 * @author Tomas Barton
 */
public interface DataReduction {

    /**
     * Should reduce number of attributes and transform data values without
     * loosing too much information
     *
     * @param input
     * @return
     */
    public Timeseries<ContinuousInstance> apply(Timeseries<ContinuousInstance> input);
}
