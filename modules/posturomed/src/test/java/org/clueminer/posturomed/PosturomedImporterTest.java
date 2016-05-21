package org.clueminer.posturomed;

import java.io.IOException;
import org.clueminer.fixtures.PosturomedFixture;
import org.junit.BeforeClass;
import org.junit.Test;
import org.netbeans.api.progress.ProgressHandle;

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
        ProgressHandle ph = ProgressHandle.createHandle("Importing dataset");
        importer.setProgressHandle(ph);
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
