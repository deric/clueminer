package org.clueminer.eval.external;

import com.google.common.collect.Table;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.math.Matrix;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractCountingPairs extends AbstractExternalEval {

    public abstract double countScore(Table<String, String, Integer> table, Clustering<? extends Cluster> ref);

    @Override
    public double score(Clustering<Cluster> c1, Clustering<Cluster> c2) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(c1, c2);
        return countScore(table, c1);
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(clusters);
        return countScore(table, clusters);
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset, Matrix proximity) {
        return score(clusters, dataset);
    }

    /**
     * Bigger is better
     *
     * @return
     */
    @Override
    public boolean isMaximized() {
        return true;
    }
}
