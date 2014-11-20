package org.clueminer.clustering.benchmark.evolve;

import java.util.Map;
import java.util.Map.Entry;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.benchmark.ResultsCollector;
import org.clueminer.evolution.bnb.BnbEvolution;

/**
 *
 * @author Tomas Barton
 */
public class EvolveExp implements Runnable {

    private static ResultsCollector rc;
    private EvolveParams params;
    private String benchmarkFolder;
    private ClusterEvaluation[] scores;
    private Map<Dataset<? extends Instance>, Integer> datasets;

    public EvolveExp(EvolveParams params, String benchmarkFolder, ClusterEvaluation[] scores, Map<Dataset<? extends Instance>, Integer> datasets) {
        this.params = params;
        this.benchmarkFolder = benchmarkFolder;
        this.scores = scores;
        this.datasets = datasets;
    }

    @Override
    public void run() {
        BnbEvolution evolution;
        for (Entry<Dataset<? extends Instance>, Integer> e : datasets.entrySet()) {
            Dataset<? extends Instance> d = e.getKey();
            evolution = new BnbEvolution();
            for (ClusterEvaluation eval : scores) {
                evolution.setEvaluator(eval);

            }

        }
    }

}
