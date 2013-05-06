package org.clueminer.io;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.fixtures.CommonFixture;
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
public class ARFFHandlerTest {

    private static ARFFHandler arff;
    private static CommonFixture tf;

    public ARFFHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        arff = new ARFFHandler();
        tf = new CommonFixture();
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
     * Test of load method, of class ARFFHandler.
     */
    @Test
    public void testLoad_File_Dataset() throws Exception {
        //load wine
        Dataset data = new SampleDataset();
        arff.load(tf.wineArff(), data, 0);
        assertEquals(13, data.attributeCount());
        assertEquals(178, data.size());

        //load iris
        data = new SampleDataset();
        arff.load(tf.irisArff(), data, 4);
        assertEquals(4, data.attributeCount());
        assertEquals(150, data.size());

        //load yeast
     /*   data = new SampleDataset();
        arff.load(tf.yeastData(), data, 9);
        assertEquals(4, data.attributeCount());
        assertEquals(150, data.size());*/
    }

    /**
     * Test of load method, of class ARFFHandler.
     */
    @Test
    public void testLoad_3args() throws Exception {
    }

    /**
     * Test of load method, of class ARFFHandler.
     */
    @Test
    public void testLoad_5args() throws Exception {
    }
}