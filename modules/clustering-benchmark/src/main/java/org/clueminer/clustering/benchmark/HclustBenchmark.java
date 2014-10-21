package org.clueminer.clustering.benchmark;

import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.hclust.linkage.SingleLinkage;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public class HclustBenchmark {

    public Runnable singleLinkage(final AgglomerativeClustering algorithm, final Dataset<? extends Instance> dataset) {
        final Props params = new Props();
        params.putBoolean(AgglParams.CLUSTER_ROWS, true);
        params.put(AgglParams.LINKAGE, SingleLinkage.name);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HierarchicalResult rowsResult = algorithm.hierarchy(dataset, params);
            }
        };
        return runnable;
    }

}
