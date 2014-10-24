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

    private Random rand;
    private BenchParams params;
    private final AgglomerativeClustering[] algorithms;

    public Experiment(BenchParams params) {
        rand = new Random();
        this.params = params;
        algorithms = new AgglomerativeClustering[]{new HAC(), new HACLW(), new HCL(), new HACLWMS()};
    }

    @Override
    public void run() {
        int inc = (params.n - params.nSmall) / params.steps;

        System.out.println("increment = " + inc);
        for (int i = params.nSmall; i <= params.n; i += inc) {
            Dataset<? extends Instance> dataset = generateData(i, params.dimension);
            for (AgglomerativeClustering alg : algorithms) {
                NanoBench.create().measurements(params.repeat).cpuAndMemory().measure(
                        alg.getName() + " - " + params.linkage + " - " + dataset.size(),
                        new HclustBenchmark().hclust(alg, dataset, params.linkage)
                );
            }
        }
    }

    /**
     * Generate random dataset of doubles with given dimensions
     *
     * @param size
     * @param dim
     * @return
     */
    protected Dataset<? extends Instance> generateData(int size, int dim) {
        Dataset<? extends Instance> dataset = new ArrayDataset<>(size, dim);
        for (int i = 0; i < size; i++) {
            dataset.get(i).setName(String.valueOf(i));
            for (int j = 0; j < dim; j++) {
                dataset.set(i, j, rand.nextDouble());
            }
        }

        return dataset;
    }

}
