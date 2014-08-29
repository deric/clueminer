package org.clueminer.approximation;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.analysis.integration.RombergIntegrator;
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
public class LegendrePolynomialTest {

    private static double delta = 1e-9;
    private static double delta5 = 1e-5;

    public LegendrePolynomialTest() {
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

    @Test
    public void testGenerate() {
        LegendrePolynomial p0 = new LegendrePolynomial(0);
        assertEquals(1.0, p0.getCoefficients()[0], delta);
        System.out.println("p0: " + p0.toString());
        LegendrePolynomial p1 = new LegendrePolynomial(1);
        assertEquals(1.0, p1.getCoefficients()[1], delta);
        System.out.println("p1: " + p1.toString());
        LegendrePolynomial p2 = new LegendrePolynomial(2);
        assertEquals(1.5, p2.getCoefficients()[2], delta);
        assertEquals(-0.5, p2.getCoefficients()[0], delta);
        System.out.println("p2: " + p2.toString());
        LegendrePolynomial p3 = new LegendrePolynomial(3);
        assertEquals(2.5, p3.getCoefficients()[3], delta);
        assertEquals(-1.5, p3.getCoefficients()[1], delta);
        System.out.println("p3: " + p3.toString());
        LegendrePolynomial p4 = new LegendrePolynomial(4);
        System.out.println("p4: " + p4.toString());
        assertEquals(4.375, p4.getCoefficients()[4], delta);
        assertEquals(-3.75, p4.getCoefficients()[2], delta);
    }

    @Test
    public void testIntegration() {
        //just linear function
        LegendrePolynomial p0 = new LegendrePolynomial(0);
        double min = -1.0;
        double max = 1.0;
        //simple integration
        double sum = integrate(p0, min, max);
        //it isn't very accurate
        System.out.println("integral t0 = " + sum);
        //profi integrator:
        BaseAbstractUnivariateIntegrator trape = new TrapezoidIntegrator();
        // p0 is constant function
        sum = trape.integrate(100, p0, min, max);
        System.out.println("integral t0 = " + sum + " (trapezoid)");
        assertEquals(2.0, sum, delta);
        LegendrePolynomial p1 = new LegendrePolynomial(1);
        sum = trape.integrate(100, p1, min, max);
        System.out.println("integral t1 = " + sum + " (trapezoid)");
        assertEquals(0.0, sum, delta);
        BaseAbstractUnivariateIntegrator romb = new RombergIntegrator();
        LegendrePolynomial p2 = new LegendrePolynomial(2);
        sum = romb.integrate(1000, p2, min, max);
        assertEquals(0.0, sum, delta);
        System.out.println("integral t2 = " + sum + " (romb)");
        LegendrePolynomial p3 = new LegendrePolynomial(3);
        sum = romb.integrate(1000, p3, min, max);
        System.out.println("integral t3 = " + sum + " (romb)");
    }

    /**
     * Orthogonality/Orthonormality test
     *
     * @see http://mathworld.wolfram.com/OrthogonalPolynomials.html
     */
    @Test
    public void testOrthogonality() {
        double min = -1.0;
        double max = 1.0;
        double sum;
        int maxEval = 1000;

        LegendrePolynomial p2 = new LegendrePolynomial(2);
        LegendrePolynomial p4 = new LegendrePolynomial(4);

        UnivariateFunction prod = new UnivariateProduct(p2, p4);

        BaseAbstractUnivariateIntegrator romb = new RombergIntegrator();
        sum = romb.integrate(maxEval, prod, min, max);
        //degrees are different, integral should be 0
        assertEquals(0.0, sum, delta);

        for (int i = 0; i < 10; i++) {
            LegendrePolynomial p = new LegendrePolynomial(i);

            prod = new UnivariateProduct(p, p);
            sum = romb.integrate(maxEval, prod, min, max);
            //degrees are the same, integral should be equal to constant factor
            assertEquals(sum, p.constFactor(), delta);
        }
    }

    private double integralTime(double min, double max, double s) {
        return 2.0 / (max - min) * s - 1;
    }

    @Test
    public void testShift() {
        /**
         * scale polynomials to different intervals while orthogonal properties
         * should remain the same
         */
        shift(0.0, 2.0);
        //shift(0.0, 4.0);
    }

    private void shift(double min, double max) {
        double sum;
        UnivariateFunction prod;

        for (int i = 0; i < 10; i++) {
            LegendrePolynomial p = new LegendrePolynomial(i);

            prod = new UnivariateProduct(p, p);
            sum = integrate(prod, min, max);
            System.out.println("p(" + i + ") = " + sum + ", fact = " + p.constFactor() + " delta = " + (sum - p.constFactor()));
            //degrees are the same, integral should be equal to constant factor
            //not very precise method, we need just approximation
            assertEquals(sum, p.constFactor(), delta);
        }
    }

    /**
     * Test of value method, of class LegendrePolynomial.
     */
    @Test
    public void testValue() {
    }

    /**
     * Test of toString method, of class LegendrePolynomial.
     */
    @Test
    public void testToString() {
    }

    /**
     * Test of binomial method, of class LegendrePolynomial.
     */
    @Test
    public void testBinomial() {
        LegendrePolynomial p = new LegendrePolynomial(0);
        assertEquals(0.5, p.binomial(0.5, 1.0), delta);
        assertEquals(2.0, p.binomial(2.0, 1.0), delta);
    }

    /**
     * Shifts time value in order to scale orthogonality to any interval
     *
     * @param f1
     * @param xmin
     * @param xmax
     * @return
     */
    private double integrate(UnivariateFunction f1, double xmin, double xmax) {
        double x = xmin;
        double step = 0.000005;
        double sum = 0.0;
        int cnt = 0;
        while (x < xmax) {
            x += step;
            //we compute a rectangle below function            
            sum += f1.value(integralTime(xmin, xmax, x)) * step;            
            cnt++;
            //System.out.println(x+" = "+f1.value(x));
        }
        System.out.println("cnt = " + cnt);
        return sum;
    }

    /**
     * Test of getCoefficients method, of class LegendrePolynomial.
     */
    @Test
    public void testGetCoefficients() {
    }

    /**
     * Test of constFactor method, of class LegendrePolynomial.
     */
    @Test
    public void testConstFactor() {
        LegendrePolynomial p2 = new LegendrePolynomial(2);
        assertEquals(0.4, p2.constFactor(), delta);
        System.out.println("factor " + p2.constFactor());
        LegendrePolynomial p3 = new LegendrePolynomial(3);
        System.out.println("factor " + p3.constFactor());
    }
}