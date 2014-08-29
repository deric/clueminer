package org.clueminer.io;

import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.clueminer.stats.AttrNumStats;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tomas Barton
 */
public class NumericalStatsTest {

    private static Dataset<Instance> dataset;
    private static CommonFixture tf = new CommonFixture();
    private static double precision = 0.001;

    public NumericalStatsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        ARFFHandler arff = new ARFFHandler();
        dataset = new SampleDataset();
        arff.load(tf.irisArff(), dataset, 4);

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testMin() {
        Attribute attr = dataset.getAttribute(0); //sepallength
        assertEquals(4.300, attr.statistics(AttrNumStats.MIN), precision);
        attr = dataset.getAttribute(1); //sepallwidth
        assertEquals(2.000, attr.statistics(AttrNumStats.MIN), precision);
        attr = dataset.getAttribute(2); //petallength
        assertEquals(1.000, attr.statistics(AttrNumStats.MIN), precision);
        attr = dataset.getAttribute(3); //petalwidth
        assertEquals(0.100, attr.statistics(AttrNumStats.MIN), precision);
    }

    @Test
    public void testMax() {
        Attribute attr = dataset.getAttribute(0); //sepallength
        assertEquals(7.900, attr.statistics(AttrNumStats.MAX), precision);
        attr = dataset.getAttribute(1); //sepallwidth
        assertEquals(4.400, attr.statistics(AttrNumStats.MAX), precision);
        attr = dataset.getAttribute(2); //petallength
        assertEquals(6.900, attr.statistics(AttrNumStats.MAX), precision);
        attr = dataset.getAttribute(3); //petalwidth
        assertEquals(2.500, attr.statistics(AttrNumStats.MAX), precision);
    }

    @Test
    public void testAvg() {
        Attribute attr = dataset.getAttribute(0); //sepallength
        assertEquals(5.843, attr.statistics(AttrNumStats.AVG), precision);
        attr = dataset.getAttribute(1); //sepallwidth
        assertEquals(3.054, attr.statistics(AttrNumStats.AVG), precision);
        attr = dataset.getAttribute(2); //petallength
        assertEquals(3.759, attr.statistics(AttrNumStats.AVG), precision);
        attr = dataset.getAttribute(3); //petalwidth
        assertEquals(1.199, attr.statistics(AttrNumStats.AVG), precision);
    }

    @Test
    public void testVariance() {
        Attribute attr = dataset.getAttribute(0); //sepallength
        assertEquals(0.686, attr.statistics(AttrNumStats.VARIANCE), precision);
        attr = dataset.getAttribute(1); //sepallwidth
        assertEquals(0.188, attr.statistics(AttrNumStats.VARIANCE), precision);
        attr = dataset.getAttribute(2); //petallength
        assertEquals(3.113, attr.statistics(AttrNumStats.VARIANCE), precision);
        attr = dataset.getAttribute(3); //petalwidth
        assertEquals(0.582, attr.statistics(AttrNumStats.VARIANCE), precision);
    }

    @Test
    public void testDeviation() {
        Attribute attr = dataset.getAttribute(0); //sepallength
        assertEquals(0.828, attr.statistics(AttrNumStats.STD_DEV), precision);
        attr = dataset.getAttribute(1); //sepallwidth
        assertEquals(0.434, attr.statistics(AttrNumStats.STD_DEV), precision);
        attr = dataset.getAttribute(2); //petallength
        assertEquals(1.764, attr.statistics(AttrNumStats.STD_DEV), precision);
        attr = dataset.getAttribute(3); //petalwidth
        assertEquals(0.763, attr.statistics(AttrNumStats.STD_DEV), precision);
    }

    @Test
    public void testAbsoluteDeviation() {
        Attribute attr = dataset.getAttribute(0); //sepallength
        assertEquals(0.829, attr.statistics(AttrNumStats.ABS_DEV), precision);
        attr = dataset.getAttribute(1); //sepallwidth
        assertEquals(0.577, attr.statistics(AttrNumStats.ABS_DEV), precision);
        attr = dataset.getAttribute(2); //petallength
        assertEquals(1.249, attr.statistics(AttrNumStats.ABS_DEV), precision);
        attr = dataset.getAttribute(3); //petalwidth
        assertEquals(0.811, attr.statistics(AttrNumStats.ABS_DEV), precision);
    }

    /**
     * Test of clone method, of class NumericalStats.
     */
    @Test
    public void testClone() {
    }

    /**
     * Test of reset method, of class NumericalStats.
     */
    @Test
    public void testReset() {
    }

    /**
     * Test of update method, of class NumericalStats.
     */
    @Test
    public void testUpdate() {
    }

    /**
     * Test of value method, of class NumericalStats.
     */
    @Test
    public void testValue() {

    }

    /**
     * Test of provides method, of class NumericalStats.
     */
    @Test
    public void testProvides() {
    }
}