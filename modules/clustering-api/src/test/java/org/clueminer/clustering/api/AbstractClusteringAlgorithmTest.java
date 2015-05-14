package org.clueminer.clustering.api;

import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class AbstractClusteringAlgorithmTest {

    public AbstractClusteringAlgorithmTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetDistanceFunction() {
    }

    @Test
    public void testSetDistanceFunction() {
    }

    @Test
    public void testGetColorGenerator() {
    }

    @Test
    public void testSetColorGenerator() {
    }

    @Test
    public void testSetProgressHandle() {
    }

    @Test
    public void testGetParameters() {
        DummyAlgorithm alg = new DummyAlgorithm();
        alg.getParameters();
    }

    private class DummyAlgorithm extends AbstractClusteringAlgorithm {

        @Param(name = "k")
        int k = 5;

        @Param(name = "x")
        int x = 5;

        @Override
        public String getName() {
            return "dummy";
        }

        @Override
        public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset, Props props) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

}
