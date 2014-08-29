package org.clueminer.eval.utils;

import org.clueminer.eval.utils.ValueComparableMap;
import com.google.common.collect.Ordering;
import java.util.TreeMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tombart
 */
public class ValueComparableMapTest {

    public ValueComparableMapTest() {
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
     * Test of put method, of class ValueComparableMap.
     */
    @Test
    public void testPut() {
        TreeMap<String, Integer> map = new ValueComparableMap<String, Integer>(Ordering.natural());
        map.put("a", 5);
        map.put("b", 1);
        map.put("c", 3);
        assertEquals("b", map.firstKey());
        assertEquals("a", map.lastKey());
        map.put("d", 0);
        assertEquals("d", map.firstKey());
        //ensure it's still a map (by overwriting a key, but with a new value) 
        map.put("d", 2);
        assertEquals("b", map.firstKey());
        //Ensure multiple values do not clobber keys
        map.put("e", 2);
        assertEquals(5, map.size());
        assertEquals(2, (int) map.get("e"));
        assertEquals(2, (int) map.get("d"));
        System.out.println("map :" +map);

    }
}