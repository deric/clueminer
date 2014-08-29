package org.clueminer.clustering.aggl;

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
public class ElementTest {

    private Element subject;
    private static double delta = 1e-9;
    public ElementTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        subject = new Element(2.0, 1, 1);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of compareTo method, of class Element.
     */
    @Test
    public void testCompareTo() {
        Element e1 = new Element(1.0, 0, 0);
        Element e2 = new Element(1.0, 0, 1);
        //should be identical
        assertEquals(0, e1.compareTo(e2));

        e2 = new Element(3.0, 0, 1);
        //e2 is bigger
        assertEquals(-1, e1.compareTo(e2));
    }

    /**
     * Test of getValue method, of class Element.
     */
    @Test
    public void testGetValue() {
        assertEquals(2.0, subject.getValue(), delta);
    }

    /**
     * Test of setValue method, of class Element.
     */
    @Test
    public void testSetValue() {
        subject.setValue(5.0);
        assertEquals(5.0, subject.getValue(), delta);
    }

    /**
     * Test of getRow method, of class Element.
     */
    @Test
    public void testGetRow() {
        assertEquals(1, subject.getRow());
    }

    /**
     * Test of setRow method, of class Element.
     */
    @Test
    public void testSetRow() {
        subject.setRow(5);
        assertEquals(5, subject.getRow());
    }

    /**
     * Test of getColumn method, of class Element.
     */
    @Test
    public void testGetColumn() {
        assertEquals(1, subject.getColumn());
    }

    /**
     * Test of setColumn method, of class Element.
     */
    @Test
    public void testSetColumn() {
        subject.setColumn(8);
        assertEquals(8, subject.getColumn());
    }

}
