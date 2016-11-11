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
package org.clueminer.fixtures.clustering;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.factory.Clusterings;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.colors.RandomColorsGenerator;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.dataset.impl.SampleDataset;
import org.clueminer.fixtures.ClustFixture;
import org.clueminer.fixtures.MLearnFixture;
import org.clueminer.io.csv.CsvLoader;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class FakeClustering {

    private static Clustering<? extends Instance, Cluster<Instance>> irisClusters;
    private static Clustering<? extends Instance, Cluster<Instance>> irisWrong;
    private static Clustering<? extends Instance, Cluster<Instance>> irisWrong1;
    private static Clustering<Instance, Cluster<Instance>> irisWrong2;
    private static Clustering<Instance, Cluster<Instance>> irisWrong4;
    private static Clustering<Instance, Cluster<Instance>> irisWrong5;
    private static Clustering<Instance, Cluster<Instance>> simpleClustering;
    private static Clustering<Instance, Cluster<Instance>> simpleResponse;
    private static Clustering<Instance, Cluster<Instance>> ext100p2;
    private static Clustering<Instance, Cluster<Instance>> ext100p3;
    private static Clustering<Instance, Cluster<Instance>> int100p4;
    private static Clustering<Instance, Cluster<Instance>> spirals;
    private static Clustering<Instance, Cluster<Instance>> kumar;
    private static Dataset<Instance> wine;

    public static Clustering iris() {
        if (irisClusters == null) {
            ColorGenerator cg = new RandomColorsGenerator();
            Dataset<? extends Instance> irisData = FakeDatasets.irisDataset();
            /**
             * fictive clustering, create iris cluster based on class labels
             * (the dataset is sorted)
             */
            irisClusters = new ClusterList(3);
            Cluster a = new BaseCluster(50);
            a.setColor(cg.next());
            a.setName("cluster 1");
            a.setAttributes(irisData.getAttributes());
            Cluster b = new BaseCluster(50);
            b.setName("cluster 2");
            b.setAttributes(irisData.getAttributes());
            b.setColor(cg.next());
            Cluster c = new BaseCluster(50);
            c.setName("cluster 3");
            c.setColor(cg.next());
            c.setAttributes(irisData.getAttributes());
            for (int i = 0; i < 50; i++) {
                a.add(irisData.instance(i));
                b.add(irisData.instance(i + 50));
                c.add(irisData.instance(i + 100));
            }

            irisClusters.add(a);
            irisClusters.add(b);
            irisClusters.add(c);
            //add dataset to lookup
            irisClusters.lookupAdd(irisData);
        }
        return irisClusters;
    }

    public static Clustering irisWrong() {
        if (irisWrong == null) {
            Dataset<? extends Instance> irisData = FakeDatasets.irisDataset();
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
            //add dataset to lookup
            irisWrong.lookupAdd(irisData);
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
            Dataset<? extends Instance> irisData = FakeDatasets.irisDataset();
            irisWrong2 = new ClusterList(3);
            Cluster a = new BaseCluster(50);
            a.setName("cluster 1"); // Iris-setosa
            //will contain 30 elements of first class (Iris-setosa)
            a.setAttributes(irisData.getAttributes());
            for (int i = 0; i < 30; i++) {
                a.add(irisData.instance(i));
            }

            Cluster b = new BaseCluster(50);
            b.setName("cluster 2");
            //will contain 20 elements of Iris-setosa
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
            //add dataset to lookup
            irisWrong2.lookupAdd(irisData);
        }
        return irisWrong2;
    }

    public static Clustering irisWrong4() {
        if (irisWrong4 == null) {
            Dataset<? extends Instance> irisData = FakeDatasets.irisDataset();
            irisWrong4 = new ClusterList(4);
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
            for (int i = 50; i < 100; i++) {
                c.add(irisData.instance(i));
            }

            Cluster d = new BaseCluster(50);
            d.setName("cluster 4");
            d.setAttributes(irisData.getAttributes());
            //the rest (100) goes to the last cluster
            for (int i = 100; i < 150; i++) {
                d.add(irisData.instance(i));
            }

            irisWrong4.add(a);
            irisWrong4.add(b);
            irisWrong4.add(c);
            irisWrong4.add(d);
            //add dataset to lookup
            irisWrong4.lookupAdd(irisData);
        }
        return irisWrong4;
    }

    public static Clustering irisWrong5() {
        if (irisWrong5 == null) {
            Dataset<? extends Instance> irisData = FakeDatasets.irisDataset();
            irisWrong5 = new ClusterList(5);
            Cluster a = new BaseCluster(49);
            a.setName("cluster 1");
            a.setAttributes(irisData.getAttributes());
            for (int i = 0; i < 48; i++) {
                a.add(irisData.instance(i));
            }

            Cluster b = new BaseCluster(1);
            b.setName("cluster 2");
            b.setAttributes(irisData.getAttributes());
            b.add(irisData.instance(48));

            Cluster c = new BaseCluster(97);
            c.setName("cluster 3");
            c.setAttributes(irisData.getAttributes());
            for (int i = 49; i < 147; i++) {
                c.add(irisData.instance(i));
            }

            Cluster d = new BaseCluster(2);
            d.setName("cluster 4");
            d.setAttributes(irisData.getAttributes());
            for (int i = 147; i < 149; i++) {
                d.add(irisData.instance(i));
            }

            Cluster e = new BaseCluster(1);
            e.setName("cluster 5");
            e.setAttributes(irisData.getAttributes());
            e.add(irisData.instance(149));

            irisWrong5.add(a);
            irisWrong5.add(b);
            irisWrong5.add(c);
            irisWrong5.add(d);
            irisWrong5.add(e);
            //add dataset to lookup
            irisWrong5.lookupAdd(irisData);
        }
        return irisWrong5;
    }

    /**
     * Very bad clustering result, one item in singleton cluster, rest together
     *
     * @return
     */
    public static Clustering irisMostlyWrong() {
        if (irisWrong1 == null) {
            Dataset<? extends Instance> irisData = FakeDatasets.irisDataset();
            irisWrong1 = new ClusterList(2);
            Cluster a = new BaseCluster(1);
            a.setName("cluster 1"); // Iris-setosa
            //will contain single setosa item
            a.setAttributes(irisData.getAttributes());
            a.add(irisData.instance(0));

            Cluster b = new BaseCluster(149);
            b.setName("cluster 2");
            //will all the remaining items
            b.setAttributes(irisData.getAttributes());
            for (int i = 1; i < 150; i++) {
                b.add(irisData.instance(i));
            }

            irisWrong1.add(a);
            irisWrong1.add(b);
            //add dataset to lookup
            irisWrong1.lookupAdd(irisData);
        }
        return irisWrong1;
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
            a.attributeBuilder().create("x", BasicAttrType.INTEGER);
            Cluster b = new BaseCluster(9);
            b.setName("cluster B");
            b.attributeBuilder().create("x", BasicAttrType.INTEGER);

            Cluster c = new BaseCluster(5);
            c.setName("cluster C");
            c.attributeBuilder().create("x", BasicAttrType.INTEGER);

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
            //add dataset to lookup
            simpleResponse.lookupAdd(data);
        }

        return simpleResponse;
    }

    private static Dataset<? extends Instance> loadExtData(File fixture) {
        Dataset<? extends Instance> data;
        CsvLoader loader = new CsvLoader();
        data = new ArrayDataset(100, 1);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.setName("external 100");
        loader.setClassIndex(1);
        loader.setSeparator(',');
        loader.setHasHeader(false);

        try {
            loader.load(fixture, data);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    private static Dataset<? extends Instance> loadIntData(File fixture, int size) {
        Dataset<? extends Instance> data;
        CsvLoader loader = new CsvLoader();
        data = new ArrayDataset(size, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        data.setName("internal 100");
        ArrayList<Integer> skip = new ArrayList<>();
        skip.add(0);
        loader.setSkipIndex(skip);
        loader.setSeparator(',');
        loader.setHasHeader(false);

        try {
            loader.load(fixture, data);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return data;
    }

    private static Clustering convertExt2Clust(File f, int k) {
        Dataset<? extends Instance> data = loadExtData(f);

        Clustering clust = new ClusterList(k);
        for (int i = 0; i < k; i++) {
            clust.createCluster(i);
        }

        int cls;
        Cluster c;
        for (Instance inst : data) {
            cls = Integer.valueOf(inst.classValue().toString());
            c = clust.get(cls - 1);
            c.add(inst);
        }
        clust.lookupAdd(data);

        return clust;
    }

    /**
     * Simple dataset from R package clusterCrit containing 100 numbers assigned
     * to 2 clusters
     *
     * http://cran.r-project.org/web/packages/clusterCrit/index.html
     *
     * @return
     */
    public static Clustering ext100p2() {
        if (ext100p2 == null) {
            ClustFixture fixture = new ClustFixture();
            try {
                ext100p2 = convertExt2Clust(fixture.ext100p2(), 2);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return ext100p2;
    }

    /**
     * Simple dataset from R package clusterCrit containing 100 numbers assigned
     * to 3 clusters
     *
     * http://cran.r-project.org/web/packages/clusterCrit/index.html
     *
     * @return
     */
    public static Clustering ext100p3() {
        if (ext100p3 == null) {
            ClustFixture fixture = new ClustFixture();
            try {
                ext100p3 = convertExt2Clust(fixture.ext100p3(), 3);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return ext100p3;
    }

    public static Clustering int100p4() {
        if (int100p4 == null) {
            ClustFixture fixture = new ClustFixture();
            try {
                Dataset<? extends Instance> labels = loadExtData(fixture.int400p4assign());
                Dataset<? extends Instance> data = loadIntData(fixture.int400p4(), 400);

                int k = 4;
                Cluster c;
                int100p4 = new ClusterList(k);
                for (int i = 0; i < k; i++) {
                    c = int100p4.createCluster(i);
                    c.setAttributes(data.getAttributes());
                }

                int cls;

                //labels contain values from 1 to 4
                Iterator<? extends Instance> it = labels.iterator();
                for (Instance inst : data) {
                    cls = Integer.valueOf(it.next().classValue().toString());
                    c = int100p4.get(cls - 1);
                    c.add(inst);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return int100p4;
    }

    public static Clustering spirals() {
        if (spirals == null) {
            MLearnFixture fixture = new MLearnFixture();
            try {
                Dataset<Instance> data = new ArrayDataset<>(1000, 2);
                CsvLoader loader = new CsvLoader();
                data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
                data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
                data.setName("spirals");
                loader.setSeparator('\t');
                loader.setHasHeader(false);

                loader.load(fixture.spirals(), data);
                spirals = (Clustering<Instance, Cluster<Instance>>) Clusterings.newList(2);
                Cluster<Instance> c1 = spirals.createCluster(0, 500);
                Cluster<Instance> c2 = spirals.createCluster(1, 500);
                for (int i = 0; i < 500; i++) {
                    c1.add(data.get(i));
                    c2.add(data.get(500 + i));
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return spirals;
    }

    public static Clustering kumar() {
        if (kumar == null) {
            Dataset<? extends Instance> data = FakeDatasets.kumarData();
            kumar = (Clustering<Instance, Cluster<Instance>>) Clusterings.newList(2);
            kumar.lookupAdd(data);
            Cluster<Instance> c1 = kumar.createCluster(0, 3);
            c1.setAttributes(data.getAttributes());
            Cluster<Instance> c2 = kumar.createCluster(1, 3);
            c2.setAttributes(data.getAttributes());
            for (int i = 0; i < data.size() / 2; i++) {
                c1.add(data.get(i));
                c2.add(data.get(data.size() / 2 + i));
            }
        }
        return kumar;
    }

}
