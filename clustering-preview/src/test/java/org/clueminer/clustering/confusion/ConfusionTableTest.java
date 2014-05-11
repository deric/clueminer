package org.clueminer.clustering.confusion;

import org.clueminer.fixtures.clustering.FakeClustering;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ConfusionTableTest {

    private ConfusionTable subject;

    public ConfusionTableTest() {
        subject = new ConfusionTable();
    }

    @BeforeClass
    public static void setUpClass() {
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

    @Test
    public void testSetClusterings() {
    }

    @Test
    public void testSetClusterings_Clustering_Clustering() {
    }

    @Test
    public void testSetClusterings_Clustering_Dataset() {
        int[][] conf = subject.countMutual(FakeClustering.irisWrong2());
        assertEquals(30, conf[0][0]);
        assertEquals(20, conf[1][0]);
        assertEquals(50, conf[2][1]);
        assertEquals(50, conf[2][2]);
    }

    @Test
    public void testCountMutual_Clustering_Clustering() {
    }

    @Test
    public void testCountMutual_Clustering_Dataset() {
    }

    @Test
    public void testUpdateSize() {
    }

    @Test
    public void testCreateBufferedGraphics() {
    }

    @Test
    public void testHasData() {
    }

    @Test
    public void testRender() {
    }

    @Test
    public void testRedraw() {
    }

    @Test
    public void testRecalculate() {
    }

    @Test
    public void testPaintComponent() {
    }

    @Test
    public void testResetCache() {
    }

    @Test
    public void testIsDisplayClustSizes() {
    }

    @Test
    public void testSetDisplayClustSizes() {
    }

    @Test
    public void testCheckMax() {
    }

}
