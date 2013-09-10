package org.clueminer.wellmap;

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
public class ColorSchemeTest {

    private static double delta = 1e-9;

    public ColorSchemeTest() {
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
     * Test of getMax method, of class ColorScheme.
     */
    @Test
    public void testGetMax() {
    }

    /**
     * Test of getMid method, of class ColorScheme.
     */
    @Test
    public void testGetMid() {
    }

    /**
     * Test of getMin method, of class ColorScheme.
     */
    @Test
    public void testGetMin() {
    }

    /**
     * Test of getColor method, of class ColorScheme.
     */
    @Test
    public void testGetColor() {
    }

    /**
     * Test of getPalette method, of class ColorScheme.
     */
    @Test
    public void testGetPalette() {
    }

    /**
     * Test of setPalette method, of class ColorScheme.
     */
    @Test
    public void testSetPalette() {
    }

    /**
     * Test of updateColors method, of class ColorScheme.
     */
    @Test
    public void testUpdateColors() {
    }

    /**
     * Test of setRange method, of class ColorScheme.
     */
    @Test
    public void testSetRange() {
    }

    /**
     * Test of countMedian method, of class ColorScheme.
     *
     * @FIXME doesn't work on trais nor jenkins (without X server)
     */
    //@Test
    public void testCountMedian() {
        ColorScheme cs = new ColorScheme();
        assertEquals(0.0, cs.countMedian(-10, 10), delta);

    }
}