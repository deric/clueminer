/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.io.arff;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.dataset.impl.SampleDataset;
import org.clueminer.exception.ParserError;
import org.clueminer.fixtures.BioFixture;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.fixtures.MLearnFixture;
import org.clueminer.io.AttrHolder;
import org.clueminer.io.LineIterator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 * @param <E>
 */
public class ARFFHandlerTest<E extends Instance> {

    private static ARFFHandler arff;
    private static CommonFixture tf;
    private static final double DELTA = 1e-9;

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

    @Test
    public void testLoad_File_Dataset() throws Exception {
        //load wine
        Dataset data = new SampleDataset();
        arff.load(tf.wineArff(), data, 0);
        assertEquals(13, data.attributeCount());
        assertEquals(178, data.size());
    }

    @Test
    public void testLoadIris() throws FileNotFoundException, IOException, ParserError {
        Dataset<? extends Instance> data = new ArrayDataset(150, 4);
        arff.load(tf.irisArff(), data, 4);
        assertEquals(4, data.attributeCount());
        assertEquals(150, data.size());
    }

    @Test
    public void testLoadIrisWithoutClassIndex() throws FileNotFoundException, IOException, ParserError {
        Dataset<? extends Instance> data = new ArrayDataset(150, 4);
        arff.load(tf.irisArff(), data);
        assertEquals(4, data.attributeCount());
        assertEquals(150, data.size());
        //there should be 3 classes
        assertEquals(3, data.getClasses().size());
    }

    @Test
    public void testLoadYeast() throws FileNotFoundException, IOException, ParserError {
        Dataset<E> data = new ArrayDataset(1484, 8);
        ArrayList<Integer> skippedIndexes = new ArrayList<>();
        skippedIndexes.add(0); //we skip instance name
        arff.load(new LineIterator(tf.yeastData()), data, 9, "\\s+", skippedIndexes);
        assertEquals(8, data.attributeCount());
        assertEquals(1484, data.size());
    }

    /**
     * auto-detect columns separator
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParserError
     */
    @Test
    public void testLoadYeastAuto() throws FileNotFoundException, IOException, ParserError {
        Dataset<E> data = new ArrayDataset(1484, 8);
        ArrayList<Integer> skippedIndexes = new ArrayList<>();
        skippedIndexes.add(0); //we skip instance name
        arff.load(tf.yeastData(), data);
        assertEquals(8, data.attributeCount());
        assertEquals(1484, data.size());
        System.out.println("0: " + data.instance(0).toString());
        assertEquals(8, data.instance(0).size());
        assertEquals(8, data.instance(1).size());
        //first line
        assertEquals(0.58, data.get(0, 0), DELTA);
        assertEquals(0.61, data.get(0, 1), DELTA);
        assertEquals("MIT", data.get(0).classValue());

        assertEquals("Yeast", data.getName());
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
        assertTrue(arff.isValidAttributeDefinition("@attribute CD45RA(La139)Di numeric"));
        assertTrue(arff.isValidAttributeDefinition("@attribute some{crazy}attr numeric"));
        assertTrue(arff.isValidAttributeDefinition("@attribute [foo'+] numeric"));
    }

    @Test
    public void testLoadZoo2() throws FileNotFoundException, IOException, ParserError {
        Dataset<? extends Instance> data = new ArrayDataset(101, 16);
        arff.load(tf.zoo2Arff(), data);
        assertEquals(16, data.attributeCount());
        assertEquals(101, data.size());
    }

    @Test
    public void testLoadGlass() throws FileNotFoundException, IOException, ParserError {
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

        a.setAllowed("");
        a = arff.parseAttribute("@attribute class {'Basophils','CD16+ NK cells','CD16- NK cells','CD34+CD38+CD123+ HSPCs','CD34+CD38+CD123- HSPCs','CD34+CD38lo HSCs','CD4 T cells','CD8 T cells','Mature B cells','Monocytes','Plasma B cells','Pre B cells','Pro B cells','pDCs'}");
        assertEquals("class", a.getName());
        assertEquals("'Basophils','CD16+ NK cells','CD16- NK cells','CD34+CD38+CD123+ HSPCs','CD34+CD38+CD123- HSPCs','CD34+CD38lo HSCs','CD4 T cells','CD8 T cells','Mature B cells','Monocytes','Plasma B cells','Pre B cells','Pro B cells','pDCs'", a.getAllowed());
    }

    @Test
    public void testAttrDefinitionWithUnusualChars() throws ParserError {
        AttrHolder a = arff.parseAttribute("@attribute CD45RA(La139)Di numeric");
        assertEquals("CD45RA(La139)Di", a.getName());
    }

    @Test
    public void testAttrParse() throws ParserError {
        AttrHolder a = arff.attrParse("@attribute class {'Basophils','CD16+ NK cells','CD16- NK cells','CD34+CD38+CD123+ HSPCs','CD34+CD38+CD123- HSPCs','CD34+CD38lo HSCs','CD4 T cells','CD8 T cells','Mature B cells','Monocytes','Plasma B cells','Pre B cells','Pro B cells','pDCs'}");

        assertEquals("class", a.getName());
        assertEquals("'Basophils','CD16+ NK cells','CD16- NK cells','CD34+CD38+CD123+ HSPCs','CD34+CD38+CD123- HSPCs','CD34+CD38lo HSCs','CD4 T cells','CD8 T cells','Mature B cells','Monocytes','Plasma B cells','Pre B cells','Pro B cells','pDCs'", a.getAllowed());
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
    public void testLoadIonosphere() throws FileNotFoundException, IOException, ParserError {
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
        assertEquals(datasetName, data.getName());
    }

    @Test
    public void testH1() throws ParserError, FileNotFoundException, IOException {
        Dataset<Instance> data = new ArrayDataset<>(100, 33);
        BioFixture bf = new BioFixture();

        String datasetName = "cytof.benchmark.h1";
        arff.load(bf.h1Test(), data);
        assertEquals(32, data.attributeCount());
        assertEquals(114, data.size());
        assertEquals("'Basophils'", data.get(0).classValue());
        assertEquals(datasetName, data.getName());
    }

    @Test
    public void testEcoli() throws ParserError, FileNotFoundException, IOException {
        Dataset<Instance> data = new ArrayDataset<>(336, 7);
        MLearnFixture bf = new MLearnFixture();

        String datasetName = "ecoli";
        arff.load(bf.ecoli(), data);
        assertEquals(7, data.attributeCount());
        assertEquals(336, data.size());
        assertEquals("cp", data.get(0).classValue());
        assertEquals(datasetName, data.getName());
        assertEquals("pp", data.get(335).classValue());
    }

}
