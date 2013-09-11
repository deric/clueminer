package org.clueminer.io;

import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.dataset.plugin.TimeseriesDataset;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.fixtures.TimeseriesFixture;
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
public class FileHandlerTest {

    public FileHandlerTest() {
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
     * Test of loadDataset method, of class FileHandler.
     */
    @Test
    public void testLoadDataset_3args_1() throws Exception {
    }

    /**
     * Test of loadDataset method, of class FileHandler.
     */
    @Test
    public void testLoadDataset_3args_2() throws Exception {
    }

    /**
     * Test of loadDataset method, of class FileHandler.
     */
    @Test
    public void testLoadDataset_File_Dataset() throws Exception {
    }

    /**
     * Test of loadDataset method, of class FileHandler.
     */
    @Test
    public void testLoadDataset_4args() throws Exception {
        CommonFixture tf = new CommonFixture();
        Dataset<Instance> data = new SampleDataset();

        FileHandler.loadDataset(tf.schoolData(), data, 4, " ");
        assertEquals(4, data.attributeCount());
        assertEquals(17, data.size());
    }

    /**
     * Test of loadSparseDataset method, of class FileHandler.
     */
    @Test
    public void testLoadSparseDataset_3args() throws Exception {
    }

    /**
     * Test of loadSparseDataset method, of class FileHandler.
     */
    @Test
    public void testLoadSparseDataset_5args() throws Exception {
    }

    /**
     * Test of exportDataset method, of class FileHandler.
     */
    @Test
    public void testExportDataset_4args() throws Exception {
    }

    /**
     * Test of exportDataset method, of class FileHandler.
     */
    @Test
    public void testExportDataset_Dataset_File() throws Exception {
    }

    /**
     * Test of exportDataset method, of class FileHandler.
     */
    @Test
    public void testExportDataset_3args() throws Exception {
    }
}