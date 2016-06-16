package org.clueminer.hts.fluorescence;

import java.io.IOException;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.SampleDataset;
import org.clueminer.fixtures.FluorescenceFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class AnalyzeRunnerTest {

    public AnalyzeRunnerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of run method, of class AnalyzeRunner.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        FluorescenceImporter importer = null;
        try {
            FluorescenceFixture tf = new FluorescenceFixture();
            importer = new FluorescenceImporter(tf.testData());
            ProgressHandle ph = ProgressHandle.createHandle("Importing dataset");
            importer.setProgressHandle(ph);

            importer.run();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        //FluorescenceDataset dataset = importer.getDataset();
        Dataset<? extends Instance> plate = importer.getDataset();
        //System.out.println("inst A1 "+ plate.instance(0).toString());
        //System.out.println("plate "+plate.toString());
        Dataset<Instance> output = new SampleDataset<Instance>();
        output.setParent((Dataset<Instance>) plate);
        ProgressHandle ph = ProgressHandle.createHandle("Analyzing dataset");
        //   AnalyzeRunner instance = new AnalyzeRunner((Timeseries<ContinuousInstance>) plate, output, ph);
        //   instance.run();

    }

    /**
     * Test of getAnalyzedData method, of class AnalyzeRunner.
     */
    @Test
    public void testGetAnalyzedData() {
        System.out.println("getAnalyzedData");

    }
}
