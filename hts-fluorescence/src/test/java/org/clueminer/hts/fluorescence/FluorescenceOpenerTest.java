package org.clueminer.hts.fluorescence;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.clueminer.fixtures.FluorescenceFixture;
import org.clueminer.utils.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class FluorescenceOpenerTest {

    private static FluorescenceFixture fixture;
    private static FluorescenceOpener test;

    public FluorescenceOpenerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        fixture = new FluorescenceFixture();
        test = new FluorescenceOpener();
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
     * Test of detectMIME method, of class FluorescenceOpener.
     */
    @Test
    public void testDetectMIME() {
        try {
            Collection res = test.detectMIME(fixture.testData());
            assertEquals("text/x-tex", res.toString());
            System.out.println("res: " + res);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Test of open method, of class FluorescenceOpener.
     */
    @Test
    public void testOpen() {
        try {
            File file = fixture.testData();
            boolean res = test.openFile(file);
            assertTrue(res);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Test of openFluorescenceFile method, of class FluorescenceOpener.
     */
    @Test
    public void testOpenFluorescenceFile() {
    }

    /**
     * Test of taskFinished method, of class FluorescenceOpener.
     */
    @Test
    public void testTaskFinished() {
    }
}