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
public class RtcaFixtureTest {
    
    private static RtcaFixture instance;
    
    public RtcaFixtureTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        instance = new RtcaFixture();
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
     * Test of rtcaTest method, of class TestFixture.
     */
    @Test
    public void testRtcaTest() throws Exception {
        assertTrue(instance.rtcaTest().exists());
    }

    @Test
    public void testRtcaData() throws Exception {
        assertTrue(instance.rtcaData().exists());
    }
    /**
     * Test of rtcaTextFile method, of class RtcaFixture.
     */
    @Test
    public void testRtcaTextFile() throws Exception {
    }

    /**
     * Test of sdfTest method, of class RtcaFixture.
     */
    @Test
    public void testSdfTest() throws Exception {
    }
}