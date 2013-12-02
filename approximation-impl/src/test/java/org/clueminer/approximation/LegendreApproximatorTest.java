package org.clueminer.approximation;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.fixtures.TimeseriesFixture;
import org.clueminer.utils.Dump;
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
public class LegendreApproximatorTest {

    private static LegendreApproximator test;

    public LegendreApproximatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        test = new LegendreApproximator(2);
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
     * Test of setMaxDegree method, of class LegendreApproximator.
     */
    @Test
    public void testSetMaxDegree() {
        test.setMaxDegree(5);
        assertEquals(5, test.getMaxDegree());
        assertEquals(5, test.getParamNames().length);
    }

    /**
     * Test of getName method, of class LegendreApproximator.
     */
    @Test
    public void testGetName() {
        assertNotNull(test.getName());
    }

    /**
     * Test of estimate method, of class LegendreApproximator.
     */
    @Test
    public void testEstimate() throws FileNotFoundException, IOException {
        TimeseriesFixture tf = new TimeseriesFixture();
        //CsvLoader loader = new CsvLoader();
        //TimeseriesDataset<ContinuousInstance> dataset = new TimeseriesDataset<ContinuousInstance>(20);

        //dataset.setAttribute(0, new TimePointAttribute);
        /*dataset.attributeBuilder().create("y", "REAL");
        loader.load(tf.irBenzin(), dataset);

        ContinuousInstance inst = dataset.instance(0);*/
    }

    /**
     * Test of getParamNames method, of class LegendreApproximator.
     */
    @Test
    public void testGetParamNames() {
        String[] names = test.getParamNames();
        assertEquals("legendre-1", names[0]);
        Dump.array(names, "names");
    }


    /**
     * Test of getFunctionValue method, of class LegendreApproximator.
     */
    @Test
    public void testGetFunctionValue() {
    }
}