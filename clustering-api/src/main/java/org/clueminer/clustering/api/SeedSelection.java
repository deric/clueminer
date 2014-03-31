package org.clueminer.clustering.api;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public interface SeedSelection {

    /**
     * Unique method identification
     *
     * @return name of the method
     */
    public String getName();

    /**
     * Select k indexes of medoids from given Dataset (medoids are existing
     * instances in the dataset)
     *
     * @param dataset from which we select points (instances)
     * @param k number of points to be selected
     * @return
     */
    public int[] selectIntIndices(Dataset<? extends Instance> dataset, int k);

}
