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
package org.clueminer.evolution.attr;

import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.external.JaccardIndex;
import org.clueminer.evolution.api.EvolutionSO;
import org.clueminer.evolution.api.Individual;
import org.clueminer.fixtures.clustering.FakeDatasets;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class WeightsIndividualTest {

    private EvolutionSO evolution;
    private WeightsIndividual one;
    private Individual two;
    private static final double DELTA = 1e-9;

    public WeightsIndividualTest() {
        Dataset<? extends Instance> dataset = FakeDatasets.irisDataset();
        evolution = new AttrEvolution(dataset, 5);
        evolution.setEvaluator(new JaccardIndex());
        evolution.setAlgorithm(new KMeans());
        one = new WeightsIndividual(evolution);
        two = new WeightsIndividual(evolution);
    }

    /**
     * Test of getClustering method, of class WeightsIndividual.
     */
    @Test
    public void testGetClustering() {
    }

    /**
     * Test of countFitness method, of class WeightsIndividual.
     */
    @Test
    public void testCountFitness() {
    }

    /**
     * Test of getFitness method, of class WeightsIndividual.
     */
    @Test
    public void testGetFitness() {
        assertNotNull(one.getFitness());
        assertTrue(one.getFitness() > 0);
    }

    /**
     * Test of mutate method, of class WeightsIndividual.
     */
    @Test
    public void testMutate() {
    }

    /**
     * Test of cross method, of class WeightsIndividual.
     */
    @Test
    public void testCross() {
    }

    /**
     * Test of deepCopy method, of class WeightsIndividual.
     */
    @Test
    public void testDeepCopy() {
        Individual<WeightsIndividual, Instance, Cluster<Instance>> other = one.deepCopy();
        assertNotNull(other.getFitness());
        assertEquals(one.getFitness(), other.getFitness(), DELTA);
    }

    @Test
    public void testCompare() {
        Individual<WeightsIndividual, Instance, Cluster<Instance>> other = one.deepCopy();
        assertEquals(one.compareTo(other), 0);
        one.setFitness(one.getFitness() + 10);
        assertEquals(one.compareTo(other), 1);
        one.setFitness(one.getFitness() - 15);
        assertEquals(one.compareTo(other), -1);
    }
}
