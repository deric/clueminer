package org.clueminer.io;

import java.io.File;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.fixtures.CommonFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class CsvLoaderTest {

    private static CsvLoader subject;
    private static final CommonFixture fixture = new CommonFixture();
    private static final double delta = 1e-9;
    public CsvLoaderTest() {
    }

    @Before
    public void setUp() {
        subject = new CsvLoader();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of load method, of class CsvLoader.
     * @throws java.lang.Exception
     */
    @Test
    public void testLoad_File_Dataset() throws Exception {
        File file = fixture.insectCsv();
        Dataset<Instance> dataset = new ArrayDataset<Instance>(30, 3);
        subject.setClassIndex(3);
        subject.load(file, dataset);
        assertEquals(3, dataset.attributeCount());
        assertEquals(30, dataset.size());
        assertEquals(true, dataset.getAttribute(0).isNumerical());
        assertEquals(191, dataset.instance(0).value(0), delta);
    }

    /**
     * Test of load method, of class CsvLoader.
     */
    @Test
    public void testLoad_File() {
    }

    /**
     * Test of hasHeader method, of class CsvLoader.
     */
    @Test
    public void testHasHeader() {
    }

    /**
     * Test of setHeader method, of class CsvLoader.
     */
    @Test
    public void testSetHeader() {
    }

    /**
     * Test of getSeparator method, of class CsvLoader.
     */
    @Test
    public void testGetSeparator() {
    }

    /**
     * Test of setSeparator method, of class CsvLoader.
     */
    @Test
    public void testSetSeparator() {
    }

    /**
     * Test of getClassIndex method, of class CsvLoader.
     */
    @Test
    public void testGetClassIndex() {
    }

    /**
     * Test of setClassIndex method, of class CsvLoader.
     */
    @Test
    public void testSetClassIndex() {
    }

    /**
     * Test of getSkipIndex method, of class CsvLoader.
     */
    @Test
    public void testGetSkipIndex() {
    }

    /**
     * Test of setSkipIndex method, of class CsvLoader.
     */
    @Test
    public void testSetSkipIndex() {
    }

    /**
     * Test of getDataset method, of class CsvLoader.
     */
    @Test
    public void testGetDataset() {
    }

    /**
     * Test of setDataset method, of class CsvLoader.
     */
    @Test
    public void testSetDataset() {
    }

    /**
     * Test of skip method, of class CsvLoader.
     */
    @Test
    public void testSkip() {
    }

    /**
     * Test of isSkipHeader method, of class CsvLoader.
     */
    @Test
    public void testIsSkipHeader() {
    }

    /**
     * Test of setSkipHeader method, of class CsvLoader.
     */
    @Test
    public void testSetSkipHeader() {
    }

}
