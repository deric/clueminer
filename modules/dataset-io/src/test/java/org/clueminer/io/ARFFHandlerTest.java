package org.clueminer.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.fixtures.CommonFixture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ARFFHandlerTest {

    private static ARFFHandler arff;
    private static CommonFixture tf;

    @BeforeClass
    public static void setUpClass() {
        arff = new ARFFHandler();
        tf = new CommonFixture();
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

    @Test
    public void testAttributeDefinitionCase() throws Exception {
        //definition should be case-insensitive (be benevolent)
        assertTrue(arff.isValidAttributeDefinition("@Attribute F22 {0,1}"));
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
    }

    @Test
    public void testLoadIris() throws FileNotFoundException, IOException {
        Dataset<? extends Instance> data = new ArrayDataset(150, 4);
        arff.load(tf.irisArff(), data, 4);
        assertEquals(4, data.attributeCount());
        assertEquals(150, data.size());
    }

    @Test
    public void testLoadIrisWithoutClassIndex() throws FileNotFoundException, IOException {
        Dataset<? extends Instance> data = new ArrayDataset(150, 4);
        arff.load(tf.irisArff(), data);
        assertEquals(4, data.attributeCount());
        assertEquals(150, data.size());
        //there should be 3 classes
        assertEquals(3, data.getClasses().size());
    }

    @Test
    public void testLoadYeast() throws FileNotFoundException, IOException {
        Dataset<? extends Instance> data = new ArrayDataset(1484, 8);
        ArrayList<Integer> skippedIndexes = new ArrayList<>();
        skippedIndexes.add(0); //we skip instance name
        arff.load(tf.yeastData(), data, 9, "\\s+", skippedIndexes);
        assertEquals(8, data.attributeCount());
        assertEquals(1484, data.size());
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
        //from glass
        assertTrue(arff.isValidAttributeDefinition("@attribute 'Type' { 'build wind float', 'build wind non-float', 'vehic wind float', 'vehic wind non-float', containers, tableware, headlamps}"));
        assertTrue(arff.isValidAttributeDefinition("@attribute 'Type' { build-wind }"));
        assertTrue(arff.isValidAttributeDefinition("@attribute 'Type' { build_wind }"));
        assertTrue(arff.isValidAttributeDefinition("@attribute HAIR integer [0, 1]"));
    }

    @Test
    public void testLoadZoo2() throws FileNotFoundException, IOException {
        Dataset<? extends Instance> data = new ArrayDataset(101, 16);
        arff.load(tf.zoo2Arff(), data);
        assertEquals(16, data.attributeCount());
        assertEquals(101, data.size());
    }

    @Test
    public void testLoadGlass() throws FileNotFoundException, IOException {
        Dataset<? extends Instance> data = new ArrayDataset(217, 9);
        arff.load(tf.glassArff(), data);
        assertEquals(9, data.attributeCount());
        assertEquals(214, data.size());
        //dataset info list 7 classes, but there are only 6 of them
        assertEquals(6, data.getClasses().size());
    }

    @Test
    public void testConsume() throws ParserError {
        String res = arff.consume("verylongstring", "very");
        assertEquals("longstring", res);

        res = arff.consume("'aaa", "'");
        assertEquals("aaa", res);
    }

    @Test
    public void testAttrDefinition() throws ParserError {
        AttrHolder a = arff.parseAttribute("@attribute 'Class' {opel,saab,bus,van}");
        assertEquals("Class", a.getName());
        assertEquals("opel,saab,bus,van", a.getAllowed());
        assertEquals(true, "REAL".matches("^[A-Za-z]+"));
        assertEquals(true, "\t".matches("^\\s(.*)"));
        assertEquals(true, " ".matches("^\\s(.*)"));

        a = arff.parseAttribute("@ATTRIBUTE	'K'	REAL");
        assertEquals("K", a.getName());
        assertEquals("REAL", a.getType());

        a = arff.parseAttribute("@ATTRIBUTE sepallength	REAL");
        assertEquals("sepallength", a.getName());
        assertEquals("REAL", a.getType());

        a = arff.parseAttribute("@attribute erythema {0,1,2,3}");
        assertEquals("erythema", a.getName());
        assertEquals("0,1,2,3", a.getAllowed());
    }

    @Test
    public void testGuessType() throws ParserError {
        AttrHolder a = arff.parseAttribute("@attribute erythema {0,1,2,3}");
        assertEquals("erythema", a.getName());
        assertEquals("0,1,2,3", a.getAllowed());

        a = arff.parseAttribute("@attribute saw-tooth_appearance_of_retes {0,1,2,3}");
        assertEquals("saw-tooth_appearance_of_retes", a.getName());
        assertEquals("0,1,2,3", a.getAllowed());
        assertEquals("INTEGER", a.getType());
    }

    @Test
    public void testDermatology() throws ParserError, FileNotFoundException, IOException {
        Dataset<Instance> data = new ArrayDataset<>(366, 33);

        String datasetName = "dermatology";
        arff.load(tf.dermatologyArff(), data, 33);
        assertEquals(33, data.attributeCount());
        assertEquals(366, data.size());
        data.setName(datasetName);
    }

    @Test
    public void testLoadIonosphere() throws FileNotFoundException, IOException {
        Dataset<? extends Instance> data = new ArrayDataset(351, 34);
        arff.load(tf.ionosphereArff(), data);
        assertEquals(34, data.attributeCount());
        assertEquals(351, data.size());

        //another version of ionosphere
        data = new ArrayDataset(351, 34);
        arff.load(tf.ionosphereArff2(), data);
        assertEquals(34, data.attributeCount());
        assertEquals(351, data.size());
    }

    @Test
    public void testDs577() throws ParserError, FileNotFoundException, IOException {
        Dataset<Instance> data = new ArrayDataset<>(366, 2);

        String datasetName = "DS-577";
        arff.load(tf.ds577(), data, 2);
        assertEquals(2, data.attributeCount());
        assertEquals(577, data.size());
        data.setName(datasetName);
    }

}
