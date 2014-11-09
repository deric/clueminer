package org.clueminer.io;

import java.util.ArrayList;
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

    @Test
    public void testAttributeDefinition() throws Exception {
        assertTrue(arff.isValidAttributeDefinition("@ATTRIBUTE sepallength	REAL"));
        assertTrue(arff.isValidAttributeDefinition("@attribute a01 real"));
        assertTrue(arff.isValidAttributeDefinition("@attribute 'CIRCULARITY' real"));
        assertTrue(arff.isValidAttributeDefinition("@attribute 'DISTANCE CIRCULARITY' real"));
        assertTrue(arff.isValidAttributeDefinition("@attribute 'MAX.LENGTH ASPECT RATIO' real"));
        assertTrue(arff.isValidAttributeDefinition("@attribute definite_borders {0,1,2,3}"));
        assertTrue(arff.isValidAttributeDefinition("@attribute OD280/OD315_of_diluted_wines REAL"));
        assertTrue(arff.isValidAttributeDefinition("@attribute F22 {0,1}"));
    }

    /**
     * Test of load method, of class ARFFHandler.
     *
     * @throws java.lang.Exception
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
        data = new SampleDataset();
        ArrayList<Integer> skippedIndexes = new ArrayList<>();
        skippedIndexes.add(0); //we skip instance name
        arff.load(tf.yeastData(), data, 9, "\\s+", skippedIndexes);
        assertEquals(8, data.attributeCount());
        assertEquals(1484, data.size());
    }

    @Test
    public void testLoad_3args() throws Exception {
    }

    @Test
    public void testClassAttrDefinition() {
        //from vehicle dataset
        assertTrue(arff.isValidAttributeDefinition("@attribute 'Class' {opel,saab,bus,van}"));
        assertTrue(arff.isValidAttributeDefinition("@attribute class {east,west}"));
        //from breast-w - in square brackets there is range of values
        assertTrue(arff.isValidAttributeDefinition("@attribute Cell_Size_Uniformity integer [1,10]"));
        //from cmc
        assertTrue(arff.isValidAttributeDefinition("@attribute Number_of_children_ever_born INTEGER"));
    }

    /**
     * Test of load method, of class ARFFHandler.
     */
    @Test
    public void testLoad_5args() throws Exception {
    }
}
