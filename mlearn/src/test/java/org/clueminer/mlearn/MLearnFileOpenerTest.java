package org.clueminer.mlearn;

import java.io.IOException;
import java.util.Collection;
import org.clueminer.fixtures.MLearnFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tombart
 */
public class MLearnFileOpenerTest {

    private MLearnFileOpener subject;
    private MLearnFixture fixtures;

    public MLearnFileOpenerTest() {
        subject = new MLearnFileOpener();
        fixtures = new MLearnFixture();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of detectMIME method, of class MLearnFileOpener.
     */
    @Test
    public void testDetectMIME() throws IOException {
        Collection col = subject.detectMIME(fixtures.iris());
        System.out.println("iris: " + col.toString());
        assertEquals(true, col.contains("application/octet-stream"));
        col = subject.detectMIME(fixtures.dermatology());
        System.out.println(col);
        assertEquals(true, col.contains("text/x-tex"));
    }

    /**
     * Test of isFileSupported method, of class MLearnFileOpener.
     */
    @Test
    public void testIsFileSupported() throws IOException {
        //should support iris data
        assertNotNull(subject);
        assertNotNull(fixtures);
        assertEquals(true, subject.isFileSupported(fixtures.iris()));
        assertEquals(true, subject.isFileSupported(fixtures.cars()));
        assertEquals(true, subject.isFileSupported(fixtures.bosthouse()));
        assertEquals(true, subject.isFileSupported(fixtures.dermatology()));
        assertEquals(true, subject.isFileSupported(fixtures.forrestFires()));
    }

    /**
     * Test of open method, of class MLearnFileOpener.
     */
    @Test
    public void testOpen() {
    }

    /**
     * Test of getExtension method, of class MLearnFileOpener.
     */
    @Test
    public void testGetExtension() {
    }

    /**
     * Test of taskFinished method, of class MLearnFileOpener.
     */
    @Test
    public void testTaskFinished() {
    }

    /**
     * Test of openDataFile method, of class MLearnFileOpener.
     */
    @Test
    public void testOpenDataFile() {
    }

    /**
     * Test of isAsciiFile method, of class MLearnFileOpener.
     */
    @Test
    public void testIsAsciiFile() throws Exception {
        assertEquals(true, subject.isAsciiFile(fixtures.iris()));
        assertEquals(true, subject.isAsciiFile(fixtures.bosthouse()));
        assertEquals(true, subject.isAsciiFile(fixtures.cars()));
        assertEquals(true, subject.isAsciiFile(fixtures.forrestFires()));
        assertEquals(true, subject.isAsciiFile(fixtures.irisMissing()));
        assertEquals(true, subject.isAsciiFile(fixtures.dermatology()));
    }
}
