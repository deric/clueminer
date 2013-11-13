package org.clueminer.mlearn;

import java.io.File;
import java.io.IOException;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.CommonFixture;
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
public class MLearnImporterTest {

    private MLearnImporter subject;
    private CommonFixture fixture = new CommonFixture();

    public MLearnImporterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
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

    // @Test
    public void testIris() throws IOException {
        File f = fixture.irisData();
        ProgressHandle ph = ProgressHandleFactory.createHandle("testing");
        subject = new MLearnImporter(f, ph);
        subject.run();
        Dataset<Instance> dataset = subject.getDataset();
        assertNotNull(subject);
        // assertEquals(150, dataset.size());
        System.out.println(dataset.instance(0).toString());
        System.out.println(dataset.toString());
    }

    /**
     * Test of cancel method, of class MLearnImporter.
     */
    @Test
    public void testCancel() {
    }

    /**
     * Test of setProgressTicket method, of class MLearnImporter.
     */
    @Test
    public void testSetProgressTicket() {
    }

    /**
     * Test of setProgressHandle method, of class MLearnImporter.
     */
    @Test
    public void testSetProgressHandle() {
    }

    /**
     * Test of run method, of class MLearnImporter.
     */
    @Test
    public void testRun() {
    }

    /**
     * Test of getFile method, of class MLearnImporter.
     */
    @Test
    public void testGetFile() {
    }

    /**
     * Test of setFile method, of class MLearnImporter.
     */
    @Test
    public void testSetFile() {
    }

    /**
     * Test of getDataset method, of class MLearnImporter.
     */
    @Test
    public void testGetDataset() {
    }

    /**
     * Test of getFileExtension method, of class MLearnImporter.
     */
    @Test
    public void testGetFileExtension() {
        assertEquals("csv", MLearnImporter.getFileExtension("foo.csv"));
        assertEquals("csv", MLearnImporter.getFileExtension("/var/bar/foo.csv"));
        assertEquals("csv", MLearnImporter.getFileExtension("/var/bar.foo/foo.csv"));
    }
}
