/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.dataset.benchmark;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.exception.ParserError;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.arff.ARFFHandler;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class DatasetFixture {

    private static final CommonFixture tf = new CommonFixture();

    public static Map<Dataset<? extends Instance>, Integer> allDatasets() {
        Map<Dataset<? extends Instance>, Integer> datasets = new HashMap<>();

        //dataset, num_true_classes
        datasets.put(DatasetFixture.dermatology(), 6);
        datasets.put(DatasetFixture.glass(), 7);
        datasets.put(DatasetFixture.insect(), 3);
        datasets.put(DatasetFixture.ionosphere(), 2);
        datasets.put(DatasetFixture.iris(), 3);
        datasets.put(DatasetFixture.sonar(), 2);
        datasets.put(DatasetFixture.wine(), 3);
        datasets.put(DatasetFixture.vehicle(), 4);
        datasets.put(DatasetFixture.yeast(), 10);
        datasets.put(DatasetFixture.zoo(), 6);

        return datasets;
    }

    public static Dataset<Instance> iris() {
        Dataset<Instance> data = new ArrayDataset<>(150, 4);
        try {
            String datasetName = "iris";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.irisArff(), data, 4);
            data.setName(datasetName);
        } catch (IOException | ParserError ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> wine() {
        Dataset<Instance> data = new ArrayDataset<>(178, 13);
        try {
            String datasetName = "wine";
            // 1st attribute is class identifier (1-3)
            data.setName(datasetName);
            File file = tf.wineArff();
            ARFFHandler arff = new ARFFHandler();
            arff.load(file, data, 0);
        } catch (IOException | ParserError ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> yeast() {
        Dataset<Instance> data = new ArrayDataset<>(1484, 8);
        try {
            String datasetName = "yeast";
            // 10th attribute is class identifier
            ArrayList<Integer> skippedIndexes = new ArrayList<>();
            skippedIndexes.add(0); //we skip instance name
            File file = tf.yeastData();
            data.setName(datasetName);
            ARFFHandler arff = new ARFFHandler();
            arff.load(file, data, 9, "\\s+", skippedIndexes);
        } catch (IOException | ParserError ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> insect() {
        Dataset<Instance> data = new ArrayDataset<>(30, 3);
        try {
            String datasetName = "insect";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.insectArff(), data, 3);
            data.setName(datasetName);
        } catch (IOException | ParserError ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> vehicle() {
        Dataset<Instance> data = new ArrayDataset<>(846, 18);
        try {
            String datasetName = "vehicle";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.vehicleArff(), data, 18);
            data.setName(datasetName);
        } catch (IOException | ParserError ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> ionosphere() {
        Dataset<Instance> data = new ArrayDataset<>(351, 34);
        try {
            String datasetName = "ionosphere";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.ionosphereArff(), data, 34);
            data.setName(datasetName);
        } catch (IOException | ParserError ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> glass() {
        Dataset<Instance> data = new ArrayDataset<>(214, 9);
        try {
            String datasetName = "glass";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.glassArff(), data, 9);
            data.setName(datasetName);
        } catch (IOException | ParserError ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> sonar() {
        Dataset<Instance> data = new ArrayDataset<>(208, 60);
        try {
            String datasetName = "sonar";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.sonarArff(), data, 60);
            data.setName(datasetName);
        } catch (IOException | ParserError ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> dermatology() {
        Dataset<Instance> data = new ArrayDataset<>(366, 33);
        try {
            String datasetName = "dermatology";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.dermatologyArff(), data, 33);
            data.setName(datasetName);
        } catch (IOException | ParserError ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    public static Dataset<Instance> zoo() {
        Dataset<Instance> data = new ArrayDataset<>(101, 16);
        try {
            String datasetName = "zoo";
            ARFFHandler arff = new ARFFHandler();
            arff.load(tf.zoo2Arff(), data, 18);
            data.setName(datasetName);
        } catch (IOException | ParserError ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }
}
