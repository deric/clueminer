package org.clueminer.clustering.benchmark;

import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.hclust.linkage.CompleteLinkage;
import org.clueminer.hclust.linkage.SingleLinkage;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public class HclustBenchmark {

    public Runnable hclust(final AgglomerativeClustering algorithm, final Dataset<? extends Instance> dataset, final String linkage) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Props params = new Props();
                params.putBoolean(AgglParams.CLUSTER_ROWS, true);
                params.put(AgglParams.LINKAGE, linkage);

                algorithm.hierarchy(dataset, params);
            }
        };
        return runnable;
    }

    public Runnable singleLinkage(final AgglomerativeClustering algorithm, final Dataset<? extends Instance> dataset) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Props params = new Props();
                params.putBoolean(AgglParams.CLUSTER_ROWS, true);
                params.put(AgglParams.LINKAGE, SingleLinkage.name);

                algorithm.hierarchy(dataset, params);
            }
        };
        return runnable;
    }

    public Runnable completeLinkage(final AgglomerativeClustering algorithm, final Dataset<? extends Instance> dataset) {

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                Props params = new Props();
                params.putBoolean(AgglParams.CLUSTER_ROWS, true);
                params.put(AgglParams.LINKAGE, CompleteLinkage.name);

                algorithm.hierarchy(dataset, params);
            }
        };
        return runnable;
    }

}
