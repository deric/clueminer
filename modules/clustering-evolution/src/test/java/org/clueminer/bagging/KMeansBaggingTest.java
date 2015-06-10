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
package org.clueminer.bagging;

import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class KMeansBaggingTest {

    private final KMeansBagging subject;

    public KMeansBaggingTest() {
        subject = new KMeansBagging();
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    @Test
    public void testCluster() {
        Dataset<? extends Instance> data = FakeDatasets.schoolData();
        Props params = new Props();
        params.putInt("k", 5);
        params.putInt(KMeansBagging.BAGGING, 5);
        params.put(KMeansBagging.CONSENSUS, CoAssociationReduce.name);
        Clustering c = subject.cluster(data, params);

    }

    @Test
    public void testPartition() {
        Dataset<? extends Instance> data = FakeDatasets.schoolData();
        Props def = new Props();
        def.putInt("max_k", data.size());
        subject.setDefaultProps(def);
        Props params = new Props();
        params.putInt("k", 3);
        params.put(KMeansBagging.INIT_METHOD, "MO");
        params.putInt(KMeansBagging.BAGGING, 3);
        params.put(KMeansBagging.FIXED_K, true);
        Clustering c = subject.cluster(data, params);
        System.out.println("num clusters: " + c.size());
    }

}
