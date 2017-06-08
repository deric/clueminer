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
package org.clueminer.clustering.seed;

import java.security.SecureRandom;
import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 * @param <E>
 */
public class RandomMedoidsSelectionTest<E extends Instance> {

    private RandomMedoidsSelection<E> subject;
    private Dataset<E> dataset;
    private static Props params;

    public RandomMedoidsSelectionTest() {
        params = new Props();
    }

    @Before
    public void setUp() {
        subject = new RandomMedoidsSelection();
        dataset = (Dataset<E>) FakeClustering.irisDataset();
    }

    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    @Test
    public void testSetRandom() {
        subject.setRandom(new SecureRandom());
        int k = 5;
        params.putInt(KMeans.K, k);
        E[] medoids = subject.selectPrototypes(dataset, params);
        assertEquals(k, medoids.length);
        for (int i = 0; i < medoids.length; i++) {
            assertEquals(true, medoids[i].getIndex() < dataset.size());
            assertEquals(true, medoids[i].getIndex() >= 0);
            assertEquals(true, dataset.hasIndex(medoids[i].getIndex()));
        }
    }

    @Test
    public void testSelectIntIndices() {
        int k = 15;
        params.putInt(KMeans.K, k);
        E[] medoids = subject.selectPrototypes(dataset, params);
        assertEquals(k, medoids.length);
        for (int i = 0; i < medoids.length; i++) {
            assertEquals(true, medoids[i].getIndex() < dataset.size());
            assertEquals(true, medoids[i].getIndex() >= 0);
            assertEquals(true, dataset.hasIndex(medoids[i].getIndex()));
        }
    }

    /**
     * If exception is not thrown, we would end in an infinite loop
     */
    @Test(expected = RuntimeException.class)
    public void testBigK() {
        int k = dataset.size() + 1;
        params.putInt(KMeans.K, k);
        E[] medoids = subject.selectPrototypes(dataset, params);
        assertEquals(k, medoids.length);
        for (int i = 0; i < medoids.length; i++) {
            assertEquals(true, medoids[i].getIndex() < dataset.size());
            assertEquals(true, medoids[i].getIndex() >= 0);
        }
    }

}
