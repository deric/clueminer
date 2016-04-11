package org.clueminer.io;

import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.impl.SampleDataset;
import org.clueminer.fixtures.CommonFixture;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tomas Barton
 */
public class DatasetConvertorTest {

    private static Dataset data;

    public DatasetConvertorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        CommonFixture tf = new CommonFixture();
        data = new SampleDataset();
        data.attributeBuilder().create("sepal length", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("sepal width", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("petal length", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("petal width", BasicAttrType.NUMERICAL);

        assertTrue(FileHandler.loadDataset(tf.irisData(), data, 4, ","));
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of convert method, of class DatasetConvertor.
     */
    @Test
    public void testConvert() {
        assertEquals(4, data.attributeCount());
    }


    /**
     * Test of toARFF method, of class DatasetConvertor.
     */
    @Test
    public void testToARFF() {
        String res = DatasetConvertor.toARFF(data);
        String[] lines = res.split("\n");
        assertEquals("@relation untitled", lines[0]);
        assertEquals(158, lines.length);
    }

    /**
     * Test of quote method, of class DatasetConvertor.
     */
    @Test
    public void testQuote() {
    }

    /**
     * Test of backQuoteChars method, of class DatasetConvertor.
     */
    @Test
    public void testBackQuoteChars() {
    }
}
