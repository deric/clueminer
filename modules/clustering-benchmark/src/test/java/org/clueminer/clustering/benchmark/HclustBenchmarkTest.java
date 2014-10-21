package org.clueminer.clustering.benchmark;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.aggl.HAC;
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

    public HclustBenchmarkTest() {
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
        //AgglomerativeClustering[] algorithms = {new HAC(), new HACLW()};
        AgglomerativeClustering[] algorithms = {new HAC()};
        Dataset<? extends Instance> dataset = FakeDatasets.irisDataset();
        for (AgglomerativeClustering alg : algorithms) {
            NanoBench.create().measurements(4).measure(alg.getName() + " single link - " + dataset.getName(), new HclustBenchmark().singleLinkage(alg, dataset));
        }

    }

}
