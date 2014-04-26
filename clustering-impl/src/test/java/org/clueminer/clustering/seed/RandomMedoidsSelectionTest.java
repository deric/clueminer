package org.clueminer.clustering.seed;

import java.security.SecureRandom;
import org.clueminer.cluster.FakeClustering;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
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

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
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
