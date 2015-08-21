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
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class WeightsIndividualTest {

    private EvolutionSO evolution;
    private WeightsIndividual one;
    private Individual two;
    private static final double delta = 1e-9;

    public WeightsIndividualTest() {
        Dataset<? extends Instance> dataset = FakeDatasets.irisDataset();
        evolution = new AttrEvolution(dataset, 5);
        evolution.setEvaluator(new JaccardIndex());
        evolution.setAlgorithm(new KMeans());
        one = new WeightsIndividual(evolution);
        two = new WeightsIndividual(evolution);
    }

    @Before
    public void setUp() {
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
        assertEquals(one.getFitness(), other.getFitness(), delta);
    }

    /**
     * Test of isCompatible method, of class WeightsIndividual.
     */
    @Test
    public void testIsCompatible() {
    }

    /**
     * Test of duplicate method, of class WeightsIndividual.
     */
    @Test
    public void testDuplicate() {
    }

    /**
     * Test of toString method, of class WeightsIndividual.
     */
    @Test
    public void testToString() {
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
