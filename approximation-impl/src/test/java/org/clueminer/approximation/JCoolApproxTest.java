package org.clueminer.approximation;

import java.util.HashMap;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.plugin.TimeseriesDataset;
import org.clueminer.dataset.row.TimeRow;
import org.clueminer.types.TimePoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class JCoolApproxTest {

    private static JCoolApprox subject = new JCoolApprox();

    public JCoolApproxTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class JCoolApprox.
     */
    @Test
    public void testGetName() {
    }

    /**
     * Test of estimate method, of class JCoolApprox.
     */
    @Test
    public void testEstimate() {
        //linear data
        int size = 7;

        TimeseriesDataset<ContinuousInstance> dataset = new TimeseriesDataset<ContinuousInstance>(5);
        ContinuousInstance inst = new TimeRow(Double.class, 7);
        TimePoint tp[] = new TimePointAttribute[size];
        for (int i = 0; i < tp.length; i++) {
            tp[i] = new TimePointAttribute(i, i + 100, Math.pow(i, 2));
            inst.put(i);
        }
        dataset.setTimePoints(tp);

        dataset.add(inst);
        HashMap<String, Double> coeff = new HashMap<String, Double>();
        subject.estimate(new double[]{1, 2, 3, 4, 5, 6, 7}, inst, coeff);
    }

    /**
     * Test of getParamNames method, of class JCoolApprox.
     */
    @Test
    public void testGetParamNames() {
    }

    /**
     * Test of getFunctionValue method, of class JCoolApprox.
     */
    @Test
    public void testGetFunctionValue() {
    }

    /**
     * Test of getNumCoefficients method, of class JCoolApprox.
     */
    @Test
    public void testGetNumCoefficients() {
    }

}
