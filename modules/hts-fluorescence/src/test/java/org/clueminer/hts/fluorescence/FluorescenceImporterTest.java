package org.clueminer.hts.fluorescence;

import java.io.IOException;
import org.clueminer.fixtures.FluorescenceFixture;
import org.clueminer.hts.api.HtsInstance;
import org.clueminer.hts.api.HtsPlate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class FluorescenceImporterTest {

    private static FluorescenceImporter importer;
    private static double delta = 1e-9;

    public FluorescenceImporterTest() {
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
     * Test of cancel method, of class FluorescenceImporter.
     */
    @Test
    public void testCancel() {
    }

    /**
     * Test of setProgressTicket method, of class FluorescenceImporter.
     */
    @Test
    public void testSetProgressTicket() {
    }

    /**
     * Test of run method, of class FluorescenceImporter.
     */
    @Test
    public void testRun() {


        try {
            FluorescenceFixture tf = new FluorescenceFixture();
            importer = new FluorescenceImporter(tf.testData());
            ProgressHandle ph = ProgressHandleFactory.createHandle("Importing dataset");
            importer.setProgressHandle(ph);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        importer.run();
        HtsPlate<HtsInstance> dataset = importer.getDataset();
        //should work, but we use tmp files extracted from jar, which have different name
        //assertEquals("AP-01_2012112", dataset.getName());
        assertEquals(15, dataset.attributeCount());
        assertEquals(1536, dataset.size());

        HtsInstance inst = dataset.instance(0);
        assertEquals("A1", inst.getName());
        assertEquals(424, inst.value(0), delta);
        assertEquals(4087, inst.value(dataset.attributeCount() - 1), delta);
        assertEquals(15, inst.size());
        System.out.println("size: "+inst.size());
        System.out.println("a1: "+inst.toString());
        assertEquals("CP-001073", dataset.getName());
    }

    /**
     * Test of getFile method, of class FluorescenceImporter.
     */
    @Test
    public void testGetFile() {
    }

    /**
     * Test of setFile method, of class FluorescenceImporter.
     */
    @Test
    public void testSetFile() {
    }
}