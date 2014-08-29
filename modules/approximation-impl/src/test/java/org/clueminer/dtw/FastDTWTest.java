package org.clueminer.dtw;

import org.clueminer.fixtures.TimeseriesFixture;
import org.clueminer.timeseries.TimeSeries;
import org.clueminer.util.DistanceFunction;
import org.clueminer.util.DistanceFunctionFactory;
import org.clueminer.util.ManhattanDistance;
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
public class FastDTWTest {

    private final TimeseriesFixture tf = new TimeseriesFixture();
    private TimeSeries tsI, tsJ;

    public FastDTWTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        tsI = new TimeSeries(tf.trace0(), false, false, ',');
        tsJ = new TimeSeries(tf.trace1(), false, false, ',');
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getWarpDistBetween method, of class FastDTW.
     */
    @Test
    public void testGetWarpDistBetween_3args() {
    }

    /**
     * Test of getWarpDistBetween method, of class FastDTW.
     */
    @Test
    public void testGetWarpDistBetween_4args() {
    }

    /**
     * Test of getWarpPathBetween method, of class FastDTW.
     */
    @Test
    public void testGetWarpPathBetween_3args() {
    }

    /**
     * Test of getWarpPathBetween method, of class FastDTW.
     */
    @Test
    public void testGetWarpPathBetween_4args() {

        final DistanceFunction distFn;

        distFn = new ManhattanDistance();
        int radius = 5;

        final TimeWarpInfo info = org.clueminer.dtw.FastDTW.getWarpInfoBetween(tsI, tsJ, radius, distFn);

        System.out.println("Warp Distance: " + info.getDistance());
        System.out.println("Warp Path:     " + info.getPath());
    }

    /**
     * Test of getWarpInfoBetween method, of class FastDTW.
     */
    @Test
    public void testGetWarpInfoBetween() {

        final DistanceFunction distFn;

        distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
        int radius = 5;

        final TimeWarpInfo info = org.clueminer.dtw.FastDTW.getWarpInfoBetween(tsI, tsJ, radius, distFn);

        System.out.println("Warp Distance: " + info.getDistance());
        System.out.println("Warp Path:     " + info.getPath());
    }

}
