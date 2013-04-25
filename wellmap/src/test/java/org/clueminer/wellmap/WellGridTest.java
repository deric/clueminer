package org.clueminer.wellmap;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
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
public class WellGridTest {

    public WellGridTest() {
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
     * Test of translatePosition method, of class WellGrid.
     */
    @Test
    public void testTranslatePosition() {
        System.out.println("translatePosition");
        int x = 0;
        int y = 0;
        WellGrid instance = new WellGrid();
        int expResult = 0;
        //   int result = instance.translatePosition(x, y);
        //   assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //   fail("The test case is a prototype.");
    }

    /**
     * Test of numberToRowLabel method, of class WellGrid.
     */
    @Test
    public void testNumberToRowLabel() {
        System.out.println("numberToRowLabel");
        WellGrid instance = new WellGrid();
        //String result = instance.numberToRowLabel(row);
        assertEquals("A", instance.numberToRowLabel(0));
        assertEquals("Z", instance.numberToRowLabel(25));
        assertEquals("AA", instance.numberToRowLabel(26));

    }
}