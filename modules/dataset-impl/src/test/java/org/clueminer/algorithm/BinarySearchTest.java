package org.clueminer.algorithm;

import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.math.Numeric;
import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author tom
 */
public class BinarySearchTest {

    private Numeric[] sortedArray;

    public BinarySearchTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        sortedArray = new TimePointAttribute[5];
        sortedArray[0] = new TimePointAttribute(0, 1, 1);
        sortedArray[1] = new TimePointAttribute(1, 2, 5.2);
        sortedArray[2] = new TimePointAttribute(2, 3, 8.5);
        sortedArray[3] = new TimePointAttribute(3, 4, 15);
        sortedArray[4] = new TimePointAttribute(4, 5, 22);

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of search method, of class BinarySearch.
     */
    @Test
    public void testSearch_NumericArr_double() {
        System.out.println("search");

        double toFind = 16.0;
        int expResult = 3;
        int result = BinarySearch.search(sortedArray, toFind);
        assertEquals(expResult, result);
    }

    /**
     * Test of search method, of class BinarySearch.
     */
    @Test
    public void testSearch_4args() {
        System.out.println("search");
        int low = 0;
        int high = 4;
        double toFind = 15.0;
        int expResult = 3;
        int result = BinarySearch.search(sortedArray, low, high, toFind);
        assertEquals(expResult, result);

        result = BinarySearch.search(sortedArray, low, high, -1);
        assertEquals(0, result);

        result = BinarySearch.search(sortedArray, low, high, 1.0);
        System.out.println("low = "+low+", high = "+high);
        assertEquals(0, result);

        result = BinarySearch.search(sortedArray, low, high, 8.45);
        System.out.println("low = "+low+", high = "+high);
        assertEquals(2, result);

        result = BinarySearch.search(sortedArray, low, high, 21.45);
        System.out.println("low = "+low+", high = "+high);
        assertEquals(4, result);

        result = BinarySearch.search(sortedArray, low, high, 22);
        System.out.println("low = "+low+", high = "+high);
        assertEquals(4, result);
        
        //search for a number outside of array's range
        result = BinarySearch.search(sortedArray, low, high, 23);
        assertEquals(4, result);
    }
}