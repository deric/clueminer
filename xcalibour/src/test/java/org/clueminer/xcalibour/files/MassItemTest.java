package org.clueminer.xcalibour.files;

import org.clueminer.xcalibour.data.MassItem;
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
public class MassItemTest {

    private MassItem item;
    private long intensity;
    private double mass;
    private double total_intensity;
    private double delta_mass = 1e-9;
    /**
     * highest precision we can get from float
     */
    private float delta_float = 1e-6f;

    public MassItemTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        intensity = (long) 15417.000000;
        mass = 30.323959;
        total_intensity = 528439.000000;
        item = new MassItem(intensity, mass, total_intensity);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getIntensity method, of class MassItem.
     */
    @Test
    public void testGetIntensity() {
        assertEquals(intensity, item.getIntensity());
    }

    /**
     * Test of setIntensity method, of class MassItem.
     */
    @Test
    public void testSetIntensity() {
        long inten = 456123l;
        item.setIntensity(inten);
        assertEquals(inten, item.getIntensity());
    }

    /**
     * Test of getMass method, of class MassItem.
     */
    @Test
    public void testGetMass() {
        assertEquals(mass, item.getMass(), delta_mass);
    }

    /**
     * Test of setMass method, of class MassItem.
     */
    @Test
    public void testSetMass() {
        double m = 31.285080;
        item.setMass(m);
        assertEquals(m, item.getMass(), delta_mass);
    }

    /**
     * Test of getTotal_intensity method, of class MassItem.
     */
    @Test
    public void testGetTotal_intensity() {
    }

    /**
     * Test of setTotalIntensity method, of class MassItem.
     */
    @Test
    public void testSetTotalIntensity() {
    }

    /**
     * Test of intValue method, of class MassItem.
     */
    @Test
    public void testIntValue() {
    }

    /**
     * Test of longValue method, of class MassItem.
     */
    @Test
    public void testLongValue() {
    }

    /**
     * Test of floatValue method, of class MassItem.
     */
    @Test
    public void testFloatValue() {
        assertEquals(mass, item.floatValue(), delta_float);
    }

    /**
     * Test of doubleValue method, of class MassItem.
     */
    @Test
    public void testDoubleValue() {
        assertEquals(mass, item.doubleValue(), delta_mass);
    }

    /**
     * Test of getValue method, of class MassItem.
     */
    @Test
    public void testGetValue() {
        //should return mass value
        assertEquals(mass, item.getValue(), delta_mass);
    }

    /**
     * Test of compareTo method, of class MassItem.
     */
    @Test
    public void testCompareTo_double() {
        assertEquals(0, item.compareTo(mass));
        double bigger = 31.285080;
        assertEquals(-1, item.compareTo(bigger));
        double smaller = 28.285080;
        assertEquals(1, item.compareTo(smaller));
    }

    /**
     * Test of compareTo method, of class MassItem.
     */
    @Test
    public void testCompareTo_Numeric() {
    }

    /**
     * Test of equals method, of class MassItem.
     */
    @Test
    public void testEquals() {
        MassItem clone = new MassItem(item.getIntensity(), item.getMass(), item.getTotalIntensity());
        assertEquals(item, clone);
        clone.setMass(1234.123);
        assertFalse(item.equals(clone));
    }

    @Test
    public void testEquals2() {
        MassItem clone = new MassItem(item.getIntensity(), item.getMass(), item.getTotalIntensity());
        assertEquals(item, clone);
        clone.setIntensity(456489l);
        assertFalse(item.equals(clone));
    }

    @Test
    public void testEquals3() {
        MassItem clone = new MassItem(item.getIntensity(), item.getMass(), item.getTotalIntensity());
        assertEquals(item, clone);
        clone.setTotalIntensity(54646);
        /**
         * currently total_intensity is not used for determining identity of
         * objects
         */
        assertTrue(item.equals(clone));
    }

    /**
     * Test of hashCode method, of class MassItem.
     */
    @Test
    public void testHashCode() {
        MassItem clone = new MassItem(item.getIntensity(), item.getMass(), item.getTotalIntensity());
        assertEquals(item.hashCode(), clone.hashCode());
        
        clone.setIntensity(456489l);
        assertNotSame(item.hashCode(), clone.hashCode());
        
    }
}