package org.clueminer.dataset.benchmark;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
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
        //dataset, num_true_classes
        Map<Dataset<Instance>, Integer> datasets = new HashMap<Dataset<Instance>, Integer>();
        datasets.put(DatasetFixture.dermatology(), 6);
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
        Dataset<Instance> data = new ArrayDataset<Instance>(150, 4);
        try {
            String datasetName = "iris";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.irisArff(), data, 4);
            data.setName(datasetName);
        }  catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> wine() {
        Dataset<Instance> data = new ArrayDataset<Instance>(178, 13);
        try {
            String datasetName = "wine";
            // 1st attribute is class identifier (1-3)
            data.setName(datasetName);
            File file = tf.wineArff();
            ARFFHandler arff = new ARFFHandler();
            arff.load(file, data, 0);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> yeast() {
        Dataset<Instance> data = new ArrayDataset<Instance>(1484, 8);
        try {
            String datasetName = "yeast";
            // 10th attribute is class identifier
            ArrayList<Integer> skippedIndexes = new ArrayList<Integer>();
            skippedIndexes.add(0); //we skip instance name
            File file = tf.yeastData();
            data.setName(datasetName);
            ARFFHandler arff = new ARFFHandler();
            arff.load(file, data, 9, "\\s+", skippedIndexes);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> insect() {
        Dataset<Instance> data = new ArrayDataset<Instance>(30, 3);
        try {
            String datasetName = "insect";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.insectArff(), data, 3);
            data.setName(datasetName);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> vehicle() {
        Dataset<Instance> data = new ArrayDataset<Instance>(846, 18);
        try {
            String datasetName = "vehicle";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.vehicleArff(), data, 18);
            data.setName(datasetName);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> ionosphere() {
        Dataset<Instance> data = new ArrayDataset<Instance>(351, 34);
        try {
            String datasetName = "ionosphere";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.ionosphereArff(), data, 34);
            data.setName(datasetName);
        }catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> glass() {
        Dataset<Instance> data = new ArrayDataset<Instance>(214, 9);
        try {
            String datasetName = "glass";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.glassArff(), data, 9);
            data.setName(datasetName);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> sonar() {
        Dataset<Instance> data = new ArrayDataset<Instance>(208, 60);
        try {
            String datasetName = "sonar";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.sonarArff(), data, 60);
            data.setName(datasetName);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> dermatology() {
        Dataset<Instance> data = new ArrayDataset<Instance>(366, 34);
        try {
            String datasetName = "dermatology";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.dermatologyArff(), data, 34);
            data.setName(datasetName);
        }catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }
}