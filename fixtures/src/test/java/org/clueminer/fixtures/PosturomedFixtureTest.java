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
public class PosturomedFixtureTest {

    private static PosturomedFixture instance;

    public PosturomedFixtureTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        instance = new PosturomedFixture();
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
     * Test of testData method, of class PosturomedFixture.
     */
    @Test
    public void testTestData() throws Exception {
        assertTrue(instance.testData().exists());
    }
}