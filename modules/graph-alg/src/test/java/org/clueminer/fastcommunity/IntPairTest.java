package org.clueminer.fastcommunity;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Hamster
 */
public class IntPairTest {

    public IntPairTest() {
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
     * Test of getFirst method, of class IntPair.
     */
    @Test
    public void testGetFirst() {
        System.out.println("getFirst");
        IntPair a = new IntPair(1, 2);
        IntPair b = new IntPair(2, 1);
        IntPair c = new IntPair(1, 2);
        assertEquals(true, a.equals(c));
        assertEquals(false, a.equals(b));
    }

}
