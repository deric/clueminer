package org.clueminer.evolution;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.algorithm.HCL;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.evaluation.BICScore;
import org.clueminer.evaluation.external.JaccardIndex;
import org.clueminer.exception.UnsupportedAttributeType;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tombart
 */
public class EvolutionTest {

    private static CommonFixture tf = new CommonFixture();
    private static Dataset<Instance> irisDataset;
    private Evolution test;

    public EvolutionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws FileNotFoundException, UnsupportedAttributeType, IOException {
        ARFFHandler arff = new ARFFHandler();
        irisDataset = new SampleDataset();
        arff.load(tf.irisArff(), irisDataset, 4);        
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
     * Test of attributesCount method, of class Evolution.
     */
    @Test
    public void testAttributesCount() {
    }

    /**
     * Test of getDataset method, of class Evolution.
     */
    @Test
    public void testGetDataset() {
    }

    /**
     * Test of run method, of class Evolution.
     */
    @Test
    public void testRun() {
        test = new Evolution(irisDataset, 50);
        test.setAlgorithm(new KMeans(3, 100, new EuclideanDistance()));
        test.setEvaluator(new BICScore());
        //test.setEvaluator(new JaccardIndex());
        test.run();
        
    }

    /**
     * Test of getMutationProbability method, of class Evolution.
     */
    @Test
    public void testGetMutationProbability() {
    }

    /**
     * Test of setMutationProbability method, of class Evolution.
     */
    @Test
    public void testSetMutationProbability() {
    }

    /**
     * Test of getCrossoverProbability method, of class Evolution.
     */
    @Test
    public void testGetCrossoverProbability() {
    }

    /**
     * Test of setCrossoverProbability method, of class Evolution.
     */
    @Test
    public void testSetCrossoverProbability() {
    }

    /**
     * Test of getAlgorithm method, of class Evolution.
     */
    @Test
    public void testGetAlgorithm() {
    }

    /**
     * Test of setAlgorithm method, of class Evolution.
     */
    @Test
    public void testSetAlgorithm() {
    }
}