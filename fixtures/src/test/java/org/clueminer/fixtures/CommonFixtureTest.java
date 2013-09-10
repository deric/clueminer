package org.clueminer.fixtures;

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
public class CommonFixtureTest {

    private static CommonFixture instance;

    public CommonFixtureTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new CommonFixture();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of makePath method, of class CommonFixture.
     */
    @Test
    public void testMakePath() throws Exception {
    }

    /**
     * Test of irisData method, of class TestFixture.
     */
    @Test
    public void testIrisData() throws Exception {
        assertTrue(instance.irisData().exists());
    }

    /**
     * Test of irisArff method, of class TestFixture.
     */
    @Test
    public void testIrisArff() throws Exception {
        assertTrue(instance.irisArff().exists());
    }

    /**
     * Test of wineData method, of class TestFixture.
     */
    @Test
    public void testWineData() throws Exception {
        assertTrue(instance.wineData().exists());
    }

    /**
     * Test of umgArff method, of class TestFixture.
     */
    @Test
    public void testUmgArff() throws Exception {
        assertTrue(instance.umgArff().exists());
    }

    /**
     * Test of simpleCluster method, of class CommonFixture.
     */
    @Test
    public void testSimpleCluster() throws Exception {
    }

    /**
     * Test of wellSeparatedCsv method, of class TestFixture.
     */
    @Test
    public void testWellSeparatedCsv() throws Exception {
        assertTrue(instance.wellSeparatedCsv().exists());
    }

    @Test
    public void testBananaArff() throws Exception {
        assertTrue(instance.bananaArff().exists());
    }

    @Test
    public void testGlassArff() throws Exception {
        assertTrue(instance.glassArff().exists());
    }

    @Test
    public void testIonosphereArff() throws Exception {
        assertTrue(instance.ionosphereArff().exists());
    }

    @Test
    public void testVehicleArff() throws Exception {
        assertTrue(instance.vehicleArff().exists());
    }

    @Test
    public void testInsectArff() throws Exception {
        assertTrue(instance.insectArff().exists());
    }

    @Test
    public void testSonarArff() throws Exception {
        assertTrue(instance.sonarArff().exists());
    }

    @Test
    public void testDermatologyArff() throws Exception {
        assertTrue(instance.dermatologyArff().exists());
    }

    @Test
    public void testSchoolData() throws Exception {
        assertTrue(instance.schoolData().exists());
    }
}