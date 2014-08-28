package org.clueminer.dataset.benchmark;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tombart
 */
public class DatasetFixtureTest {

    public DatasetFixtureTest() {
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

    /**
     * Test of allDatasets method, of class DatasetFixture.
     */
    @Test
    public void testAllDatasets() {
    }

    @Test
    public void testIris() {
        Dataset<Instance> dataset = DatasetFixture.iris();
        assertEquals(150, dataset.size());
        assertEquals(4, dataset.attributeCount());
    }

    @Test
    public void testVehicle() {
        Dataset<Instance> dataset = DatasetFixture.vehicle();
        assertEquals(846, dataset.size());
        assertEquals(18, dataset.attributeCount());
    }

    @Test
    public void testInsect() {
        Dataset<Instance> dataset = DatasetFixture.insect();
        assertEquals(30, dataset.size());
        assertEquals(3, dataset.attributeCount());
    }

    @Test
    public void testIonosphere() {
        Dataset<Instance> dataset = DatasetFixture.ionosphere();
        assertEquals(34, dataset.attributeCount());
        assertEquals(351, dataset.size());
    }

    @Test
    public void testGlass() {
        Dataset<Instance> dataset = DatasetFixture.glass();
        assertEquals(9, dataset.attributeCount());
        assertEquals(214, dataset.size());
    }

    @Test
    public void testSonar() {
        Dataset<Instance> dataset = DatasetFixture.sonar();
        assertEquals(60, dataset.attributeCount());
        assertEquals(208, dataset.size());
    }

    @Test
    public void testWine() {
        Dataset<Instance> dataset = DatasetFixture.wine();
        assertEquals(13, dataset.attributeCount());
        assertEquals(178, dataset.size());
    }

    @Test
    public void testDermatology() {
        Dataset<Instance> dataset = DatasetFixture.dermatology();
        assertEquals(34, dataset.attributeCount());
        assertEquals(366, dataset.size());
    }

    @Test
    public void testYeast() {
        Dataset<Instance> dataset = DatasetFixture.yeast();
        assertEquals(8, dataset.attributeCount());
        assertEquals(1484, dataset.size());
    }
}
