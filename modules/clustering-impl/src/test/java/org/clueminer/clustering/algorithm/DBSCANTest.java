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
package org.clueminer.clustering.algorithm;

import java.util.ArrayList;
import java.util.List;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.knn.LinearRNN;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.neighbor.RNNSearch;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DBSCANTest {

    private static DBSCAN<Instance, Cluster<Instance>> subject;

    public DBSCANTest() {
        subject = new DBSCAN<>();
    }

    private Dataset<? extends Instance> basicData() {
        Dataset<? extends Instance> data = new ArrayDataset(10, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        InstanceBuilder b = data.builder();

        b.create(new double[]{1, 1}, "1");
        b.create(new double[]{0, 1}, "1");
        b.create(new double[]{1, 0}, "1");

        b.create(new double[]{10, 10}, "2");
        b.create(new double[]{10, 13}, "2");
        b.create(new double[]{13, 13}, "2");

        b.create(new double[]{49, 49}, "4");

        b.create(new double[]{30, 30}, "3");
        b.create(new double[]{30, 33}, "3");
        b.create(new double[]{33, 33}, "3");
        b.create(new double[]{36, 36}, "3");

        return data;
    }

    private Dataset<? extends Instance> smallData() {
        Dataset<? extends Instance> data = new ArrayDataset(10, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        InstanceBuilder b = data.builder();

        b.create(new double[]{1, 1}, "1");
        b.create(new double[]{2, 2}, "1");
        b.create(new double[]{3, 3}, "1");

        b.create(new double[]{50, 50}, "2");
        b.create(new double[]{51, 51}, "2");

        return data;
    }

    @Test
    public void testSchoolData() {
        Dataset dataset = FakeClustering.schoolData();
        Props pref = new Props();

        pref.putInt(DBSCAN.MIN_PTS, 2);
        pref.putDouble(DBSCAN.EPS, 20);
        Clustering<Instance, Cluster<Instance>> c = subject.cluster(dataset, pref);
        assertEquals(2, c.size());
    }

    @Test
    public void testBasicData() {
        Dataset dataset = basicData();
        Props pref = new Props();
        assertEquals(2, dataset.attributeCount());

        System.out.println("data size: " + dataset.size());

        pref.putInt(DBSCAN.MIN_PTS, 2);
        pref.putDouble(DBSCAN.EPS, 5);
        Clustering<Instance, Cluster<Instance>> c = subject.cluster(dataset, pref);
        assertEquals(4, c.size());
        System.out.println(c.toString());
    }

    @Test
    public void testSmallData() {
        Dataset dataset = smallData();
        double eps = 1.0;
        RNNSearch<Instance> nns = new LinearRNN<>(dataset);
        List<Neighbor<Instance>> seeds = new ArrayList<>();

        nns.range(dataset.get(1), eps, seeds);

        //closest neighbor is in distance of sqrt(2)
        assertEquals(0, seeds.size());

        //should include just itself
        nns.setIdenticalExcluded(false);
        nns.range(dataset.get(1), eps, seeds);
        assertEquals(1, seeds.size());

        nns.setIdenticalExcluded(true);
        seeds = new ArrayList<>();
        eps = 2.0;
        nns.range(dataset.get(1), eps, seeds);
        assertEquals(2, seeds.size());
    }

}
