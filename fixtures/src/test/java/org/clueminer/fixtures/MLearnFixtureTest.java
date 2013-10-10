package org.clueminer.fixtures;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tombart
 */
public class MLearnFixtureTest {

    private MLearnFixture subject;

    public MLearnFixtureTest() {
        subject = new MLearnFixture();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of iris method, of class MLearnFixture.
     */
    @Test
    public void testIris() throws Exception {
        assertTrue(subject.iris().exists());
    }

    /**
     * Test of bosthouse method, of class MLearnFixture.
     */
    @Test
    public void testBosthouse() throws Exception {
        assertTrue(subject.bosthouse().exists());
    }

    /**
     * Test of irisMissing method, of class MLearnFixture.
     */
    @Test
    public void testIrisMissing() throws Exception {
        assertTrue(subject.irisMissing().exists());
    }

    /**
     * Test of irisQuoted1 method, of class MLearnFixture.
     */
    @Test
    public void testIrisQuoted1() throws Exception {
        assertTrue(subject.irisQuoted1().exists());
    }

    /**
     * Test of irisQuoted2 method, of class MLearnFixture.
     */
    @Test
    public void testIrisQuoted2() throws Exception {
        assertTrue(subject.irisQuoted2().exists());
    }

    /**
     * Test of cars method, of class MLearnFixture.
     */
    @Test
    public void testCars() throws Exception {
        assertTrue(subject.cars().exists());
    }

    /**
     * Test of dermatology method, of class MLearnFixture.
     */
    @Test
    public void testDermatology() throws Exception {
        assertTrue(subject.dermatology().exists());
    }

    /**
     * Test of forrestFires method, of class MLearnFixture.
     */
    @Test
    public void testForrestFires() throws Exception {
        assertTrue(subject.forrestFires().exists());
    }
}
