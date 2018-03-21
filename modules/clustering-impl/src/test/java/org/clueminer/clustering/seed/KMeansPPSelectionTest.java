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
package org.clueminer.clustering.seed;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class KMeansPPSelectionTest<E extends Instance> {

    private KMeansPPSelection<E> subject;
    private static ExecutorService exec;

    public KMeansPPSelectionTest() {
        subject = new KMeansPPSelection();
    }

    @BeforeClass
    public static void setUpClass() {
        exec = Executors.newFixedThreadPool(3);
    }

    @AfterClass
    public static void tearDownClass() {
        exec.shutdown();
    }


    @Test
    public void testSelectPrototypes_Dataset_Props() {
        Dataset<E> dataset = (Dataset<E>) FakeClustering.irisDataset();
        Props params = new Props();
        params.putInt(KMeans.K, 3);

        E[] prototypes = subject.selectPrototypes(dataset, params);
        assertEquals(3, prototypes.length);
        assertNotNull(prototypes[0]);
        assertEquals(dataset.attributeCount(), prototypes[0].size());
        assertTrue(prototypes[0].getIndex() != prototypes[1].getIndex());
        for (int i = 0; i < prototypes.length; i++) {
            E prototype = prototypes[i];
            System.out.println(i + ": " + prototype.getIndex());
        }
    }

    @Test
    public void testSelectPrototypes_3args() {
        Dataset<E> dataset = (Dataset<E>) FakeClustering.irisDataset();
        Props params = new Props();
        params.putInt(KMeans.K, 4);
        params.putInt(KMeans.SEED, 133);

        exec = Executors.newFixedThreadPool(3);
        E[] prototypes = subject.selectPrototypes(dataset, params, exec);
        assertEquals(4, prototypes.length);
        assertEquals(dataset.attributeCount(), prototypes[0].size());
        assertTrue(prototypes[0].getIndex() != prototypes[1].getIndex());
    }

}
