package org.clueminer.fixtures.clustering;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.clueminer.io.CsvLoader;
import org.openide.util.Exceptions;

/**
 * Preloaded "real world" datasets for testing purposes
 *
 * @author Tomas Barton
 */
public class FakeDatasets {

    private static Dataset<? extends Instance> irisData;
    private static Dataset<? extends Instance> wine;
    private static Dataset<? extends Instance> school;
    private static Dataset<? extends Instance> usArrests;
    private static Dataset<? extends Instance> glassDataset;
    private static final CommonFixture fixture = new CommonFixture();

    public static Dataset<? extends Instance> schoolData() {
        if (school == null) {
            CsvLoader loader = new CsvLoader();
            school = new ArrayDataset(17, 4);
            loader.setClassIndex(4);
            loader.setSeparator(' ');
            try {
                loader.load(fixture.schoolData(), school);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return school;
    }

    public static Dataset<? extends Instance> glassDataset() {
        if (glassDataset == null) {
            CommonFixture tf = new CommonFixture();
            glassDataset = new ArrayDataset(214, 9);
            ARFFHandler arff = new ARFFHandler();
            try {
                arff.load(tf.glassArff(), glassDataset, 9);
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return glassDataset;
    }

    public static Dataset<? extends Instance> irisDataset() {
        if (irisData == null) {
            CommonFixture tf = new CommonFixture();
            irisData = new SampleDataset();
            ARFFHandler arff = new ARFFHandler();
            try {
                arff.load(tf.irisArff(), irisData, 4);
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return irisData;
    }

    public static Dataset<? extends Instance> usArrestData() {
        if (usArrests == null) {
            CsvLoader loader = new CsvLoader();
            usArrests = new ArrayDataset(17, 4);
            loader.setClassIndex(0);
            loader.setSeparator(',');
            loader.setHasHeader(true);
            try {
                loader.load(fixture.usArrestsCsv(), usArrests);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return usArrests;
    }

    public static Dataset<? extends Instance> simpleData() {
        Dataset<Instance> data = new ArrayDataset<>(4, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        data.builder().create(new double[]{0, 0}, "A");
        data.builder().create(new double[]{1, 3}, "B");
        data.builder().create(new double[]{2, 2}, "C");
        data.builder().create(new double[]{2, 1}, "D");
        return data;
    }

}
