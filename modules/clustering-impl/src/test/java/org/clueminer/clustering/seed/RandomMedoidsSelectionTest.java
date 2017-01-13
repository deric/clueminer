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
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class RandomMedoidsSelectionTest {

    private static RandomMedoidsSelection subject;
    private static Dataset<? extends Instance> dataset;

    public RandomMedoidsSelectionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        subject = new RandomMedoidsSelection();
        dataset = FakeClustering.irisDataset();
    }

    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    @Test
    public void testSetRandom() {
        subject.setRandom(new SecureRandom());
        int k = 5;
        int[] medoids = subject.selectIntIndices(dataset, k);
        assertEquals(k, medoids.length);
        for (int i = 0; i < medoids.length; i++) {
            assertEquals(true, medoids[i] < dataset.size());
            assertEquals(true, medoids[i] >= 0);
            assertEquals(true, dataset.hasIndex(medoids[i]));
        }
    }

    @Test
    public void testSelectIntIndices() {
        int k = 15;
        int[] medoids = subject.selectIntIndices(dataset, k);
        assertEquals(k, medoids.length);
        for (int i = 0; i < medoids.length; i++) {
            assertEquals(true, medoids[i] < dataset.size());
            assertEquals(true, medoids[i] >= 0);
            assertEquals(true, dataset.hasIndex(medoids[i]));
        }
    }

    /**
     * If exception is not thrown, we would end in an infinite loop
     */
    @Test(expected = RuntimeException.class)
    public void testBigK() {
        int k = dataset.size() + 1;
        int[] medoids = subject.selectIntIndices(dataset, k);
        assertEquals(k, medoids.length);
        for (int i = 0; i < medoids.length; i++) {
            assertEquals(true, medoids[i] < dataset.size());
            assertEquals(true, medoids[i] >= 0);
        }
    }

}
