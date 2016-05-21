package org.clueminer.mlearn;

import java.io.File;
import java.io.IOException;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.fixtures.MLearnFixture;
import org.clueminer.fixtures.TimeseriesFixture;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author tombart
 */
public class MLearnImporterTest {

    private MLearnImporter subject;
    private final CommonFixture fixture = new CommonFixture();
    private static final double delta = 1e-9;

    public MLearnImporterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
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
        ProgressHandle ph = ProgressHandle.createHandle("testing");
        subject = new MLearnImporter(f, ph);
        subject.run();
        Dataset<? extends Instance> dataset = subject.getDataset();
        assertNotNull(subject);
        // assertEquals(150, dataset.size());
        System.out.println(dataset.instance(0).toString());
        System.out.println(dataset.toString());
    }

    /**
     * IR spectrum
     *
     * @throws IOException
     */
    @Test
    public void testTimeseries() throws IOException {
        MLearnFixture mlFixture = new MLearnFixture();
        File file = mlFixture.irBaClassification();
        System.out.println("file exists? " + file.exists());
        ProgressHandle ph = ProgressHandle.createHandle("testing");
        subject = new MLearnImporter(file, ph);
        subject.loadTimeseries(file);
        assertEquals(253, subject.getDataset().size());
        assertEquals(1651, subject.getDataset().attributeCount());
    }

    /**
     * Temporarily disabled until data publicly available
     *
     * @throws IOException
     */
    //@Test
    public void testMTimeseries() throws IOException {
        File dir = new File(getClass().getProtectionDomain().getCodeSource().
                getLocation().getFile() + "/../../../../../_data");
        String path = dir.getCanonicalPath() + "/" + "csv/Data_Milka_20131219_100260.csv";

        System.out.println("path: " + path);
        File file = new File(path);
        System.out.println("file exists? " + file.exists());
        ProgressHandle ph = ProgressHandle.createHandle("testing");
        subject = new MLearnImporter(file, ph);
        subject.loadMTimeseries(file);
        assertEquals(800, subject.getDataset().size());
        assertEquals(37, subject.getDataset().attributeCount());
        System.out.println("dataset size: " + subject.getDataset().size());
        Dataset<? extends Instance> dataset = subject.getDataset();
        assertNotNull(subject);
        Instance inst = dataset.instance(0);
        assertEquals(0.128, inst.value(0), delta);
        inst = dataset.instance(1);
        assertEquals(0.135, inst.value(0), delta);
        inst = dataset.instance(799);
        assertEquals(0.162, inst.value(0), delta);
    }

    @Test
    public void testLoadingDFiles() throws IOException {
        TimeseriesFixture tf = new TimeseriesFixture();
        ProgressHandle ph = ProgressHandle.createHandle("testing");
        File file = tf.ap01();
        subject = new MLearnImporter(file, ph);
        subject.loadDTimeseries(file);
        assertEquals(1536, subject.getDataset().size());
        assertEquals(15, subject.getDataset().attributeCount());
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
