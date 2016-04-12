package org.clueminer.importer.impl;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class InstanceDraftImplTest {

    private InstanceDraftImpl subject;
    private DraftContainer container;
    private static double DELTA = 1e-9;

    @Before
    public void setUp() {
        container = new DraftContainer();
        subject = new InstanceDraftImpl(container);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setLabel method, of class InstanceDraftImpl.
     */
    @Test
    public void testSetLabel() {
        String label = "foo";
        subject.setLabel(label);
        assertEquals(label, subject.getLabel());
    }

    /**
     * Test of getId method, of class InstanceDraftImpl.
     */
    @Test
    public void testGetId() {
        String id = "123";
        subject.setId(id);
        assertEquals(id, subject.getId());
    }

    /**
     * Test of size method, of class InstanceDraftImpl.
     */
    @Test
    public void testSize() {
        assertEquals(0, subject.size());
    }

    @Test
    public void testIsEmpty() {
        assertEquals(true, subject.isEmpty());
        subject.put(1.0);
        assertEquals(false, subject.isEmpty());
    }

    /**
     * Test of getValue method, of class InstanceDraftImpl.
     */
    @Test
    public void testGetValue_String() {
    }

    /**
     * Test of setValue method, of class InstanceDraftImpl.
     */
    @Test
    public void testSetValue_String_Object() {
        for (int i = 0; i < 5; i++) {
            subject.set(i, i + 10);
            assertEquals(i + 10, subject.get(i), DELTA);
        }
    }

    /**
     * Test of setValue method, of class InstanceDraftImpl.
     */
    @Test
    public void testSetValue_int_Object() {
    }

    /**
     * Test of getValue method, of class InstanceDraftImpl.
     */
    @Test
    public void testGetValue_int() {
    }

}
