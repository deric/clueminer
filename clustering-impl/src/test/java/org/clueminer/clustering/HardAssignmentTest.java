package org.clueminer.clustering;

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
public class HardAssignmentTest {

    private HardAssignment subject;

    public HardAssignmentTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        subject = new HardAssignment();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testMembership() {
        assertEquals(0, subject.membership().length);
    }

    @Test
    public void testAssign() {
        subject.assign(1, 0);
        subject.assign(2, 1);
        subject.assign(3, 2);
        subject.assign(5, 1);

        assertEquals(8, subject.size());
        assertEquals(3, subject.distinct());
    }

    /**
     * Test of length method, of class HardAssignment.
     */
    @Test
    public void testLength() {
        assertEquals(0, subject.size());

        subject = new HardAssignment(5);
        assertEquals(5, subject.size());
    }

    /**
     * Test of distinct method, of class HardAssignment.
     */
    @Test
    public void testDistinct() {
        assertEquals(0, subject.distinct());

        subject = new HardAssignment(new int[]{1, 3, 5, 8});
        assertEquals(4, subject.size());
        assertEquals(4, subject.distinct());

    }

    @Test
    public void testAssigned() {
        subject.assign(0, 5);

        assertEquals(5, subject.assigned(0));
    }

}
