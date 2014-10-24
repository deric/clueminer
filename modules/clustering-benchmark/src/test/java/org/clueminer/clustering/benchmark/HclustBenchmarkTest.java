package org.clueminer.clustering.benchmark;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.aggl.HAC;
import org.clueminer.clustering.aggl.HACLW;
import org.clueminer.clustering.aggl.HACLWMS;
import org.clueminer.clustering.algorithm.HCL;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.report.NanoBench;
import org.clueminer.report.ReporterHandler;
import org.junit.Test;
import org.testng.Reporter;
import org.testng.annotations.BeforeSuite;

/**
 *
 * @author deric
 */
public class HclustBenchmarkTest {

    private final AgglomerativeClustering[] algorithms;

    public HclustBenchmarkTest() {
        algorithms = new AgglomerativeClustering[]{new HAC(), new HACLW(), new HCL(), new HACLWMS()};
    }

    @BeforeSuite
    public void setUp() {
        Logger logger = NanoBench.getLogger();
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);
        logger.addHandler(new ReporterHandler());
        Reporter.setEscapeHtml(false);
    }

    @Test
    public void testSingleLinkage() {
        Dataset<? extends Instance> dataset = FakeDatasets.irisDataset();
        for (AgglomerativeClustering alg : algorithms) {
            NanoBench.create().measurements(4).cpuAndMemory().measure(
                    alg.getName() + " single link - " + dataset.getName(),
                    new HclustBenchmark().singleLinkage(alg, dataset)
            );
        }
    }

    @Test
    public void testCompleteLinkage() {
        Dataset<? extends Instance> dataset = FakeDatasets.irisDataset();
        for (AgglomerativeClustering alg : algorithms) {
            NanoBench.create().cpuAndMemory().measurements(4).measure(
                    alg.getName() + " complete link - " + dataset.getName(),
                    new HclustBenchmark().completeLinkage(alg, dataset)
            );
        }
    }

}
