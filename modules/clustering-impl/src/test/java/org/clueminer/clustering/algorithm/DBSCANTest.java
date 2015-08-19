/*
 * Copyright (C) 2011-2015 clueminer.org
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

import org.clueminer.attributes.BasicAttrType;
import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DBSCANTest {

    private static DBSCAN<? extends Instance> subject;

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

    @Test
    public void testSchoolData() {
        Dataset dataset = FakeClustering.schoolData();
        Props pref = new Props();

        pref.putDouble(DBSCAN.MIN_PTS, 2);
        pref.putDouble(DBSCAN.RADIUS, 20);
        Clustering<? extends Cluster> c = subject.cluster(dataset, pref);
        assertEquals(2, c.size());
    }

    @Test
    public void testBasicData() {
        Dataset dataset = basicData();
        Props pref = new Props();
        assertEquals(2, dataset.attributeCount());

        System.out.println("data size: " + dataset.size());

        pref.putDouble(DBSCAN.MIN_PTS, 2);
        pref.putDouble(DBSCAN.RADIUS, 5);
        Clustering<? extends Cluster> c = subject.cluster(dataset, pref);
        assertEquals(4, c.size());
        System.out.println(c.toString());
    }

}
