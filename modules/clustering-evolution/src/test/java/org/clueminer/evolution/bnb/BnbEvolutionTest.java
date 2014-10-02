package org.clueminer.evolution.bnb;

import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.eval.BICScore;
import org.clueminer.eval.NMI;
import org.clueminer.eval.external.JaccardIndex;
import org.clueminer.eval.external.Precision;
import org.clueminer.evolution.attr.AttrEvolution;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class BnbEvolutionTest {

    private BnbEvolution subject;
    private static final long MEGABYTE = 1024L * 1024L;
    private long startTime;
    private ConsoleReporter report;

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

    public BnbEvolutionTest() {
    }

    @Before
    public void setUp() {
        subject = new BnbEvolution();
        report = new ConsoleReporter();
        subject.addEvolutionListener(report);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetName() {
    }

    private void startClock() {
        startTime = System.currentTimeMillis();
    }

    private void stopClock() {
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("time: " + elapsedTime / 1000 + "s");
    }

    /**
     * Test iris dataset evolution
     */
    @Test
    public void testRun() {
        subject.setDataset(FakeDatasets.irisDataset());
        //subject.setAlgorithm(new ));
        subject.setEvaluator(new NMI());
        ExternalEvaluator ext = new Precision();
        subject.setExternal(ext);

        startClock();
        subject.run();
        stopClock();

        reportMemory();
    }

    @Test
    public void testMakeClusters() {
    }

    @Test
    public void testStandartize() {
    }

    @Test
    public void testFinish() {
    }

    @Test
    public void testIndividualCreated() {
    }

    private void reportMemory() {
        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used memory: " + memory + " bytes");
        System.out.println("Used memory: "
                + bytesToMegabytes(memory) + " MB");
    }

}
