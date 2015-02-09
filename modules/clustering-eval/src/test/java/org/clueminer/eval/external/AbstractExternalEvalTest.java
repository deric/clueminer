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

    @Test
    public void testCompareInfinite() {
        //maximized. first one is greater
        assertEquals(-1, subject.compare(2.0, Double.NaN));
        assertEquals(-1, subject.compare(2.0, Double.NEGATIVE_INFINITY));
        assertEquals(1, subject.compare(2.0, Double.POSITIVE_INFINITY));

        //same
        assertEquals(0, subject.compare(Double.NaN, Double.NaN));

        //this values might cause strange behaviour
        assertEquals(false, subject.isFinite(Double.NaN));
        assertEquals(false, subject.isFinite(Double.POSITIVE_INFINITY));
        assertEquals(false, subject.isFinite(Double.NEGATIVE_INFINITY));

        assertEquals(0, subject.compare(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        assertEquals(0, subject.compare(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));

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
