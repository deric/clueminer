package org.clueminer.mlearn;

import java.io.IOException;
import java.util.Collection;
import org.clueminer.fixtures.ImageFixture;
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

    private final MLearnFileOpener subject = new MLearnFileOpener();
    private final MLearnFixture fixtures = new MLearnFixture();

    public MLearnFileOpenerTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of detectMIME method, of class MLearnFileOpener.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testDetectMIME() throws IOException {
        Collection col = subject.detectMIME(fixtures.iris());
        System.out.println("iris: " + col.toString());
        assertEquals(true, col.contains("application/octet-stream"));
        col = subject.detectMIME(fixtures.dermatology());
        System.out.println(col);
        assertEquals(true, col.contains("text/x-tex"));
        ImageFixture inf = new ImageFixture();
        col = subject.detectMIME(inf.insect3d());
        System.out.println("image: " + col);
    }

    @Test
    public void testDetectMIMECsv() throws IOException {
        Collection col = subject.detectMIME(fixtures.forrestFires());
        assertEquals(true, col.contains("application/octet-stream"));
    }

    /**
     * Test of isFileSupported method, of class MLearnFileOpener.
     *
     * @throws java.io.IOException
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
        ImageFixture inf = new ImageFixture();
        assertEquals(false, subject.isFileSupported(inf.insect3d()));
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
}
