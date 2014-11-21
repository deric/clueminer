package org.clueminer.clustering.benchmark.evolve;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import org.clueminer.clustering.aggl.HACLW;
import org.clueminer.clustering.api.ClusterEvaluation;
import static org.clueminer.clustering.benchmark.Bench.ensureFolder;
import static org.clueminer.clustering.benchmark.Bench.safeName;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.benchmark.ConsoleDump;
import org.clueminer.dataset.benchmark.GnuplotWriter;
import org.clueminer.dataset.benchmark.ResultsCollector;
import org.clueminer.evolution.bnb.BnbEvolution;

/**
 * Evolution of hierarchical clusterings with different (unsupervised)
 * optimization criterion (single criterion)
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
        String name;
        evolution = new BnbEvolution();
        evolution.setAlgorithm(new HACLW());
        for (Entry<Dataset<? extends Instance>, Integer> e : datasets.entrySet()) {
            Dataset<? extends Instance> d = e.getKey();
            evolution.setDataset(d);
            name = d.getName();
            String csvRes = benchmarkFolder + File.separatorChar + name + File.separatorChar + name + ".csv";
            System.out.println("=== dataset " + name);
            System.out.println("size: " + d.size());
            ensureFolder(benchmarkFolder + File.separatorChar + name);
            for (ClusterEvaluation eval : scores) {
                evolution.setEvaluator(eval);
                GnuplotWriter gw = new GnuplotWriter(evolution, benchmarkFolder, name + "/" + name + "-" + safeName(eval.getName()));
                gw.setPlotDumpMod(50);
                //collect data from evolution
                evolution.addEvolutionListener(new ConsoleDump());
                evolution.addEvolutionListener(gw);
                evolution.addEvolutionListener(rc);
                evolution.run();
                rc.writeToCsv(csvRes);
            }

        }
    }

}
