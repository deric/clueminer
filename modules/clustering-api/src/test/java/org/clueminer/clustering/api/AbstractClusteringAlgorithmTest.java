package org.clueminer.clustering.api;

import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class AbstractClusteringAlgorithmTest {

    public AbstractClusteringAlgorithmTest() {
    }

    @Test
    public void testGetParameters() {
        DummyAlgorithm alg = new DummyAlgorithm();
        alg.getParameters();
    }

    private class DummyAlgorithm<E extends Instance, C extends Cluster<E>> extends Algorithm<E, C> {

        @Param(name = "k")
        int k = 5;

        @Param(name = "x")
        int x = 5;

        @Override
        public String getName() {
            return "dummy";
        }

        @Override
        public Clustering<E, C> cluster(Dataset<E> dataset, Props props) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

}
