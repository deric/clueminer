package org.clueminer.clustering.aggl;

import java.util.Map;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class PointerHierarchy {

    private final Dataset<? extends Instance> dataset;
    private final Map<Integer, Double> lambda;
    private final int[] pi;


    public PointerHierarchy(Dataset<? extends Instance> dataset, Map<Integer, Double> lambda, int[] pi) {
        this.dataset = dataset;
        this.lambda = lambda;
        this.pi = pi;
    }

    public Dataset<? extends Instance> getDataset() {
        return dataset;
    }

    public Map<Integer, Double> getLambda() {
        return lambda;
    }

    public int[] getPi() {
        return pi;
    }

}
