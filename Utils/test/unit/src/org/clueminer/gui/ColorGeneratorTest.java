package org.clueminer.gui;

import java.awt.Color;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tomas Barton
 */
public class ColorGeneratorTest {

    public ColorGeneratorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getRandomColor method, of class ColorGenerator.
     */
    @Test
    public void testGetRandomColor() {
        Color c1 = ColorGenerator.getRandomColor();
        Color c2 = ColorGenerator.getRandomColor();
        assertNotSame(c1, c2);
    }

    /**
     * Test of getTransparentColor method, of class ColorGenerator.
     */
    @Test
    public void testGetTransparentColor() {
        ColorGenerator.getTransparentColor(Color.yellow, 5);
    }
    

    /**
     * Test of getBrightColor method, of class ColorGenerator.
     */
    @Test
    public void testGetBrightColor() {
        Color c = ColorGenerator.getBrightColor();
        System.out.println("bright= "+c.toString());
        assertNotNull(c);
    }
    
    
        @Test
    public void testGetHexColor() {
        String c =ColorGenerator.getHexColor();
        //6 numbers + starting #
        assertEquals(7, c.length());
        System.out.println("hex= "+c);
    }

}