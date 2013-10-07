package org.clueminer.approximation.api;

import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public interface DataTransform {
    
    public String getName();

    /**
     * Creates a discrete dataset from dataset with continuous values
     *
     * @param dataset
     * @param output
     * @param ph
     */
    public void analyze(Timeseries<ContinuousInstance> dataset, Dataset<Instance> output, ProgressHandle ph);
}
