package org.clueminer.approximation;

import java.util.HashMap;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.row.TimeRow;
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
        ContinuousInstance inst = new TimeRow(Double.class, 7);
        inst.put(1.0);
        inst.put(2.0);
        inst.put(3.0);
        inst.put(4.0);
        inst.put(5.0);
        inst.put(6.0);
        inst.put(7.0);

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
