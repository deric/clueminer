package org.clueminer.colors;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ColorSchemeTest {

    private static double delta = 1e-9;

    public ColorSchemeTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getMax method, of class ColorScheme.
     */
    @org.junit.Test
    public void testGetMax() {
    }

    /**
     * Test of getMid method, of class ColorScheme.
     */
    @org.junit.Test
    public void testGetMid() {
    }

    /**
     * Test of getMin method, of class ColorScheme.
     */
    @org.junit.Test
    public void testGetMin() {
    }

    /**
     * Test of getColor method, of class ColorScheme.
     */
    @org.junit.Test
    public void testGetColor() {
    }

    /**
     * Test of updateColors method, of class ColorScheme.
     */
    @org.junit.Test
    public void testUpdateColors() {
    }

    /**
     * Test of setRange method, of class ColorScheme.
     */
    @org.junit.Test
    public void testSetRange() {
    }

    /**
     * Test of countMedian method, of class ColorScheme.
     *
     */
    @Test
    public void testCountMedian() {
        ColorScheme cs = new ColorScheme();
        assertEquals(0.0, cs.countMedian(-10, 10), delta);

    }

    /**
     * Test of isUseDoubleGradient method, of class ColorScheme.
     */
    @org.junit.Test
    public void testIsUseDoubleGradient() {
    }

    /**
     * Test of setUseDoubleGradient method, of class ColorScheme.
     */
    @org.junit.Test
    public void testSetUseDoubleGradient() {
    }

}
