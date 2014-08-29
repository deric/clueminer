package org.clueminer.posturomed;

import java.io.IOException;
import org.clueminer.fixtures.PosturomedFixture;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

/**
 *
 * @author tombart
 */
public class PosturomedImporterTest {

    private static PosturomedImporter importer;
    private static PosturomedFixture fixture;

    public PosturomedImporterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        fixture = new PosturomedFixture();
        importer = new PosturomedImporter(fixture.testData());
        ProgressHandle ph = ProgressHandleFactory.createHandle("Importing dataset");
        importer.setProgressHandle(ph);
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
     * Test of cancel method, of class PosturomedImporter.
     */
    @Test
    public void testCancel() {
    }

    /**
     * Test of setProgressTicket method, of class PosturomedImporter.
     */
    @Test
    public void testSetProgressTicket() {
    }

    /**
     * Test of setProgressHandle method, of class PosturomedImporter.
     */
    @Test
    public void testSetProgressHandle() {
    }

    /**
     * Test of run method, of class PosturomedImporter.
     */
    @Test
    public void testRun() {
        importer.run();
    }

    /**
     * Test of getFile method, of class PosturomedImporter.
     */
    @Test
    public void testGetFile() {
    }

    /**
     * Test of setFile method, of class PosturomedImporter.
     */
    @Test
    public void testSetFile() {
    }

    /**
     * Test of translatePosition method, of class PosturomedImporter.
     */
    @Test
    public void testTranslatePosition() throws Exception {
    }

    /**
     * Test of getDataset method, of class PosturomedImporter.
     */
    @Test
    public void testGetDataset() {
    }
}