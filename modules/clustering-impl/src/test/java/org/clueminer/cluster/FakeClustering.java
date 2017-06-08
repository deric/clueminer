/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.cluster;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.dataset.impl.SampleDataset;
import org.clueminer.exception.ParserError;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.arff.ARFFHandler;
import org.clueminer.io.csv.CsvLoader;
import org.openide.util.Exceptions;

/**
 * For tests in this package we can't use external module unless cyclic
 * dependency would be introduced. Solution? data structures in one module
 * algorithms, measures etc. in other module
 *
 * @author Tomas Barton
 */
public class FakeClustering {

    private static Clustering<Instance, Cluster<Instance>> irisClusters;
    private static Clustering<Instance, Cluster<Instance>> irisWrong;
    private static Clustering<Instance, Cluster<Instance>> irisWrong2;
    private static Clustering<Instance, Cluster<Instance>> simpleClustering;
    private static Clustering<Instance, Cluster<Instance>> simpleResponse;
    private static Dataset<Instance> irisData;
    private static Dataset<Instance> wine;
    private static Dataset<Instance> kumar;
    private static Dataset<? extends Instance> school;
    private static final CommonFixture fixture = new CommonFixture();

    /**
     * Testing dataset from Kumar (chapter 8, page 519)
     *
     * @return
     */
    public static Dataset<? extends Instance> kumarData() {
        if (kumar == null) {
            kumar = new ArrayDataset<>(4, 2);
            kumar.attributeBuilder().create("x", BasicAttrType.NUMERIC);
            kumar.attributeBuilder().create("y", BasicAttrType.NUMERIC);
            kumar.builder().create(new double[]{0.40, 0.53}, "1");
            kumar.builder().create(new double[]{0.22, 0.38}, "2");
            kumar.builder().create(new double[]{0.35, 0.32}, "3");
            kumar.builder().create(new double[]{0.26, 0.19}, "4");
            kumar.builder().create(new double[]{0.08, 0.41}, "5");
            kumar.builder().create(new double[]{0.45, 0.30}, "6");
        }
        return kumar;
    }

    public static Clustering iris() {
        if (irisClusters == null) {
            irisDataset();
            /**
             * fictive clustering, create iris cluster based on class labels
             * (the dataset is sorted)
             */
            irisClusters = new ClusterList(3);
            Cluster a = new BaseCluster(50);
            a.setName("cluster 1");
            a.setAttributes(irisData.getAttributes());
            Cluster b = new BaseCluster(50);
            b.setName("cluster 2");
            b.setAttributes(irisData.getAttributes());
            Cluster c = new BaseCluster(50);
            c.setName("cluster 3");
            c.setAttributes(irisData.getAttributes());
            for (int i = 0; i < 50; i++) {
                a.add(irisData.instance(i));
                b.add(irisData.instance(i + 50));
                c.add(irisData.instance(i + 100));
            }

            irisClusters.add(a);
            irisClusters.add(b);
            irisClusters.add(c);
        }
        return irisClusters;
    }

    public static Dataset<? extends Instance> irisDataset() {
        if (irisData == null) {
            CommonFixture tf = new CommonFixture();
            irisData = new ArrayDataset(150, 4);
            ARFFHandler arff = new ARFFHandler();
            try {
                arff.load(tf.irisArff(), irisData, 4);
            } catch (FileNotFoundException | ParserError ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return irisData;
    }

    public static Clustering irisWrong() {
        if (irisWrong == null) {
            irisWrong = new ClusterList(3);
            Cluster a = new BaseCluster(50);
            a.setName("cluster 1");
            a.setAttributes(irisData.getAttributes());
            //add few instances to first cluster
            a.add(irisData.instance(0));
            a.add(irisData.instance(1));
            a.add(irisData.instance(2));
            a.add(irisData.instance(149));

            Cluster b = new BaseCluster(50);
            b.setName("cluster 2");
            b.setAttributes(irisData.getAttributes());
            b.add(irisData.instance(3));
            b.add(irisData.instance(4));
            b.add(irisData.instance(5));
            b.add(irisData.instance(6));
            Cluster c = new BaseCluster(50);
            c.setName("cluster 3");
            c.setAttributes(irisData.getAttributes());
            //rest goes to the last cluster
            for (int i = 7; i < 149; i++) {
                c.add(irisData.instance(i));
            }

            irisWrong.add(a);
            irisWrong.add(b);
            irisWrong.add(c);
        }
        return irisWrong;
    }

    /**
     * Pretty bad clustering result, one class contained in two clusters
     *
     * @return
     */
    public static Clustering irisWrong2() {
        if (irisWrong2 == null) {
            irisWrong2 = new ClusterList(3);
            Cluster a = new BaseCluster(50);
            a.setName("cluster 1");
            //will contain 30 elements of first class
            a.setAttributes(irisData.getAttributes());
            for (int i = 0; i < 30; i++) {
                a.add(irisData.instance(i));
            }

            Cluster b = new BaseCluster(50);
            b.setName("cluster 2");
            //will contain 20 elements of first class
            b.setAttributes(irisData.getAttributes());
            for (int i = 30; i < 50; i++) {
                b.add(irisData.instance(i));
            }
            Cluster c = new BaseCluster(50);
            c.setName("cluster 3");
            c.setAttributes(irisData.getAttributes());
            //the rest (100) goes to the last cluster
            for (int i = 50; i < 150; i++) {
                c.add(irisData.instance(i));
            }

            irisWrong2.add(a);
            irisWrong2.add(b);
            irisWrong2.add(c);
        }
        return irisWrong2;
    }

    public static Dataset<Instance> wine() {
        if (wine == null) {
            wine = new SampleDataset(27);
            wine.attributeBuilder().create("x", BasicAttrType.INTEGER);

            String klass = "cabernet";
            for (int i = 0; i < 13; i++) {
                wine.builder().create(new double[]{i}, klass);
            }

            String klass2 = "syrah";
            for (int i = 0; i < 9; i++) {
                wine.builder().create(new double[]{i * 3 + 13}, klass2);
            }

            String klass3 = "pinot";
            for (int i = 0; i < 5; i++) {
                wine.builder().create(new double[]{i * 4 + 50}, klass3);
            }
        }

        return wine;
    }

    public static Clustering wineCorrect() {
        if (simpleClustering == null) {

            simpleClustering = new ClusterList(3);
            Cluster a = new BaseCluster(12);
            a.setName("cabernet");
            a.attributeBuilder().create("x", BasicAttrType.INTEGER);
            Cluster b = new BaseCluster(9);
            b.setName("syrah");
            b.attributeBuilder().create("x", BasicAttrType.INTEGER);
            Cluster c = new BaseCluster(6);
            c.setName("pinot");
            c.attributeBuilder().create("x", BasicAttrType.INTEGER);

            Dataset<Instance> data = wine();
            for (int i = 0; i < 13; i++) {
                a.add(data.instance(i));
            }

            for (int i = 13; i < 22; i++) {
                b.add(data.instance(i));
            }

            for (int i = 22; i < 27; i++) {
                c.add(data.instance(i));
            }

            simpleClustering.add(a);
            simpleClustering.add(b);
            simpleClustering.add(c);
        }

        return simpleClustering;
    }

    /**
     * @see
     * http://alias-i.com/lingpipe/docs/api/com/aliasi/classify/PrecisionRecallEvaluation.html
     * @return
     */
    public static Clustering wineClustering() {

        if (simpleResponse == null) {
            simpleResponse = new ClusterList(3);
            Cluster a = new BaseCluster(13);
            a.setName("cluster A");
            a.setAttribute(0, a.attributeBuilder().create("x", BasicAttrType.INTEGER));
            Cluster b = new BaseCluster(9);
            b.setName("cluster B");
            b.setAttribute(0, b.attributeBuilder().create("x", BasicAttrType.INTEGER));

            Cluster c = new BaseCluster(5);
            c.setName("cluster C");
            c.setAttribute(0, c.attributeBuilder().create("x", BasicAttrType.INTEGER));

            Dataset<Instance> data = wine();
            System.out.println("dataset size " + data.size());
            // cabernet 9x -> a
            for (int i = 0; i < 9; i++) {
                a.add(data.instance(i));
            }

            // cabernet 2x => b
            b.add(data.instance(9));
            // cabernet 1x => c
            c.add(data.instance(10));
            b.add(data.instance(11));
            b.add(data.instance(12));

            // syrah 2x -> a
            for (int i = 13; i < 15; i++) {
                a.add(data.instance(i));
            }

            // syrah 2x -> c
            c.add(data.instance(15));

            // syrah 5x -> b
            for (int i = 16; i < 21; i++) {
                b.add(data.instance(i));
            }
            a.add(data.instance(21));
            // pinot 4x -> c
            for (int i = 22; i < 26; i++) {
                c.add(data.instance(i));
            }

            // pinot -> cabernet cluster
            b.add(data.instance(26));

            simpleResponse.add(a);
            simpleResponse.add(b);
            simpleResponse.add(c);
        }

        return simpleResponse;
    }

    public static Dataset<? extends Instance> schoolData() {
        if (school == null) {
            CsvLoader loader = new CsvLoader();
            school = new ArrayDataset(17, 4);
            school.setName("school data");
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

}
