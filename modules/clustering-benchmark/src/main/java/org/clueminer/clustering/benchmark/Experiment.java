package org.clueminer.clustering.benchmark;

import java.util.Random;
import org.clueminer.clustering.aggl.HAC;
import org.clueminer.clustering.aggl.HACLW;
import org.clueminer.clustering.aggl.HACLWMS;
import org.clueminer.clustering.algorithm.HCL;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.report.NanoBench;

/**
 *
 * @author Tomas Barton
 */
public class Experiment implements Runnable {

    private final Random rand;
    private final BenchParams params;
    private final AgglomerativeClustering[] algorithms;
    private final String results;

    public Experiment(BenchParams params, String results) {
        rand = new Random();
        this.params = params;
        this.results = results;
        algorithms = new AgglomerativeClustering[]{new HAC(), new HACLW(), new HCL(), new HACLWMS()};
    }

    @Override
    public void run() {
        int inc = (params.n - params.nSmall) / params.steps;

        GnuplotReporter reporter = new GnuplotReporter(results, new String[]{"algorithm", "linkage", "n"}, algorithms, params.nSmall + "-" + params.n);
        System.out.println("increment = " + inc);
        for (int i = params.nSmall; i <= params.n; i += inc) {
            Dataset<? extends Instance> dataset = generateData(i, params.dimension);
            for (AgglomerativeClustering alg : algorithms) {
                String[] opts = new String[]{alg.getName(), params.linkage, String.valueOf(dataset.size())};
                NanoBench.create().measurements(params.repeat).collect(reporter, opts).measure(
                        alg.getName() + " - " + params.linkage + " - " + dataset.size(),
                        new HclustBenchmark().hclust(alg, dataset, params.linkage)
                );
            }
        }
        reporter.finish();
    }

    /**
     * Generate random dataset of doubles with given dimensions
     *
     * @param size
     * @param dim
     * @return
     */
    protected Dataset<? extends Instance> generateData(int size, int dim) {
        System.out.println("generating data: " + size + " x " + dim);
        Dataset<? extends Instance> dataset = new ArrayDataset<>(size, dim);
        for (int i = 0; i < dim; i++) {
            dataset.attributeBuilder().create("attr-" + i, "NUMERIC");
        }
        for (int i = 0; i < size; i++) {
            dataset.instance(i).setName(String.valueOf(i));
            for (int j = 0; j < dim; j++) {
                dataset.set(i, j, rand.nextDouble());
            }
        }

        return dataset;
    }

}
