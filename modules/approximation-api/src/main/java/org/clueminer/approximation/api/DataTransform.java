package org.clueminer.approximation.api;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.netbeans.api.progress.ProgressHandle;

/**
 * Generic interface for transforming data structure into different
 * representation
 *
 * @author Tomas Barton
 * @param <I>
 * @param <O>
 */
public interface DataTransform<I extends Instance, O extends Instance> {

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
    public void analyze(Dataset<I> dataset, Dataset<O> output, ProgressHandle ph);

    /**
     * Creates preferred data structure for storing results of this
     * transformation
     *
     * @param input input dataset, usually we use number of instances or
     * dimensionality to optimize output storage
     * @return dataset for storing results
     */
    public Dataset<O> createDefaultOutput(Dataset<I> input);
}
