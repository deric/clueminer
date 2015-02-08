package org.clueminer.eval.external;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class AbstractExternalEvalTest {

    private final AbstractExternalEvalImpl subject = new AbstractExternalEvalImpl();

    public AbstractExternalEvalTest() {
    }

    @Test
    public void testCompare() {
        //maximized. first one is better
        assertEquals(-1, subject.compare(2.0, 0.1));

        assertEquals(subject.compare(0.1, 2.0), -subject.compare(2.0, 0.1));

        //same
        assertEquals(0, subject.compare(0.0, 0.0));
        assertEquals(0, subject.compare(1e-9, 2e-9));

        //first one is worser
        assertEquals(1, subject.compare(0.0, 0.9));

    }

    public class AbstractExternalEvalImpl extends AbstractExternalEval {

        private static final long serialVersionUID = 1L;

        @Override
        public String getName() {
            return "test-cmp";
        }

        @Override
        public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset, Matrix proximity) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        /**
         * When true order is reversed (normal ordering in Java is ascending)
         *
         * @return
         */
        @Override
        public boolean isMaximized() {
            return true;
        }

        @Override
        public double score(Clustering<Cluster> c1, Clustering<Cluster> c2) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

}
