package org.clueminer.io;

import java.io.File;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.math.Standardisation;
import org.clueminer.std.StdScale;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tomas Barton
 */
public class CSVLoaderTest {

    private static final CommonFixture tf = new CommonFixture();
    private static final CsvLoader loader = new CsvLoader();

    public CSVLoaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of load method, of class CsvLoader.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLoad_File_Dataset() throws Exception {
    }

    /**
     * Test of load method, of class CsvLoader.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCommaSeparatedFileWithHeader() throws Exception {
        File file = tf.miguel();
        assertTrue(file.exists());

        Dataset<Instance> output = new SampleDataset(15);
        loader.skip(0);
        loader.setClassIndex(1);
        loader.setDataset(output);
        assertTrue(loader.load(file));
        assertEquals(3, output.attributeCount());
        assertEquals(84, output.size());
        Standardisation std = new StdScale();
        double res[][] = std.optimize(output.arrayCopy(), output.size(), output.attributeCount());
        //Dump.matrix(res, "scale", 5);
        assertEquals(3, res[0].length);
        assertEquals(84, res.length);
    }
}
