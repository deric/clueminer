package org.clueminer.cluster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.exception.UnsupportedAttributeType;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class DatasetFixture {

    private static CommonFixture tf = new CommonFixture();

    public static Map<Dataset<Instance>, Integer> allDatasets() {
        Map<Dataset<Instance>, Integer> datasets = new HashMap<Dataset<Instance>, Integer>();
        datasets.put(DatasetFixture.glass(), 7);
        datasets.put(DatasetFixture.insect(), 3);
        datasets.put(DatasetFixture.ionosphere(), 2);
        datasets.put(DatasetFixture.iris(), 3);
        datasets.put(DatasetFixture.sonar(), 2);
        datasets.put(DatasetFixture.wine(), 3);
        datasets.put(DatasetFixture.vehicle(), 4);
        datasets.put(DatasetFixture.yeast(), 10);
        return datasets;
    }

    public static Dataset<Instance> iris() {
        Dataset<Instance> data = new SampleDataset();
        try {
            String datasetName = "iris";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.irisArff(), data, 4);
            data.setName(datasetName);
        } catch (UnsupportedAttributeType ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> wine() {
        Dataset<Instance> data = new SampleDataset();
        try {
            String datasetName = "wine";
            // 1st attribute is class identifier (1-3)
            data.setName(datasetName);
            File file = tf.wineArff();
            ARFFHandler arff = new ARFFHandler();
            arff.load(file, data, 0);
        } catch (UnsupportedAttributeType ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> yeast() {
        Dataset<Instance> data = new SampleDataset();
        try {
            String datasetName = "yeast";
            // 10th attribute is class identifier
            ArrayList<Integer> skippedIndexes = new ArrayList<Integer>();
            skippedIndexes.add(0); //we skip instance name
            File file = tf.yeastData();
            data.setName(datasetName);
            ARFFHandler arff = new ARFFHandler();
            arff.load(file, data, 9, "\\s+", skippedIndexes);
        } catch (UnsupportedAttributeType ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> insect() {
        Dataset<Instance> data = new SampleDataset();
        try {
            String datasetName = "insect";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.insectArff(), data, 3);
            data.setName(datasetName);
        } catch (UnsupportedAttributeType ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> vehicle() {
        Dataset<Instance> data = new SampleDataset();
        try {
            String datasetName = "vehicle";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.vehicleArff(), data, 18);
            data.setName(datasetName);
        } catch (UnsupportedAttributeType ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> ionosphere() {
        Dataset<Instance> data = new SampleDataset();
        try {
            String datasetName = "ionosphere";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.ionosphereArff(), data, 34);
            data.setName(datasetName);
        } catch (UnsupportedAttributeType ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> glass() {
        Dataset<Instance> data = new SampleDataset();
        try {
            String datasetName = "glass";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.ionosphereArff(), data, 9);
            data.setName(datasetName);
        } catch (UnsupportedAttributeType ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }
    
    public static Dataset<Instance> sonar() {
        Dataset<Instance> data = new SampleDataset();
        try {
            String datasetName = "sonar";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.sonarArff(), data, 60);
            data.setName(datasetName);
        } catch (UnsupportedAttributeType ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }
}