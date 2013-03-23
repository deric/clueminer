package org.clueminer.attributes;

import org.clueminer.interpolation.InterpolationSearch;
import org.clueminer.math.Numeric;
import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author Tomas Barton
 */
public class InterpolationSearchTest {

    public InterpolationSearchTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of search method, of class InterpolationSearch.
     */
    @Test
    public void testSearch() {
        Numeric[] sortedArray = (Numeric[]) new TimePointAttribute[5];
        sortedArray[0] = (Numeric) new TimePointAttribute(0, 1, 1.5F);
        sortedArray[1] = (Numeric) new TimePointAttribute(1, 2, 2F);
        sortedArray[2] = (Numeric) new TimePointAttribute(2, 3, 5F);
        sortedArray[3] = (Numeric) new TimePointAttribute(3, 4, 10F);
        sortedArray[4] = (Numeric) new TimePointAttribute(4, 5, 15F);
        
        double toFind = 8.0F;
        int expResult = 3; //10
        int result = InterpolationSearch.search(sortedArray, toFind);
        assertEquals(expResult, result);
        
        result = InterpolationSearch.search(sortedArray, 1F);
        assertEquals(0, result);
        
        result = InterpolationSearch.search(sortedArray, 16F);
        assertEquals(4, result);
        
        result = InterpolationSearch.search(sortedArray, 1.5d);
        assertEquals(0, result);
    }

}