package org.clueminer.importer.impl;

import java.io.IOException;
import java.util.Collection;
import org.clueminer.fixtures.ImageFixture;
import org.clueminer.fixtures.MLearnFixture;
import org.clueminer.spi.FileImporter;
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
public class ImportControllerImplTest {

    private static final ImportControllerImpl subject = new ImportControllerImpl();
    private final MLearnFixture fixtures = new MLearnFixture();

    public ImportControllerImplTest() {
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

    /**
     * Test of importFile method, of class ImportControllerImpl.
     */
    @Test
    public void testImportFile_File() throws Exception {
    }

    /**
     * Test of importFile method, of class ImportControllerImpl.
     */
    @Test
    public void testImportFile_File_FileImporter() throws Exception {
    }

    /**
     * Test of importFile method, of class ImportControllerImpl.
     */
    @Test
    public void testImportFile_Reader_FileImporter() {
    }

    /**
     * Test of importFile method, of class ImportControllerImpl.
     */
    @Test
    public void testImportFile_InputStream_FileImporter() {
    }

    /**
     * Test of getFileImporter method, of class ImportControllerImpl.
     */
    @Test
    public void testGetFileImporter_File() throws IOException {
        FileImporter im = subject.getFileImporter(fixtures.iris());
        //assertNotNull(im);
        System.out.println("importer: " + im);
//        assertEquals(CsvImporter.class, im.getClass());
    }

    /**
     * Test of getFileImporter method, of class ImportControllerImpl.
     */
    @Test
    public void testGetFileImporter_String() {
    }

    /**
     * Test of process method, of class ImportControllerImpl.
     */
    @Test
    public void testProcess_Container() {
    }

    /**
     * Test of process method, of class ImportControllerImpl.
     */
    @Test
    public void testProcess_3args() {
    }

    /**
     * Test of getFileTypes method, of class ImportControllerImpl.
     */
    @Test
    public void testGetFileTypes() {
    }

    /**
     * Test of isFileSupported method, of class ImportControllerImpl.
     */
    @Test
    public void testIsFileSupported() throws IOException {

    }

    /**
     * Test of isAccepting method, of class ImportControllerImpl.
     * @throws java.io.IOException
     */
    @Test
    public void testIsAccepting() throws IOException {
        //should support iris data
        assertNotNull(subject);
        assertNotNull(fixtures);

        assertEquals(true, subject.isAccepting(fixtures.iris()));
        assertEquals(true, subject.isAccepting(fixtures.cars()));
        assertEquals(true, subject.isAccepting(fixtures.bosthouse()));
        assertEquals(true, subject.isAccepting(fixtures.dermatology()));
        assertEquals(true, subject.isAccepting(fixtures.forrestFires()));
        ImageFixture inf = new ImageFixture();
        assertEquals(false, subject.isAccepting(inf.insect3d()));
    }

    /**
     * Test of getUI method, of class ImportControllerImpl.
     */
    @Test
    public void testGetUI() {
    }

    /**
     * Test of getWizardUI method, of class ImportControllerImpl.
     */
    @Test
    public void testGetWizardUI() {
    }

    /**
     * Test of importDatabase method, of class ImportControllerImpl.
     */
    @Test
    public void testImportDatabase() {
    }

}
