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
public class XCalibourFixtureTest {
    
    private static XCalibourFixture instance;
    
    public XCalibourFixtureTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        instance = new XCalibourFixture();
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
     * Test of testData method, of class XCalibourFixture.
     */
    @Test
    public void testTestData() throws Exception {
        System.out.println(instance.testData());
        assertEquals(true, instance.testData().exists());
    }
}