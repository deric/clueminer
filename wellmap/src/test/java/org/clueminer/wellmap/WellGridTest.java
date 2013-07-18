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
        WellGrid instance = new WellGrid();
        instance.setDimensions(8, 12);
        assertEquals(-1, instance.translatePosition(0, 0));
        assertEquals(0, instance.wellPosToId(0, 0));
        assertEquals(1, instance.wellPosToId(1, 0));
        assertEquals(2, instance.wellPosToId(2, 0));
        assertEquals(12, instance.wellPosToId(0, 1));
        assertEquals(95, instance.wellPosToId(11, 7));
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