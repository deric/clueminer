package org.clueminer.xcalibour.files;

import java.io.IOException;
import java.util.Collection;
import org.clueminer.fixtures.XCalibourFixture;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class XCalibourFileOpenerTest {

    private static XCalibourFileOpener test;
    private static XCalibourFixture fixture;

    public XCalibourFileOpenerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        test = new XCalibourFileOpener();
        fixture = new XCalibourFixture();
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
     * Test of detectMIME method, of class XCalibourFileOpener.
     */
    @Test
    public void testDetectMIME() {
        try {
            Collection<String> mime = test.detectMIME(fixture.testData());
            String str = mime.toString();
            System.out.println("mime: " + str);
            assertEquals(true, str.contains("octet-stream"));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Test of open method, of class XCalibourFileOpener.
     */
    @Test
    public void testOpen() throws IOException {        
        boolean res = test.openFile(fixture.testData());
        assertEquals(true, res);
    }

    /**
     * Test of taskFinished method, of class XCalibourFileOpener.
     */
    @Test
    public void testTaskFinished() {
    }

    /**
     * Test of openXCalibourFile method, of class XCalibourFileOpener.
     */
    @Test
    public void testOpenXCalibourFile() {
    }

    /**
     * Test of getExtension method, of class XCalibourFileOpener.
     */
    @Test
    public void testGetExtension() throws IOException {
        String ext = test.getExtension(fixture.testData().getPath());
        //in this test we use file extracted from a jar file or loaded from disk
        assertTrue(ext.equals("tmp") || ext.equals("cdf") );
    }
}