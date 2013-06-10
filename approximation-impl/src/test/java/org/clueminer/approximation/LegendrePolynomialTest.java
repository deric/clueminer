package org.clueminer.approximation;

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
}