package org.clueminer.approximation;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;
import org.apache.commons.math3.analysis.integration.RombergIntegrator;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;
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
public class ChebyshevFunctionTest {

    public ChebyshevFunctionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
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
     * Test of chebyshev method, of class ChebyshevPolynomial.
     */
    @Test
    public void testChebyshev() {
    }

    /**
     * Test of xTimes method, of class ChebyshevPolynomial.
     */
    @Test
    public void testXTimes() {
    }

    /**
     * Test of arrayShiftLeft method, of class ChebyshevPolynomial.
     */
    @Test
    public void testArrayShiftLeft() {
    }

    /**
     * Test of arrayMinus method, of class ChebyshevPolynomial.
     */
    @Test
    public void testArrayMinus() {
    }

    /**
     * Test of value method, of class ChebyshevPolynomial.
     */
    @Test
    public void testValueAt() {
        ChebyshevPolynomial t0 = new ChebyshevPolynomial(0);
        System.out.println(t0.toString());
        System.out.println("T0 integral: " + integrate(t0));

        int maxEval = 4000;
        //BaseAbstractUnivariateIntegrator sims = new IterativeLegendreGaussIntegrator(maxEval, 1.0e-5, 1.0e-10);
        //BaseAbstractUnivariateIntegrator sims = new RombergIntegrator();
        //BaseAbstractUnivariateIntegrator sims = new TrapezoidIntegrator();
        BaseAbstractUnivariateIntegrator sims = new SimpsonIntegrator(1e-4, 1e-6, 10, 64);
        double lower = -1.0;
        double upper = 0.9999;
        double sum = sims.integrate(maxEval, t0, lower, upper);
        ScalarProduct prod;
        System.out.println("sims = " + sum);
        UnivariateFunction weight = new ChebyshevWeightingFactor();

        ChebyshevPolynomial t1 = new ChebyshevPolynomial(1);
        ChebyshevPolynomial t2 = new ChebyshevPolynomial(2);
        ChebyshevPolynomial t3 = new ChebyshevPolynomial(3);
        System.out.println(t1.toString());
        prod = new ScalarProduct(weight, t1, t1);
        // sum = sims.integrate(maxEval, prod, lower, upper);
        //  System.out.println("T1-T1 integral: " + sum);
        System.out.println("PI/2 = "+ (Math.PI / 2));
        System.out.println("T1-T1= " + integrate(prod));

        prod = new ScalarProduct(weight, t2, t2);
        System.out.println("T2-T2= " + integrate(prod));
        
        prod = new ScalarProduct(weight, t3, t3);
        System.out.println("T3-T3= " + integrate(prod));
        //sum = sims.integrate(maxEval, prod, lower, upper);
        //System.out.println("T3-T3 integral: " + sum);
        /*       
         System.out.println(t2.toString());
         System.out.println("T2-T2 integral: " + integrate(t2, t2));

         
         System.out.println(t3.toString());
         System.out.println("T3-T3 integral: " + integrate(t3, t3));


         System.out.println("T0-T1 integral: " + integrate(t0, t1));
         System.out.println("T0-T2 integral: " + integrate(t0, t2));
         System.out.println("T1-T2 integral: " + integrate(t1, t2));
         System.out.println("T1-T3 integral: " + integrate(t1, t3));*/

    }

    private double integrate(UnivariateFunction f1, UnivariateFunction f2) {
        double x = -1.0;
        double step = 0.005;
        double sum = 0.0;
        while (x < 1.0) {
            //we compute a rectangle below function
            sum += f1.value(x) * f2.value(x) * step;
            x += step;
            //System.out.println(x+" = "+t1.value(x));
        }
        return sum;
    }

    private double integrate(UnivariateFunction f1) {
        double x = -1.0;
        double step = 0.005;
        double sum = 0.0;
        int cnt = 0;
        while (x < 1.0) {
            x += step;
            //we compute a rectangle below function            
            sum += f1.value(x) * step;
            cnt++;
            //  System.out.println(x+" = "+f1.value(x));
        }
        System.out.println("cnt = " + cnt);
        return sum;
    }
}