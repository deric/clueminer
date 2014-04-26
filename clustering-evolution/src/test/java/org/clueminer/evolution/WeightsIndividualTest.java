package org.clueminer.evolution;

import org.clueminer.clustering.api.evolution.Individual;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.eval.external.JaccardIndex;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class WeightsIndividualTest {

    private Evolution evolution;
    private WeightsIndividual one;
    private Individual two;
    private static double delta = 1e-9;

    public WeightsIndividualTest() {
        Dataset dataset = FakeClustering.irisDataset();
        evolution = new AttrEvolution(dataset, 5);
        evolution.setEvaluator(new JaccardIndex());
        evolution.setAlgorithm(new KMeans(3, 100, new EuclideanDistance()));
        one = new WeightsIndividual(evolution);
        two = new WeightsIndividual(evolution);
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
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
        Individual<WeightsIndividual> other = one.deepCopy();
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
        Individual<WeightsIndividual> other = one.deepCopy();
        assertEquals(one.compareTo(other), 0);
        one.setFitness(one.getFitness() + 10);
        assertEquals(one.compareTo(other), 1);
        one.setFitness(one.getFitness() - 15);
        assertEquals(one.compareTo(other), -1);
    }
}
