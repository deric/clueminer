package org.clueminer.asr;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.plugin.TimeseriesDataset;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.fixtures.TimeseriesFixture;
import org.clueminer.io.CsvLoader;
import org.clueminer.io.FileHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class AdaptableSamplingReductionTest {

    private static Timeseries<ContinuousInstance> dataset;

    public AdaptableSamplingReductionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws FileNotFoundException, IOException {
        TimeseriesFixture tf = new TimeseriesFixture();
        dataset = new TimeseriesDataset<ContinuousInstance>(1880);
        //FileHandler.loadDataset(tf.irBenzin(), dataset, ",");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of apply method, of class AdaptableSamplingReduction.
     */
    @Test
    public void testApply() {
      //  System.out.println("dataset size: "+dataset.size());
    }
}