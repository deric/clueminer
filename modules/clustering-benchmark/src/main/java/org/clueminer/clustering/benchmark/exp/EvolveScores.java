package org.clueminer.clustering.benchmark.exp;

import com.beust.jcommander.JCommander;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.clustering.benchmark.Bench;
import org.clueminer.clustering.benchmark.evolve.EvolveExp;
import org.clueminer.clustering.benchmark.evolve.EvolveParams;
import org.clueminer.dataset.benchmark.DatasetFixture;

/**
 *
 * @author Tomas Barton
 */
public class EvolveScores extends Bench {

    public static final String name = "evolve-sc";

    protected static EvolveParams parseArguments(String[] args) {
        EvolveParams params = new EvolveParams();
        JCommander cmd = new JCommander(params);
        printUsage(args, cmd, params);
        return params;
    }

    @Override
    public void main(String[] args) {
        EvolveParams params = parseArguments(args);

        benchmarkFolder = params.home + File.separatorChar + "benchmark" + File.separatorChar + name;
        ensureFolder(benchmarkFolder);

        System.out.println("=== starting evolution exp:");
        List<InternalEvaluator> eval = InternalEvaluatorFactory.getInstance().getAll();
        ClusterEvaluation[] scores = eval.toArray(new ClusterEvaluation[eval.size()]);
        EvolveExp exp = new EvolveExp(params, benchmarkFolder, scores, DatasetFixture.allDatasets());
        ExecutorService execService = Executors.newFixedThreadPool(1);
        execService.submit(exp);
        execService.shutdown();
    }

}
