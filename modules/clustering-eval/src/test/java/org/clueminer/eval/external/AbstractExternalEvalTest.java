package org.clueminer.eval.external;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class AbstractExternalEvalTest {

    private final AbstractExternalEvalImpl<Instance, Cluster<Instance>> subject = new AbstractExternalEvalImpl<>();

    public AbstractExternalEvalTest() {
    }

    @Test
    public void testCompare() {
        //maximized. first one is better
        assertEquals(1, subject.compare(2.0, 0.1));

        assertEquals(subject.compare(0.1, 2.0), -subject.compare(2.0, 0.1));

        //same
        assertEquals(0, subject.compare(0.0, 0.0));
        assertEquals(0, subject.compare(1e-9, 2e-9));

        //first one is worser
        assertEquals(-1, subject.compare(0.0, 0.9));
    }

    @Test
    public void testCompareInfinite() {
        //maximized. first one is greater
        assertEquals(1, subject.compare(2.0, Double.NaN));
        assertEquals(1, subject.compare(2.0, Double.NEGATIVE_INFINITY));
        //infinity is typically not consiedered as desired value
        // usually a result of incorrect operation e.g. log(-1)
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
        assertEquals(-1, subject.compare(0.0, 0.9));
    }

    public class AbstractExternalEvalImpl<E extends Instance, C extends Cluster<E>> extends AbstractExternalEval<E, C> {

        private static final long serialVersionUID = 1L;

        @Override
        public String getName() {
            return "test-cmp";
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
        public double getMin() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public double getMax() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public double score(Clustering<E, C> clusters) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public double score(Clustering<E, C> clusters, Props params) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public double score(Clustering<E, C> clusters, Matrix proximity, Props params) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public double score(Clustering<E, C> c1, Clustering<E, C> c2, Props params) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
