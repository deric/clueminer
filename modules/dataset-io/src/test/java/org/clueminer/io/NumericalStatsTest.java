package org.clueminer.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.StatsNum;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.exception.ParserError;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.arff.ARFFHandler;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class NumericalStatsTest {

    private static Dataset<? extends Instance> dataset;
    private static final CommonFixture TF = new CommonFixture();
    private static final double DELTA = 1e-9;

    public NumericalStatsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        irisDataset();
    }

    public static Dataset<? extends Instance> irisDataset() throws FileNotFoundException, IOException, ParserError {
        if (dataset == null) {
            ARFFHandler arff = new ARFFHandler();
            dataset = new ArrayDataset(150, 4);
            arff.load(TF.irisArff(), dataset, 4);
        }
        return dataset;
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testMin() {
        Attribute attr = dataset.getAttribute(0); //sepallength
        assertEquals(4.300, attr.statistics(StatsNum.MIN), DELTA);
        attr = dataset.getAttribute(1); //sepallwidth
        assertEquals(2.000, attr.statistics(StatsNum.MIN), DELTA);
        attr = dataset.getAttribute(2); //petallength
        assertEquals(1.000, attr.statistics(StatsNum.MIN), DELTA);
        attr = dataset.getAttribute(3); //petalwidth
        assertEquals(0.100, attr.statistics(StatsNum.MIN), DELTA);
    }

    @Test
    public void testMax() {
        Attribute attr = dataset.getAttribute(0); //sepallength
        assertEquals(7.900, attr.statistics(StatsNum.MAX), DELTA);
        attr = dataset.getAttribute(1); //sepallwidth
        assertEquals(4.400, attr.statistics(StatsNum.MAX), DELTA);
        attr = dataset.getAttribute(2); //petallength
        assertEquals(6.900, attr.statistics(StatsNum.MAX), DELTA);
        attr = dataset.getAttribute(3); //petalwidth
        assertEquals(2.500, attr.statistics(StatsNum.MAX), DELTA);
    }

    @Test
    public void testAvg() {
        Attribute attr = dataset.getAttribute(0); //sepallength
        assertEquals(5.843333333333333, attr.statistics(StatsNum.AVG), DELTA);
        attr = dataset.getAttribute(1); //sepallwidth
        assertEquals(3.0573333333333315, attr.statistics(StatsNum.AVG), DELTA);
        attr = dataset.getAttribute(2); //petallength
        assertEquals(3.758, attr.statistics(StatsNum.AVG), DELTA);
        attr = dataset.getAttribute(3); //petalwidth
        assertEquals(1.1993333333333336, attr.statistics(StatsNum.AVG), DELTA);
    }

    @Test
    public void testVariance() {
        Attribute attr = dataset.getAttribute(0); //sepallength
        assertEquals(0.6856935123042518, attr.statistics(StatsNum.VARIANCE), DELTA);
        attr = dataset.getAttribute(1); //sepallwidth
        assertEquals(0.1899794183445187, attr.statistics(StatsNum.VARIANCE), DELTA);
        attr = dataset.getAttribute(2); //petallength
        assertEquals(3.1162778523489933, attr.statistics(StatsNum.VARIANCE), DELTA);
        attr = dataset.getAttribute(3); //petalwidth
        assertEquals(0.5810062639821031, attr.statistics(StatsNum.VARIANCE), DELTA);
    }

    @Test
    public void testDeviation() {
        Attribute attr = dataset.getAttribute(0); //sepallength
        assertEquals(0.8280661279778637, attr.statistics(StatsNum.STD_DEV), DELTA);
        attr = dataset.getAttribute(1); //sepallwidth
        assertEquals(0.4358662849366979, attr.statistics(StatsNum.STD_DEV), DELTA);
        attr = dataset.getAttribute(2); //petallength
        assertEquals(1.7652982332594664, attr.statistics(StatsNum.STD_DEV), DELTA);
        attr = dataset.getAttribute(3); //petalwidth
        assertEquals(0.7622376689603467, attr.statistics(StatsNum.STD_DEV), DELTA);
    }

    /**
     * abs dev with bessel's correction
     */
    @Test
    public void testAbsoluteDeviation() {
        Attribute attr = dataset.getAttribute(0); //sepallength
        assertNotNull(attr.getName());
        //assertEquals(0.829, attr.statistics(StatsNum.ABS_DEV), precision);
        assertEquals(0.8319675608888636, attr.statistics(StatsNum.ABS_DEV), DELTA);
        attr = dataset.getAttribute(1); //sepallwidth
        System.out.println("1:" + attr.getName());
        //assertEquals(0.577, attr.statistics(StatsNum.ABS_DEV), precision);
        assertEquals(0.582273565940307, attr.statistics(StatsNum.ABS_DEV), DELTA);
        attr = dataset.getAttribute(2); //petallength
        //assertEquals(1.249, attr.statistics(StatsNum.ABS_DEV), precision);
        assertEquals(1.2542866097223795, attr.statistics(StatsNum.ABS_DEV), DELTA);
        attr = dataset.getAttribute(3); //petalwidth
        //assertEquals(0.811, attr.statistics(StatsNum.ABS_DEV), precision);
        assertEquals(0.8139719501128218, attr.statistics(StatsNum.ABS_DEV), DELTA);
    }
}
