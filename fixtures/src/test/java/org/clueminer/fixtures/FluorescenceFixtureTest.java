package org.clueminer.fixtures;

import java.io.File;
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
public class FluorescenceFixtureTest {
    
    private static FluorescenceFixture instance;
    
    public FluorescenceFixtureTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        instance = new FluorescenceFixture();  
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
     * Test of testData method, of class FluorescenceFixture.
     */
    @Test
    public void testTestData() throws Exception {              
        File result = instance.testData();
        assertEquals(true, result.exists());

    }
}