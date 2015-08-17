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

    private class DummyAlgorithm<T extends Instance> extends AbstractClusteringAlgorithm<T> {

        @Param(name = "k")
        int k = 5;

        @Param(name = "x")
        int x = 5;

        @Override
        public String getName() {
            return "dummy";
        }

        @Override
        public Clustering<? extends Cluster<? super T>> cluster(Dataset<T> dataset, Props props) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

}
