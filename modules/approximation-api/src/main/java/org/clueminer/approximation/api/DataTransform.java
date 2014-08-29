package org.clueminer.approximation.api;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public interface DataTransform {

    public String getName();

    /**
     * Creates a discrete dataset from dataset with continuous values.
     * Transforms something into something else (run dimensionality reduction,
     * outliers detection, etc.)
     *
     * @param dataset
     * @param output
     * @param ph
     */
    public void analyze(Dataset<? extends Instance> dataset, Dataset<? extends Instance> output, ProgressHandle ph);

    /**
     * Creates preferred data structure for storing results of this
     * transformation
     *
     * @param input input dataset, usually we use number of instances or
     * dimensionality to optimize output storage
     * @return dataset for storing results
     */
    public Dataset<? extends Instance> createDefaultOutput(Dataset<? extends Instance> input);
}
