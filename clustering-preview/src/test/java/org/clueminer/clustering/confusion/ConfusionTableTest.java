package org.clueminer.clustering.confusion;

import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.utils.Dump;
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
        Dump.matrix(conf, "iris wrong", 0);
        int sum;
        for (int[] conf1 : conf) {
            sum = 0;
            for (int j = 0; j < conf1.length; j++) {
                sum += conf1[j];
            }
            //sum in rows should be 50
            assertEquals(50, sum);
        }
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
