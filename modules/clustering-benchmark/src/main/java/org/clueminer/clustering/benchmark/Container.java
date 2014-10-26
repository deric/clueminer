package org.clueminer.clustering.benchmark;

import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public abstract class Container implements Runnable {

    private HierarchicalResult result;
    private final AgglomerativeClustering algorithm;
    private final Dataset<? extends Instance> dataset;

    public Container(AgglomerativeClustering algorithm, Dataset<? extends Instance> dataset) {
        this.algorithm = algorithm;
        this.dataset = dataset;
    }

    public abstract HierarchicalResult hierarchical(AgglomerativeClustering algorithm, Dataset<? extends Instance> dataset, Props params);

    @Override
    public void run() {
        Props params = new Props();
        this.result = hierarchical(algorithm, dataset, params);
    }

}
